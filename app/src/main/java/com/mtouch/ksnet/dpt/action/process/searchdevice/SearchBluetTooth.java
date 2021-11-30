package com.mtouch.ksnet.dpt.action.process.searchdevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.mtouch.ksnet.dpt.action.PayResultActivity;
import com.mtouch.ksnet.dpt.action.imp.BlueToothCompleteListener;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by parksuwon on 2018-02-08.
 */

public class SearchBluetTooth {

    public PayResultActivity act;
    public BlueToothCompleteListener callback;
    private BluetoothAdapter mBluetoothAdapter;
    HashMap<String, String> map = new HashMap<>();
    public String BTName = "";

    public SearchBluetTooth() {
    }


    public SearchBluetTooth(PayResultActivity cxt, BlueToothCompleteListener callback) {
        this.callback = callback;
        this.act = act;
    }

    public HashMap<String, String> getBlutToothAddress(String BTName) {
        map.clear();
        this.BTName = BTName;

        map.put("name", BTName);
        map.put("resultcd", "0");
        map.put("resultmsg", "블루투스가 비활성화 되어 있습니다");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            map.put("resultcd", "0");
            map.put("resultmsg", "연결된 블루투스가 없습니다");

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().indexOf(BTName) >= 0) {
                        map.put("resultcd", "1");
                        map.put("devicename", device.getName());
                        map.put("deviceaddress", device.getAddress());
                        map.put("resultmsg", "해당블루투스를 찾았습니다");
                    }
                }
            } else {
                map.put("resultcd", "0");
                map.put("resultmsg", "연결된 블루투스가 없습니다");
            }
        }
        return map;
    }

}
