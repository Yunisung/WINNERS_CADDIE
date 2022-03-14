package com.bkwinners.ksnet.dpt.ks03.pay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class CompleteCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CompleteTest", "====================== 리시버 실행 ======================");


        // RingtonePlayingService 서비스 intent 생성
        Intent service_intent = new Intent(context, CompleteCheckService.class);
        service_intent.putExtras(intent.getExtras());

        long alarmStartTime = service_intent.getLongExtra("alarmTime",System.currentTimeMillis());

        if((System.currentTimeMillis() - alarmStartTime) > (20 * 1000)){

        }else{

            new Handler().postDelayed(()->{
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(service_intent);
                } else {
                    context.startService(service_intent);
                }

            },7000);
            return;
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(service_intent);
        } else {
            context.startService(service_intent);
        }
    }
}
