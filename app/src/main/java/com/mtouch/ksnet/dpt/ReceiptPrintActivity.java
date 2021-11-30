package com.mtouch.ksnet.dpt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mtouch.caddie.databinding.ActivityReceiptPrintBinding;
import com.mtouch.caddie.network.CaddieAPIService;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.caddie.network.model.Order;
import com.mtouch.caddie.network.model.OrderDetail;
import com.mtouch.caddie.network.model.OrderListDetailResponse;
import com.mtouch.ksnet.dpt.action.obj.responseObj;
import com.mtouch.ksnet.dpt.db.PaymentInfo;
import com.mtouch.ksnet.dpt.design.util.LOG;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.MtouchSMSDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.pay.httpcomunity.retrofitclient.ApiUtils;
import com.mtouch.ksnet.dpt.ks03.pay.httpcomunity.sms.PatternUtil;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;
import com.mtouch.ksnet.dpt.telegram.NotiAsyncTask;
import com.pswseoul.util.GsonUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptPrintActivity extends AppCompatActivity {

    private ActivityReceiptPrintBinding binding;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForParse = new SimpleDateFormat("yyMMddHHmmss");
    private SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Order order;
    private ArrayList<OrderDetail> orderDetailArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptPrintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String trackId = getIntent().getStringExtra("trackId");
        String delngSe = getIntent().getStringExtra("delngSe").trim();
        String trxId = getIntent().getStringExtra("trxId");
        String cardNumber = getIntent().getStringExtra("cardNumber").trim();
        String amount = getIntent().getStringExtra("amount").trim();
        String authDate = getIntent().getStringExtra("authDate").trim();
        String authNum = getIntent().getStringExtra("authNum").trim();
        String issuCmpnyNm = getIntent().getStringExtra("issuCmpnyNm").trim();
        String puchasCmpnyNm = getIntent().getStringExtra("puchasCmpnyNm").trim();
        String instlmtMonth = getIntent().getStringExtra("instlmtMonth").trim();
        String brand = getIntent().getStringExtra("brand").trim();

        binding.mchtNameTextView.setText(SharedPreferenceUtil.getData(this, "name"));
        binding.mchtAddrTextView.setText(SharedPreferenceUtil.getData(this, "addr"));
        binding.mchtBizNumTextView.setText(SharedPreferenceUtil.getData(this, "identity"));
        binding.trxResultTextView.setText(delngSe);
        binding.timeTextView.setText(simpleDateFormatDetail.format(new Date()));

//        String cardNumber = paymentInfo.getCardNo();
//        for (int i = cardNumber.length(); i < 16; i++) {
//            cardNumber += "*";
//        }
//
//        cardNumber = cardNumber.substring(0, 4) + "-" + cardNumber.substring(4, 8) + "-" + cardNumber.substring(8, 12) + "-" + cardNumber.substring(12);

        String installment = instlmtMonth.equals("00") ? "일시불" : Integer.parseInt(instlmtMonth) + "개월";

        binding.cardNameTextView.setText(brand);
        binding.installmentTextView.setText(installment);
        binding.cardNumberTextView.setText(cardNumber);
        binding.amountTextView.setText(String.format("%,d", Integer.parseInt(amount)) + "원");
//        try {
//            approvalDayTextView.setText(simpleDateFormatDetail.format(simpleDateFormatForParse.parse(paymentInfo.getRegDate())));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        binding.approvalDayTextView.setText(authDate);
        binding.approvalNumberTwoTextView.setText(authNum);
        binding.cardNameTextView.setText(issuCmpnyNm);
        binding.purchaseNameTextView.setText(puchasCmpnyNm);

        binding.smsButtn.setOnClickListener(v -> {
            try {
                getToken(binding.phoneNumberEditText.getText().toString(),
                        binding.mchtNameTextView.getText().toString(),
                        binding.mchtAddrTextView.getText().toString(),
                        binding.mchtBizNumTextView.getText().toString(),
                        binding.cardNumberTextView.getText().toString(),
                        binding.trxResultTextView.getText().toString(),
                        binding.approvalNumberTwoTextView.getText().toString(),
                        String.format("%,d", Integer.parseInt(amount)) + "원",
                        binding.installmentTextView.getText().toString(),
                        binding.approvalDayTextView.getText().toString(),
                        binding.cardNameTextView.getText().toString(),
                        binding.purchaseNameTextView.getText().toString()
                );
            } catch (Exception e) {
                e.getMessage();
            }
        });

        binding.cancelButton.setOnClickListener(v -> {
            finish();
        });

        binding.imageSmsButtn.setOnClickListener(v -> {
            String savePath = fileSave();
            fileUploadAndSMSSend(savePath,
                    binding.phoneNumberEditText.getText().toString(),
                    binding.mchtNameTextView.getText().toString(),
                    binding.mchtAddrTextView.getText().toString(),
                    binding.mchtBizNumTextView.getText().toString(),
                    binding.cardNumberTextView.getText().toString(),
                    binding.trxResultTextView.getText().toString(),
                    binding.approvalNumberTwoTextView.getText().toString(),
                    String.format("%,d", Integer.parseInt(amount)) + "원",
                    binding.installmentTextView.getText().toString(),
                    binding.approvalDayTextView.getText().toString(),
                    binding.cardNameTextView.getText().toString(),
                    binding.purchaseNameTextView.getText().toString());

        });
    }

    private void getToken(String phoneNo,
                          String mchtName,
                          String mchtAddr,
                          String mchtBizNum,
                          String cardNumber,
                          String trxResult,
                          String authCd,
                          String amount,
                          String installment,
                          String regDate,
                          String issuer,
                          String acquirer) {

        ApiUtils.getAPIService().getStringToken("mtm198f-b18ca0-5b3-7df1a").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        String authorization = responseobj.data.get("Authorization");

                        sendSMS(authorization,
                                phoneNo,
                                mchtName,
                                mchtAddr,
                                mchtBizNum,
                                cardNumber,
                                trxResult,
                                authCd,
                                amount,
                                installment,
                                regDate,
                                issuer,
                                acquirer);
                    } else {
                        Toast.makeText(ReceiptPrintActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ReceiptPrintActivity.this, "network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String fileSave() {

        Bitmap bm = createViewToBitmap(this, binding.receiptLayout);
        File dir = new File(getFilesDir().getAbsolutePath() + File.separator + "receipts");

        boolean doSave = true;
        boolean fileSave = false;
        if (!dir.exists()) {
            doSave = dir.mkdirs();
        }

        if (doSave) {
            fileSave = saveBitmapToFile(dir, "receipt.jpeg", bm, Bitmap.CompressFormat.JPEG, 100);
        } else {
            LOG.e("Couldn't create target directory.");
        }

        if(fileSave){
            return dir.getAbsolutePath()+File.separator+"receipt.jpeg";
        }else{
            LOG.e("Couldn't save target image.");
        }

        return null;
    }

    private void fileUploadAndSMSSend(String path,
                                      String phoneNo,
                                      String mchtName,
                                      String mchtAddr,
                                      String mchtBizNum,
                                      String cardNumber,
                                      String trxResult,
                                      String authCd,
                                      String amount,
                                      String installment,
                                      String regDate,
                                      String issuer,
                                      String acquirer) {

        //PYS : 영수증 문자메세지로 보내기... 이미지는 나중에 하자
        Toast.makeText(ReceiptPrintActivity.this, "이미지영수증은 현재 지원하지않습니다.", Toast.LENGTH_SHORT).show();

//        ApiUtils.getAPIService().getStringToken("mtm198f-b18ca0-5b3-7df1a").enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    if (response.isSuccessful()) {
//                        String responseData = new String(response.body().bytes());
//                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
//                        String authorization = responseobj.data.get("Authorization");
//
//
//
//                        File file = new File(path);
//
//                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),
//                                file);
//                        Map<String, RequestBody> requestBodyMap = new HashMap<>();
//                        requestBodyMap.put("file\"; filename=\"" + System.currentTimeMillis()+"_receipt.jpeg",requestFile);
//
//
//                        ApiUtils.getSMSFileUploadService().uploadReceipt(authorization,requestBodyMap).enqueue(new Callback<ResponseBody>() {
//                            @Override
//                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                try {
//                                    if (response.isSuccessful()) {
//                                        String responseData = new String(response.body().bytes());
//                                        try{
//                                            JSONObject responseJSON = new JSONObject(responseData);
//                                            String fileKey = responseJSON.has("fileKey")?responseJSON.getString("fileKey"):null;
//                                            if(fileKey != null){
//
//                                                sendSMS(authorization,
//                                                        phoneNo,
//                                                        mchtName,
//                                                        mchtAddr,
//                                                        mchtBizNum,
//                                                        cardNumber,
//                                                        trxResult,
//                                                        authCd,
//                                                        amount,
//                                                        installment,
//                                                        regDate,
//                                                        issuer,
//                                                        acquirer,
//                                                        fileKey);
//                                            }
//                                        }catch (Exception e){}
//
//                                    } else {
//                                        Toast.makeText(ReceiptPrintActivity.this, "network error", Toast.LENGTH_SHORT).show();
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    Toast.makeText(ReceiptPrintActivity.this, "response data error", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                Toast.makeText(ReceiptPrintActivity.this, "network error", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//                    } else {
//                        Toast.makeText(ReceiptPrintActivity.this, "network error", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(ReceiptPrintActivity.this, "network error", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    public static Bitmap createViewToBitmap(Context context, View view) {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();


        Bitmap resizedBitmap = null;

        while (height > 400) {
            resizedBitmap = Bitmap.createScaledBitmap(bitmap, (width * 400) / height, 400, true);
            height = resizedBitmap.getHeight();
            width = resizedBitmap.getWidth();
        }

        bitmap.recycle();

        return resizedBitmap;
    }

    /**
     * @param dir      you can get from many places like Environment.getExternalStorageDirectory() or mContext.getFilesDir() depending on where you want to save the image.
     * @param fileName The file name.
     * @param bm       The Bitmap you want to save.
     * @param format   Bitmap.CompressFormat can be PNG,JPEG or WEBP.
     * @param quality  quality goes from 1 to 100. (Percentage).
     * @return true if the Bitmap was saved successfully, false otherwise.
     */
    boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                             Bitmap.CompressFormat format, int quality) {

        File imageFile = new File(dir, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(format, quality, fos);

            fos.close();

            return true;
        } catch (IOException e) {
            Log.e("app", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }finally {
            bm.recycle();
        }
        return false;
    }

    private void sendSMS(String authorization,
                         String phoneNo,
                         String mchtName,
                         String mchtAddr,
                         String mchtBizNum,
                         String cardNumber,
                         String trxResult,
                         String authCd,
                         String amount,
                         String installment,
                         String regDate,
                         String issuer,
                         String acquirer) {
        this.sendSMS(authorization,
                phoneNo,
                mchtName,
                mchtAddr,
                mchtBizNum,
                cardNumber,
                trxResult,
                authCd,
                amount,
                installment,
                regDate,
                issuer,
                acquirer,null);
    }

    private void sendSMS(String authorization,
                         String phoneNo,
                         String mchtName,
                         String mchtAddr,
                         String mchtBizNum,
                         String cardNumber,
                         String trxResult,
                         String authCd,
                         String amount,
                         String installment,
                         String regDate,
                         String issuer,
                         String acquirer,
                         String fileKey) {
        if (phoneNo == null || !PatternUtil.isCellphoneNo(phoneNo)) {
            Log.d("test", "번호없음");
            Toast.makeText(this, "핸드폰번호가 없거나 정상적이지 않습니다.\n다시 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        LOG.w("SMS전송시작");
        String name = mchtName;
//        String issuer = resp.data.get("PurchaseName");
//        String cardNumber = resp.data.get("number");
//        if (cardNumber == null || cardNumber.length() == 0) {
//            cardNumber = resp.data.get("bin");
//        }
//        for (int i = cardNumber.length(); i < 16; i++) {
//            cardNumber += "*";
//        }
//
//        cardNumber = cardNumber.substring(0, 4) + "-" + cardNumber.substring(4, 8) + "-" + cardNumber.substring(8, 12) + "-" + cardNumber.substring(12);

//        String trxResult = resp.data.get("type");
//        String authCd = resp.data.get("authCd");
//        String amount = resp.data.get("TotalAmount");
//        String installment = resp.data.get("installment");
//        String regDate = resp.data.get("regDate");

        HashMap<String, Object> data = new HashMap<>();
        data.put("title", "(주)부국위너스");
        data.put("from", "0517516422");
        data.put("ttl", "0");

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("to", "82" + phoneNo.substring(1).replaceAll("-", ""));
        ArrayList<Object> list = new ArrayList<>();
        list.add(map);


        data.put("destinations", list);
        if(fileKey!=null)
            data.put("fileKey",fileKey);

        StringBuffer sb = new StringBuffer();

        if(name!=null && name.length()>0) {
            sb.append("가맹점: ");
            sb.append(name);
            sb.append("\n");
        }
        if(mchtAddr!=null && mchtAddr.length()>0){
            sb.append("주소: ");
            sb.append(mchtAddr);
            sb.append("\n");
        }
        if(mchtBizNum!=null && mchtBizNum.length()>0){
            sb.append("사업자번호: ");
            sb.append(mchtBizNum);
            sb.append("\n");
        }
        sb.append("카드발급사: ");
        sb.append(issuer);
        sb.append("\n");
        sb.append("카드번호: ");
        sb.append(cardNumber);
        sb.append("\n");
        sb.append("승인결과: ");
        sb.append(trxResult);
        sb.append("\n");
        sb.append("승인번호: ");
        sb.append(authCd);
        sb.append("\n");
        sb.append("승인금액: ");
        sb.append(amount);
        sb.append("\n");
        if (installment != null && installment.length() > 0) {
            sb.append("할부기간: ");
            sb.append(installment);
            sb.append("\n");
        }
        sb.append("승인일자: ");
        sb.append(regDate);
        sb.append("\n");
        sb.append("매입사: ");
        sb.append(acquirer);

        data.put("text", sb.toString());

        //PYS : 영수증 문자메세지로 보내기
        HashMap<String, Object> param = new HashMap<>();
        param.put("phone",phoneNo);
        param.put("msg", sb.toString());
        CaddieAPIService caddieAPIService = NetworkManager.getAPIService(getApplicationContext());
        caddieAPIService.sendReceiptSMS(param).enqueue(new Callback<com.mtouch.caddie.network.model.Response>() {

            @Override
            public void onResponse(Call<com.mtouch.caddie.network.model.Response> call, retrofit2.Response<com.mtouch.caddie.network.model.Response> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = response.body().getResultMsg();

                        LOG.d(responseData);

                        try {
                            JSONObject logJson = new JSONObject();
                            logJson.put("message", "영수증이미지 문자전송.");
                            logJson.put("phoneNo", "대상번호: "+phoneNo);
                            logJson.put("data", sb.toString());
                            logJson.put("caddie_version", BuildConfig.VERSION_NAME);
                            logJson.put("os", Build.VERSION.SDK_INT + "");
                            logJson.put("model",Build.MODEL + "");
                            new NotiAsyncTask(ReceiptPrintActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
                        } catch (Exception ee){}

                        Toast.makeText(ReceiptPrintActivity.this, "SMS 전송완료", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ReceiptPrintActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<com.mtouch.caddie.network.model.Response> call, Throwable t) {
                Toast.makeText(ReceiptPrintActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
            }
        });

//        ApiUtils.getSMSSendService().sendSMS(authorization, data).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    if (response.isSuccessful()) {
////                        Toast.makeText(WebCheckActivity.this, "Success!", Toast.LENGTH_SHORT).show();
//                        String responseData = new String(response.body().bytes());
//
//                        LOG.d(responseData);
//
//                        try {
//                            JSONObject logJson = new JSONObject();
//                            logJson.put("message", "영수증이미지 문자전송.");
//                            logJson.put("phoneNo", "대상번호: "+phoneNo);
//                            logJson.put("data", sb.toString());
//                            logJson.put("caddie_version", BuildConfig.VERSION_NAME);
//                            logJson.put("os", Build.VERSION.SDK_INT + "");
//                            logJson.put("model",Build.MODEL + "");
//                            new NotiAsyncTask(ReceiptPrintActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
//                        } catch (Exception ee){}
//
//                        Toast.makeText(ReceiptPrintActivity.this, "SMS 전송완료", Toast.LENGTH_SHORT).show();
//                        finish();
////                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
////                        resp.setDataMap(GsonUtil.toJson(responseobj.data));
//
////                        System.out.println(resp.toJsonString());
//
//
//                    } else {
//                        Toast.makeText(ReceiptPrintActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(ReceiptPrintActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
////                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
//                Toast.makeText(ReceiptPrintActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
//            }
//        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}