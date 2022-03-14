package com.bkwinners.ksnet.dpt.design;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.bkwinners.ksnet.dpt.design.appToApp.network.APIService;
import com.bkwinners.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.Request;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompleteCheckService extends Service {

    private static final int MAX_RETRY_COUNT = 5;

    private APIService apiService;
    private int retryCount = 0;

    public CompleteCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        apiService = ApiUtils.getAPIService();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = String.valueOf(System.currentTimeMillis());
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("MTOUCH")
                    .setContentText("결제완료체크중")
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");

        try {
            sendPushData(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    private void sendPushData(final Intent intent)  throws Exception {
        HashMap<String, String> paymentMap = (HashMap<String, String>) intent.getSerializableExtra("payment");

        Request req = new Request();
        req.data.put("van", intent.getStringExtra("van"));
        req.data.put("vanId", intent.getStringExtra("vanId"));
        req.data.put("vanTrxId", intent.getStringExtra("vanTrxId"));
        req.data.put("amount", intent.getStringExtra("amount"));
        req.data.put("regDate", intent.getStringExtra("regDate"));
        req.data.put("authCd", intent.getStringExtra("authCd"));
        req.data.put("trackId", intent.getStringExtra("trackId"));
        if(intent.getStringExtra("trxId")!=null)
            req.data.put("trxId", intent.getStringExtra("trxId"));
        req.data.put("type", intent.getStringExtra("type"));
        req.data.put("number", intent.getStringExtra("number"));

        if(intent.getStringExtra("installment")!=null)
            req.data.put("installment", intent.getStringExtra("installment"));

        if(intent.getStringExtra("prodQty")!=null)
            req.data.put("prodQty", intent.getStringExtra("prodQty"));

        if(intent.getStringExtra("prodDesc")!=null)
            req.data.put("prodDesc", intent.getStringExtra("prodDesc"));

        if(intent.getStringExtra("prodName")!=null)
            req.data.put("prodName", intent.getStringExtra("prodName"));

        if(intent.getStringExtra("prodPrice")!=null)
            req.data.put("prodPrice", intent.getStringExtra("prodPrice"));

        if(intent.getStringExtra("payerName")!=null)
            req.data.put("payerName", intent.getStringExtra("payerName"));

        if(intent.getStringExtra("payerEmail")!=null)
            req.data.put("payerEmail", intent.getStringExtra("payerEmail"));

        if(intent.getStringExtra("payerTel")!=null)
            req.data.put("payerTel", intent.getStringExtra("payerTel"));


        //분리정산
        //walletSettle
        //dealerId
        //dealerRate
        //distRate
        if(intent.getStringExtra("walletSettle")!=null)
            req.data.put("walletSettle", intent.getStringExtra("walletSettle"));
        if(intent.getStringExtra("dealerId")!=null)
            req.data.put("dealerId", intent.getStringExtra("dealerId"));
        if(intent.getStringExtra("distId")!=null)
            req.data.put("distId", intent.getStringExtra("distId"));
        if(intent.getStringExtra("dealerRate")!=null)
            req.data.put("dealerRate", intent.getStringExtra("dealerRate"));
        if(intent.getStringExtra("distRate")!=null)
            req.data.put("distRate", intent.getStringExtra("distRate"));



        apiService.getApproveComplete(paymentMap.get("key"), req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                } else {

                }


                stopForeground(true);
                stopSelf();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (retryCount < MAX_RETRY_COUNT) {
                    try {
                        sendPushData(intent);
                    } catch (Exception e) {
                    }
                } else {
                    stopForeground(true);
                    stopSelf();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
        Log.d("CompleteTest", "====================== 서비스 종료 ======================");
    }


}
