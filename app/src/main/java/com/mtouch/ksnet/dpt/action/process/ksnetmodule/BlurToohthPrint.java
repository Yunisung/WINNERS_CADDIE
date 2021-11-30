package com.mtouch.ksnet.dpt.action.process.ksnetmodule;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.Toast;

import com.mtouch.ksnet.dpt.Toasty;

import zj.com.cn.bluetooth.sdk.BluetoothService;

/**
 * Created by parksuwon on 2018-02-08.
 */

public class BlurToohthPrint {
    Activity act ;
    String BlurToohthPrn;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;

    public BlurToohthPrint(Activity act) {
        this.act = act;
    }

    public void BtConnect() {
    }

    public void BTPrinter(String BT_PRN_ADDRESS  ) {
//        if(KSNETStatus.BT_PRN_FLAG) return;
        if((BT_PRN_ADDRESS != null) && (BT_PRN_ADDRESS.length() > 0)) {  // 바로 접솝합니다
            if (BluetoothAdapter.checkBluetoothAddress(BT_PRN_ADDRESS ) ) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(BT_PRN_ADDRESS );
                // Attempt to connect to the device
                mService.connect(device);
                Toasty.info(act, "프린터를 연결하고 있습니다", Toast.LENGTH_LONG);
                // custom_toast("프린터 연결중입니다......");
            }
        }
    }

    public int getBTPrnConnectFlag(){
       return  mService.getState();
    }

}
