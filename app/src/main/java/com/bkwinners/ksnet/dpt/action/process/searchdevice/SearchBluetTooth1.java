package com.bkwinners.ksnet.dpt.action.process.searchdevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.bkwinners.ksnet.dpt.action.PayResultActivity;
import com.bkwinners.ksnet.dpt.action.imp.BlueToothCompleteListener;
import com.pswseoul.comunity.library.BataTime;
import com.pswseoul.comunity.library.BataTimeCallback;
import com.pswseoul.comunity.library.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Created by parksuwon on 2018-02-08.
 */

public class SearchBluetTooth1 {

    public PayResultActivity act ;
    public BlueToothCompleteListener callback;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    List<BluetoothDevice> deviceList;
    HashMap<String , String> map = new HashMap<>();
    public String BTName = "";
    BluetoothManager bluetoothManager = null;

    private static final long SCAN_PERIOD = 10000; //scanning for 10 seconds


    public SearchBluetTooth1(PayResultActivity cxt, BlueToothCompleteListener callback){
        this.callback  = callback;
        this.act = act;
        if (!cxt.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

        }
        // Register for broadcasts when a device is discovered
    //    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    //    act.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
    //    filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
     //   act.registerReceiver(mReceiver, filter);
        addFilter();
    }

    public void getBlutToothAddress (String BTName) {
        map.clear();
        this.BTName = BTName;

        map.put("name" , BTName);

        if(bluetoothManager == null) {
            bluetoothManager =
                    (BluetoothManager) act.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            return;
        }

        if(mHandler == null)  mHandler = new Handler();

        scanLeDevice(true);
        TimerCheck(10000);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
    }


    // park
    public void TimerCheck(int timeMiliseconds){
        if(timeMiliseconds == 0 )   timeMiliseconds = 2000;
        final BataTime basetime = new BataTime(timeMiliseconds);
        basetime.start(new BataTimeCallback() {
            @Override
            public void onUpdate(int elapsed) {
                Logger.d("TAG", "On update called...time elapsed = "+elapsed );

                if(deviceList != null && deviceList.size() > 0) {
                    for (BluetoothDevice listDev : deviceList) {
                        Logger.d("TAG",  "Device  : " + listDev.getAddress() + "/" + listDev.getName() + "/" + listDev.getType() + "/" + listDev.getUuids());
                        if(listDev.getName() != null) {
                            if (listDev.getName().indexOf(BTName) >= 0) {
                                basetime.stop();
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                callback.onBlueToothComplete(map);
                            }
                        }
                    }
                }
            }
            @Override
            public void onComplete() {
                Logger.d("TAG", "On complete called..." );

                if(deviceList != null && deviceList.size() > 0) {
                    for (BluetoothDevice listDev : deviceList) {
                        Logger.d("TAG", "Device  : " + listDev.getAddress() + "/" + listDev.getName() + "/" + listDev.getType() + "/" + listDev.getUuids());
                        if(listDev.getName() != null) {
                            if (listDev.getName().indexOf(BTName) >= 0) {
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                callback.onBlueToothComplete(map);
                                break;
                            } // listDev
                        }  //if listDev
                    }  // for
                }  // if devicelist

                /* 이곳은 찾지못한 것입니다 */
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        });
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addDevice(device,rssi);
                        }
                    });
                }
            };

    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;

        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }
        if(!deviceFound){
         deviceList.add(device);
        }
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("tag", "============BroadcastReceiver=================" + action );
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }
        }
    };

    public void addFilter(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }


}
