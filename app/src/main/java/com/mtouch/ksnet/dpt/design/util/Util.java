package com.mtouch.ksnet.dpt.design.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.mtouch.caddie.R;
import com.mtouch.caddie.network.CaddieAPIService;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.caddie.network.model.Response;
import com.mtouch.ksnet.dpt.design.appToApp.network.ApiUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Util {


    public static void sendOriginalTextSMS(Activity activity, final String smsNumber, String smsText) {
        if (smsNumber == null || smsNumber.length() == 0 || smsText == null || smsText.length() == 0) {
            return;
        }
        Log.d("debug", "==============================" + smsNumber + "====data " + smsText);


        try {

            HashMap<String, Object> data = new HashMap<>();
            data.put("title", "(주)광원");
            data.put("from", "18551838");
            data.put("ttl", "0");

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("to", "82" + smsNumber.substring(1).replaceAll("-", ""));
            ArrayList<Object> list = new ArrayList<>();
            list.add(map);


            data.put("destinations", list);
            data.put("text", smsText);

            //PYS : 영수증 문자메세지로 보내기
            HashMap<String, Object> param = new HashMap<>();
            param.put("phone",smsNumber);
            param.put("msg", smsText);
            CaddieAPIService caddieAPIService = NetworkManager.getAPIService(activity.getApplicationContext());
            caddieAPIService.sendReceiptSMS(param).enqueue(new Callback<com.mtouch.caddie.network.model.Response>() {

                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    try {
                        if (response.isSuccessful()) {
                            String responseData = response.body().getResultMsg();

                            Log.d("test", responseData);
//                            Toast.makeText(context, smsNumber + "으로 SMS 전송되었습니다.", Toast.LENGTH_SHORT).show();
                            if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_check).setTitleText("전송 완료").setContentText(smsNumber + "으로 SMS를 성공적으로 전송 하였습니다.").show();
                        } else {
//                            Toast.makeText(context, smsNumber + "으로 SMS 전송실패했습니다.", Toast.LENGTH_SHORT).show();
                            if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("전송 실패").setContentText(smsNumber + "으로 SMS를 전송실패 하였습니다.").show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("전송 실패").setContentText(smsNumber + "으로 SMS를 전송실패 하였습니다.").show();
                    t.getMessage();
                }
            });

//            ApiUtils.getSMSSendService().sendSMS(SharedPreferenceUtil.getData(activity,"Authorization"), data).enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                    try {
//                        if (response.isSuccessful()) {
//                            String responseData = new String(response.body().bytes());
//
//                            Log.d("test", responseData);
////                            Toast.makeText(context, smsNumber + "으로 SMS 전송되었습니다.", Toast.LENGTH_SHORT).show();
//                            if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_check).setTitleText("전송 완료").setContentText(smsNumber + "으로 SMS를 성공적으로 전송 하였습니다.").show();
//                        } else {
////                            Toast.makeText(context, smsNumber + "으로 SMS 전송실패했습니다.", Toast.LENGTH_SHORT).show();
//                            if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("전송 실패").setContentText(smsNumber + "으로 SMS를 전송실패 하였습니다.").show();
//                        }
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
////                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
////                    Toast.makeText(, smsNumber + "으로 SMS 전송실패했습니다.", Toast.LENGTH_SHORT).show();
//                    if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("전송 실패").setContentText(smsNumber + "으로 SMS를 전송실패 하였습니다.").show();
//                    t.getMessage();
//                }
//            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendSMS(Activity activity, final String smsNumber, String smsText) {
        if (smsNumber == null || smsNumber.length() == 0 || smsText == null || smsText.length() == 0) {
            return;
        }
        Log.d("debug", "==============================" + smsNumber + "====data " + smsText);

//        if(KSNETStatus.telNo == null  || KSNETStatus.telNo.length() < 5 ) {
//            Toasty.info(this, "핸드폰의 전화번호가 없어 SMS를 발송이 안될 수 있습니다", Toast.LENGTH_SHORT);
//            return;
//        }

        //   if(smsNumber == null  || smsNumber.length() < 5) return ;
        //    PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);
        //    PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        //    SmsManager mSmsManager = SmsManager.getDefault();
        try {
            JSONObject jo = new JSONObject(smsText);

            String name = jo.getString("name");
            String issuer = jo.getString("brand");
            String cardNumber = jo.getString("number");
            if (cardNumber == null || cardNumber.length() == 0) {
                cardNumber = jo.getString("bin");
            }

            String trxResult = jo.getString("trxResult");
            String authCd = jo.getString("authCd");
            String amount = jo.getString("amount");
            String installment = jo.getString("installment");
            installment = Integer.parseInt(installment)==0?"일시불":jo.getString("installment")+"개월";
            String regDate = jo.getString("regDate");

            String authorization = SharedPreferenceUtil.getData(activity,"Authorization");

            /**
             * (주)광원
             * [Web발신]
             * 가맹점:주식회사 광원
             * 카드사:KB국민카드
             * 카드번호:540926******0012
             * 승인결과:승인
             * 승인번호:30027341
             * 승인금액:1004
             * 할부기간:00
             * 승인일자:191121155103
             * (주)광원
             * [Web발신]
             * 가맹점:주식회사 광원
             * 카드사:KB국민카드
             * 카드번호:540926******0012
             * 승인결과:취소
             * 승인번호:30027341
             * 승인금액:1004
             * 할부기간:00
             * 승인일자:191121155118
             */

            HashMap<String, Object> data = new HashMap<>();
            data.put("title", "(주)광원");
            data.put("from", "18551838");
            data.put("ttl", "0");

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("to", "82" + smsNumber.substring(1).replaceAll("-", ""));
            ArrayList<Object> list = new ArrayList<>();
            list.add(map);


            data.put("destinations", list);


            StringBuffer sb = new StringBuffer();

            sb.append("가맹점:");
            sb.append(name);
            sb.append("\n");
            sb.append("카드사:");
            sb.append(issuer);
            sb.append("\n");
            sb.append("카드번호:");
            sb.append(cardNumber);
            sb.append("\n");
            sb.append("승인결과:");
            sb.append(trxResult);
            sb.append("\n");
            sb.append("승인번호:");
            sb.append(authCd);
            sb.append("\n");
            sb.append("승인금액:");
            sb.append(amount);
            sb.append("\n");
            if (installment != null && installment.length() > 0) {
                sb.append("할부기간:");
                sb.append(installment);
                sb.append("\n");
            }
            sb.append("승인일자:");
            sb.append(regDate);

            data.put("text", sb.toString());

            //PYS : 영수증 문자메세지로 보내기
            HashMap<String, Object> param = new HashMap<>();
            param.put("phone",smsNumber);
            param.put("msg", sb.toString());
            CaddieAPIService caddieAPIService = NetworkManager.getAPIService(activity.getApplicationContext());
            caddieAPIService.sendReceiptSMS(param).enqueue(new Callback<com.mtouch.caddie.network.model.Response>() {

                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    try {
                        if (response.isSuccessful()) {
                            String responseData = response.body().getResultMsg();

                            Log.d("test", responseData);
//                            Toast.makeText(context, smsNumber + "으로 SMS 전송되었습니다.", Toast.LENGTH_SHORT).show();
                            if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_check).setTitleText("전송 완료").setContentText(smsNumber + "으로 SMS를 성공적으로 전송 하였습니다.").show();
                        } else {
//                            Toast.makeText(context, smsNumber + "으로 SMS 전송실패했습니다.", Toast.LENGTH_SHORT).show();
                            if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("전송 실패").setContentText(smsNumber + "으로 SMS를 전송실패 하였습니다.").show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("전송 실패").setContentText(smsNumber + "으로 SMS를 전송실패 하였습니다.").show();
                    t.getMessage();
                }
            });


//            ApiUtils.getSMSSendService().sendSMS(authorization, data).enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                    try {
//                        if (response.isSuccessful()) {
//                            String responseData = new String(response.body().bytes());
//
//                            Log.d("test", responseData);
//                            if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_check).setTitleText("전송 완료").setContentText(smsNumber + "으로 SMS를 성공적으로 전송 하였습니다.").show();
//                        } else {
////                            Toast.makeText(context, smsNumber + "으로 SMS 전송실패했습니다.", Toast.LENGTH_SHORT).show();
//                            if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("전송 실패").setContentText(smsNumber + "으로 SMS를 전송실패 하였습니다.").show();
//                        }
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
////                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
////                    Toast.makeText(context, smsNumber + "으로 SMS 전송실패했습니다.", Toast.LENGTH_SHORT).show();
//                    if(!activity.isFinishing()) new MtouchDialog(activity).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("전송 실패").setContentText(smsNumber + "으로 SMS를 전송실패 하였습니다.").show();
//                    t.getMessage();
//                }
//            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getVersion(Context context, String packageName) {
        String versionName = null;
        if (context == null) return versionName;
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        for (PackageInfo pack : packs) {
            if (pack.packageName.equals(packageName)) {
                versionName = pack.versionName;
                break;
            }
        }

        return versionName;
    }
}
