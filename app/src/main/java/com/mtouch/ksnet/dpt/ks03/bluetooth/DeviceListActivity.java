package com.mtouch.ksnet.dpt.ks03.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mtouch.ksnet.dpt.ks03.pay.Constants;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeviceListActivity extends Activity {
    private static final long SCAN_PERIOD = 2000;
    public static final String TAG = "DeviceListActivity";
    final int FinishListActivity = -3;
    final int NoKSNETReader = -2;
    final int UserReaderFindCancel = -4;
    Button btnCancel;
    Map<String, Integer> devRssiValues;
    private DeviceAdapter deviceAdapter;
    List<BluetoothDevice> deviceList;
    /* access modifiers changed from: private */
    public BluetoothAdapter mBluetoothAdapter;
    View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (view.getId() == R.id.btnCancel) {
                DeviceListActivity.this.setResult(-4, new Intent());
                DeviceListActivity.this.finish();
            }
        }
    };
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            BluetoothDevice bluetoothDevice = DeviceListActivity.this.deviceList.get(i);
            DeviceListActivity.this.mBluetoothAdapter.stopLeScan(DeviceListActivity.this.mLeScanCallback);
            Bundle bundle = new Bundle();
            bundle.putString("android.bluetooth.device.extra.DEVICE", DeviceListActivity.this.deviceList.get(i).getAddress());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            DeviceListActivity.this.setResult(-1, intent);
            DeviceListActivity.this.finish();
        }
    };
    private TextView mEmptyList;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice bluetoothDevice, final int i, byte[] bArr) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    DeviceListActivity.this.addDevice(bluetoothDevice, i);
                }
            }, 10);
        }
    };
    private boolean mScanning;
    private ServiceConnection onService = null;
    SharedPreferences pref;
    SharedPreferences.Editor prvEditor;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("DeviceListActivity", "onCreate");
        requestWindowFeature(1);
        setContentView(R.layout.devicel_list_activity);
        getWindow().getAttributes().width = (int) (((double) ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getWidth()) * 0.7d);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = 17;
        attributes.y = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        this.mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        this.mBluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.pref = getSharedPreferences("Variable", 0);
        this.prvEditor = this.pref.edit();
        populateList();
        this.btnCancel = (Button) findViewById(R.id.btnCancel);
        this.btnCancel.setOnClickListener(this.mClickListener);
    }

    private void populateList() {
        this.deviceList = new ArrayList();
        BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        String string = this.pref.getString(Constants.KEY_MAC_ADDRESS, "NONE");
        Log.d("DeviceListActivity", "저장된 리더기 MAC_Adress: " + string);
        this.devRssiValues = new HashMap();
        this.deviceAdapter = new DeviceAdapter(this, this.deviceList);
        if (this.pref.getString(Constants.KEY_MAC_ADDRESS, "NONE").equals("NONE")) {
            setResult(-2, (Intent) null);
            finish();
            return;
        }
        scanLeDevice(true);
    }

    private void scanLeDevice(boolean z) {
        this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
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
            if (bluetoothDevice.getName() != null) {
                String string = this.pref.getString(Constants.KEY_MAC_ADDRESS, "NONE");
                if (!bluetoothDevice.getName().equals("KSNET_BTIC0")) {
                    return;
                }
                if (string.equals("NOREGIST") || bluetoothDevice.getAddress().equals(string)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("android.bluetooth.device.extra.DEVICE", bluetoothDevice.getAddress());
                    this.prvEditor.putString(Constants.KEY_MAC_ADDRESS, bluetoothDevice.getAddress());
                    this.prvEditor.commit();
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(-1, intent);
                    finish();
                }
            }
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
        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
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
            TextView textView = (TextView) viewGroup2.findViewById(R.id.name);
            TextView textView2 = (TextView) viewGroup2.findViewById(R.id.paired);
            ((TextView) viewGroup2.findViewById(R.id.rssi)).setVisibility(View.VISIBLE);
            textView.setText(bluetoothDevice.getName());
            ((TextView) viewGroup2.findViewById(R.id.address)).setText(bluetoothDevice.getAddress());
            bluetoothDevice.getBondState();
            textView.getText().equals("KSNET_BTIC0");
            return viewGroup2;
        }
    }

    private void showMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}