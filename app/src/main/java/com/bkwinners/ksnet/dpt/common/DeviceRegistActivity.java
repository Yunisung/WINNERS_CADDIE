package com.bkwinners.ksnet.dpt.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeviceRegistActivity extends Activity {
    private static final long SCAN_PERIOD = 100000;
    public static final String TAG = "DeviceListActivity";
    Map<String, Integer> devRssiValues;
    /* access modifiers changed from: private */
    public DeviceAdapter deviceAdapter;
    List<BluetoothDevice> deviceList;
    /* access modifiers changed from: private */
    public BluetoothLeScanner mBLEScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
            LinearLayout linearLayout = (LinearLayout) View.inflate(DeviceRegistActivity.this, R.layout.ble_password, (ViewGroup) null);
            mEditPWD = (EditText) linearLayout.findViewById(R.id.pwd);
            new AlertDialog.Builder(DeviceRegistActivity.this).setTitle("비밀번호를 입력해주세요").setView(linearLayout).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!"123456".equals(mEditPWD.getText().toString())) {
                        Toast.makeText(DeviceRegistActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    } else if (deviceList.get(i).getName() == null) {
                        Toast.makeText(DeviceRegistActivity.this, "KSNET_BTIC0를 선택해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("android.bluetooth.device.extra.DEVICE", deviceList.get(i).getAddress());
                        String address = deviceList.get(i).getAddress();
                        SharedPreferenceUtil.putData(DeviceRegistActivity.this, Constants.KEY_MAC_ADDRESS, address);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        setResult(-1, intent);
                        finish();
                    }
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).show();
        }
    };
    EditText mEditPWD;
    private TextView mEmptyList;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public ScanCallback mScanCallback = new ScanCallback() {
        public void onScanFailed(int i) {
        }

        public void onScanResult(int i, ScanResult scanResult) {
            processResult(scanResult);
        }

        public void onBatchScanResults(List<ScanResult> list) {
            for (ScanResult processResult : list) {
                processResult(processResult);
            }
        }

        private void processResult(final ScanResult scanResult) {
            runOnUiThread(new Runnable() {
                public void run() {
                    addDevice(scanResult.getDevice(), 1);
                    deviceAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    List<ScanFilter> mScanFilter;
    ScanSettings mScanSettings;
    /* access modifiers changed from: private */
    public boolean mScanning;
    private ServiceConnection onService = null;
    SharedPreferences pref;
    SharedPreferences.Editor prvEditor;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("DeviceListActivity", "onCreate");
        requestWindowFeature(1);
        setContentView(R.layout.device_reg_list);
        Display defaultDisplay = ((WindowManager) getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay();
        getWindow().getAttributes().width = (int) (((double) defaultDisplay.getWidth()) * 0.9d);
        getWindow().getAttributes().height = (int) (((double) defaultDisplay.getHeight()) * 0.9d);
        getWindow().clearFlags(2);
        this.mHandler = new Handler();
        ScanFilter build = new ScanFilter.Builder().build();
        this.mScanFilter = new ArrayList();
        this.mScanFilter.add(build);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.mScanSettings = new ScanSettings.Builder().setScanMode(0).setReportDelay(0).setCallbackType(1).build();
        }else{
            Toast.makeText(this, "디바이스 버전이 낮습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        this.mBluetoothAdapter = ((BluetoothManager) getSystemService(Service.BLUETOOTH_SERVICE)).getAdapter();
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            finish();
            return;
        }
        this.mBLEScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, 0).show();
            finish();
            return;
        }
        populateList();
        ((Button) findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!mScanning) {
                    scanLeDevice(true);
                    return;
                }
                setResult(-4, new Intent());
                finish();
            }
        });
    }

    private void populateList() {
        Log.d("DeviceListActivity", "populateList");
        this.deviceList = new ArrayList();
        this.deviceAdapter = new DeviceAdapter(this, this.deviceList);
        this.devRssiValues = new HashMap();
        ListView listView = (ListView) findViewById(R.id.new_devices);
        listView.setAdapter(this.deviceAdapter);
        listView.setOnItemClickListener(this.mDeviceClickListener);
        scanLeDevice(true);
    }

    /* access modifiers changed from: private */
    public void scanLeDevice(boolean z) {
        final Button button = (Button) findViewById(R.id.btn_cancel);
        if (z) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    boolean unused = mScanning = false;
                    mBLEScanner.stopScan(mScanCallback);
                    button.setText(R.string.scan);
                }
            }, SCAN_PERIOD);
            this.mScanning = true;
            this.mBLEScanner.startScan(this.mScanCallback);
            button.setText(R.string.cancel);
            return;
        }
        this.mScanning = false;
        this.mBLEScanner.stopScan(this.mScanCallback);
        button.setText(R.string.scan);
    }

    /* access modifiers changed from: private */
    public void addDevice(BluetoothDevice bluetoothDevice, int i) {
        boolean z;
        Iterator<BluetoothDevice> it = this.deviceList.iterator();
        while (true) {
            if (it.hasNext()) {
                if (it.next().getAddress().equals(bluetoothDevice.getAddress())) {
                    z = true;
                    break;
                }
            } else {
                z = false;
                break;
            }
        }
        this.devRssiValues.put(bluetoothDevice.getAddress(), Integer.valueOf(i));
        if (!z) {
            this.deviceList.add(bluetoothDevice);
            this.deviceAdapter.notifyDataSetChanged();
        }
    }

    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.device.action.FOUND");
        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
    }

    public void onStop() {
        super.onStop();
        this.mBLEScanner.stopScan(this.mScanCallback);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mScanning = false;
        this.mBLEScanner.stopScan(this.mScanCallback);
    }

    public void onBackPressed() {
        super.onBackPressed();
        setResult(-4, new Intent());
        finish();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
    }

    class DeviceAdapter extends BaseAdapter {
        Context context;
        List<BluetoothDevice> devices;
        LayoutInflater inflater;

        public long getItemId(int i) {
            return (long) i;
        }

        public DeviceAdapter(Context context2, List<BluetoothDevice> list) {
            this.context = context2;
            this.inflater = LayoutInflater.from(context2);
            this.devices = list;
        }

        public int getCount() {
            return this.devices.size();
        }

        public Object getItem(int i) {
            return this.devices.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewGroup viewGroup2;
            if (view != null) {
                viewGroup2 = (ViewGroup) view;
            } else {
                viewGroup2 = (ViewGroup) this.inflater.inflate(R.layout.device_element, (ViewGroup) null);
            }
            BluetoothDevice bluetoothDevice = this.devices.get(i);
            TextView textView = (TextView) viewGroup2.findViewById(R.id.address);
            TextView textView2 = (TextView) viewGroup2.findViewById(R.id.name);
            TextView textView3 = (TextView) viewGroup2.findViewById(R.id.paired);
            TextView textView4 = (TextView) viewGroup2.findViewById(R.id.rssi);
            textView4.setVisibility(View.VISIBLE);
            byte intValue = (byte) devRssiValues.get(bluetoothDevice.getAddress()).intValue();
            if (intValue != 0) {
                textView4.setText("Rssi = " + String.valueOf(intValue));
            }
            textView2.setText(bluetoothDevice.getName());
            textView.setText(bluetoothDevice.getAddress());
            if (bluetoothDevice.getBondState() == 12) {
                Log.i("DeviceListActivity", "device::" + bluetoothDevice.getName());
                textView2.setTextColor(-1);
                textView.setTextColor(-1);
                textView3.setTextColor(-7829368);
                textView3.setVisibility(View.VISIBLE);
                textView3.setText(R.string.paired);
                textView4.setVisibility(View.VISIBLE);
                textView4.setTextColor(-1);
            } else {
                textView2.setTextColor(-1);
                textView.setTextColor(-1);
                textView3.setVisibility(View.GONE);
                textView4.setVisibility(View.VISIBLE);
                textView4.setTextColor(-1);
            }
            return viewGroup2;
        }
    }
}