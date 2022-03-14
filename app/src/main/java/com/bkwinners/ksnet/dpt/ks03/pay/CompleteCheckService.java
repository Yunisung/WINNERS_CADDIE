package com.bkwinners.ksnet.dpt.ks03.pay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.bkwinners.ksnet.dpt.action.PayResultActivity;
import com.bkwinners.ksnet.dpt.design.appToApp.network.APIService;
import com.bkwinners.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.Request;

import java.util.HashMap;

import okhttp3.RequestBody;
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
            String CHANNEL_ID = "mtouch_ksr03";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "mtouch ksr03",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("GOODCADDIE")
                    .setContentText("결제완료체크중")
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        HashMap<String, String> paymentMap = (HashMap<String, String>) intent.getSerializableExtra("payment");

//        Request req = new Request();
//        req.data.put("van", intent.getStringExtra("van"));
//        req.data.put("vanId", intent.getStringExtra("vanId"));
//        req.data.put("vanTrxId", intent.getStringExtra("vanTrxId"));
//        req.data.put("amount", intent.getStringExtra("amount"));
//        req.data.put("regDate", intent.getStringExtra("regDate"));
//        req.data.put("authCd", intent.getStringExtra("authCd"));
//        req.data.put("trackId", intent.getStringExtra("trackId"));
//        req.data.put("type", intent.getStringExtra("type"));
//        req.data.put("number", intent.getStringExtra("number"));
//        req.data.put("installment", intent.getStringExtra("installment"));
//        req.data.put("prodQty", intent.getStringExtra("prodQty"));
//        req.data.put("prodDesc", intent.getStringExtra("prodDesc"));
//        req.data.put("prodName", intent.getStringExtra("prodName"));
//        req.data.put("prodPrice", intent.getStringExtra("prodPrice"));
//        req.data.put("payerName", intent.getStringExtra("payerName"));
//        req.data.put("payerEmail", intent.getStringExtra("payerEmail"));
//        req.data.put("payerTel", intent.getStringExtra("payerTel"));

//        Intent completeCheckIntent = new Intent(PayResultActivity.this, CompleteCheckReceiver.class);
//        completeCheckIntent.putExtra("payment",paymentMap);
//        completeCheckIntent.putExtra("responseData",responseobj.data);
//
//        completeCheckIntent.putExtra("van",paymentMap.get("van"));
//        completeCheckIntent.putExtra("vanId",paymentMap.get("vanId"));
//        completeCheckIntent.putExtra("vanTrxId",paymentMap.get("trackId"));
//        completeCheckIntent.putExtra("amount",responseobj.data.get("TotalAmount"));
//        completeCheckIntent.putExtra("regDate",responseobj.data.get("Authdate"));
//        completeCheckIntent.putExtra("authCd",responseobj.data.get("AuthNum"));
//        completeCheckIntent.putExtra("trackId",trackId);
//
//        if (new String(adminInfo.getTelegramType()).equals("0420")) {
//            completeCheckIntent.putExtra("type", "승인취소");
//        }else{
//            completeCheckIntent.putExtra("type","승인");
//        }
//
//        completeCheckIntent.putExtra("number",responseobj.data.get("CardNo"));
//        completeCheckIntent.putExtra("installment",paymentMap.get("installment"));
//
//        completeCheckIntent.putExtra("prodQty", "1");
//        completeCheckIntent.putExtra("prodDesc", paymentMap.get("payerAddr"));
//        completeCheckIntent.putExtra("prodName", paymentMap.get("prodName"));
//        try {
//            completeCheckIntent.putExtra("prodPrice", String.valueOf(Long.parseLong(responseobj.data.get("TotalAmount"))));
//        } catch (Exception e) {
//            completeCheckIntent.putExtra("prodPrice", responseobj.data.get("TotalAmount"));
//        }
//        completeCheckIntent.putExtra("payerName", paymentMap.get("payerName"));
//        completeCheckIntent.putExtra("payerEmail", paymentMap.get("payerEmail"));
//        completeCheckIntent.putExtra("payerTel", paymentMap.get("payerTel"));

        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");
        Log.d("CompleteTest", "====================== 서비스 실행 ======================");

        try {
            long alarmStartTime = intent.getLongExtra("alarmTime",System.currentTimeMillis());

            if((System.currentTimeMillis() - alarmStartTime) > (20 * 1000)){
                sendPushData(intent);
            }else{

                new Handler().postDelayed(()->{
                    try {
                        sendPushData(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },7000);
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    private void sendPushData(final Intent intent)  throws Exception {
        HashMap<String, String> paymentMap = (HashMap<String, String>) intent.getSerializableExtra("payment");
        HashMap<String, String> responseData = (HashMap<String, String>) intent.getSerializableExtra("responseData");

        Request req = new Request();
        req.data.put("issuerCode ", responseData.get("IssueCode"));
        req.data.put("acquirerCode  ", responseData.get("PurchaseCode"));
        req.data.put("van", intent.getStringExtra("van"));
        req.data.put("vanId", intent.getStringExtra("vanId"));
        req.data.put("vanTrxId", intent.getStringExtra("vanTrxId"));
        req.data.put("amount", intent.getStringExtra("amount"));
        req.data.put("regDate", intent.getStringExtra("regDate"));
        if(intent.getStringExtra("authCd")!=null)
        req.data.put("authCd", intent.getStringExtra("authCd").trim());
        req.data.put("trackId", intent.getStringExtra("trackId"));
        if(intent.getStringExtra("trxId")!=null)
        req.data.put("trxId", intent.getStringExtra("trxId"));
        req.data.put("type", intent.getStringExtra("type"));
        req.data.put("number", intent.getStringExtra("number"));
        if(intent.getStringExtra("installment")!=null)
        req.data.put("installment", intent.getStringExtra("installment"));
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
