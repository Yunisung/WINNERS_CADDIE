package com.mtouch.ksnet.dpt.ks03.bluetooth;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.content.pm.ActivityInfo;
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

import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.ksnet.dpt.action.process.util.LOG;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DeviceRegistActivity extends AppCompatActivity {
    private static final long SCAN_PERIOD = 100000;
    public static final String TAG = "DeviceRegistActivity";
    Map<String, Integer> devRssiValues;
    /* access modifiers changed from: private */
    public DeviceAdapter deviceAdapter;
    List<BluetoothDevice> deviceList;
    /* access modifiers changed from: private */
    public BluetoothLeScanner mBLEScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long j) {
            if (deviceList.get(i).getName() == null) {
                Toast.makeText(DeviceRegistActivity.this, "KSNET_BTIC0를 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

                LinearLayout linearLayout = (LinearLayout) View.inflate(DeviceRegistActivity.this, R.layout.ble_password, (ViewGroup) null);
            mEditPWD = (EditText) linearLayout.findViewById(R.id.pwd);
            new AlertDialog.Builder(DeviceRegistActivity.this).setTitle("비밀번호를 입력해주세요").setIcon(R.drawable.ico_help).setView(linearLayout).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    if (!"123456".equals(mEditPWD.getText().toString())) {
                        Toast.makeText(DeviceRegistActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    } else if (deviceList.get(i).getName() == null) {
                        Toast.makeText(DeviceRegistActivity.this, "KSNET_BTIC0를 선택해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DeviceRegistActivity.this, "리더기가 등록되었습니다.",Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("android.bluetooth.device.extra.DEVICE", deviceList.get(i).getAddress());
                        String address = deviceList.get(i).getAddress();
                        SharedPreferenceUtil.putData(DeviceRegistActivity.this, Constants.KEY_MAC_ADDRESS, address);
                        Intent intent = new Intent();
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
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
        @Override
        public void onScanFailed(int i) {
            LOG.d("");
        }
        @Override
        public void onScanResult(int i, ScanResult scanResult) {
            LOG.d("scanResult: "+scanResult);
            processResult(scanResult);
        }
        @Override
        public void onBatchScanResults(List<ScanResult> list) {

            for (ScanResult processResult : list) {
                LOG.d("scanResult:"+processResult);
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

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }catch (Exception e){}

        Log.d(TAG, "onCreate");
        requestWindowFeature(1);
        setContentView(R.layout.device_reg_list);
        Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        getWindow().getAttributes().width = (int) (((double) defaultDisplay.getWidth()) * 0.9d);
        getWindow().getAttributes().height = (int) (((double) defaultDisplay.getHeight()) * 0.9d);
        getWindow().clearFlags(2);
        this.mHandler = new Handler();
        ScanFilter build = new ScanFilter.Builder().build();
        this.mScanFilter = new ArrayList();
        this.mScanFilter.add(build);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.mScanSettings = new ScanSettings.Builder().setScanMode(0).setReportDelay(0).setCallbackType(1).build();
        }
        if (!getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        this.mBluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null) {
            finish();
            return;
        }
        this.mBLEScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "populateList");
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
            if(bluetoothDevice.getName()!=null && bluetoothDevice.getName().contains("KSNET")){
                viewGroup2.setBackgroundResource(R.color.black77);
            }

            if (bluetoothDevice.getBondState() == 12) {
                Log.i(TAG, "device::" + bluetoothDevice.getName());
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