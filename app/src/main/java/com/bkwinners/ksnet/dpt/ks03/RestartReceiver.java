package com.bkwinners.ksnet.dpt.ks03;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.nordicsemi.nrfUARTv2.UartService;

public class RestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            Log.w("BootReceiver", "Broadcast Action: "+intent.getAction());

            if("true".equals(SharedPreferenceUtil.getData(context, Constants.KEY_KEEP_CONNECTION,"false"))) {

//                new Handler().postDelayed(()->{
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                        context.startForegroundService(new Intent(context, UartService.class));
//                    }else{
//                        context.startService(new Intent(context, UartService.class));
//                    }
//                },1000);

                Log.w("BootReceiver", "@@@@@@@@@@@@@@@@@@@@ Service loaded at start..");
            }
        }

    }
}
