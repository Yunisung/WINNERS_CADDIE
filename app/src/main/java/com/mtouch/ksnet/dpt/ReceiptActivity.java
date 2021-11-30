package com.mtouch.ksnet.dpt;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.caddie.auth.AuthSMSActivity;
import com.mtouch.caddie.network.CaddieAPIService;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.ksnet.dpt.action.obj.responseObj;
import com.mtouch.ksnet.dpt.db.PaymentInfo;
import com.mtouch.ksnet.dpt.design.util.LOG;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
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
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptActivity extends AppCompatActivity {

    private Realm realm = Realm.getDefaultInstance();
    private CaddieAPIService caddieAPIService;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForParse = new SimpleDateFormat("yyMMddHHmmss");
    private SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private LinearLayout receiptLayout;
    private LinearLayout mchtNameLayout;
    private LinearLayout mchtAddrLayout;
    private LinearLayout mchtBizNumLayout;

    private TextView headerTitleTextView;
    private TextView mchtNameTitleTextView;
    private TextView mchtNameTextView;
    private TextView mchtAddrTitleTextView;
    private TextView mchtAddrTextView;
    private TextView mchtBizNumTitleTextView;
    private TextView mchtBizNumTextView;
    private TextView trxResultTextView;
    private TextView cardNumberTextView;
    private TextView amountTextView;
    private TextView installmentTextView;
    private TextView approvalDayTextView;
    private TextView approvalNumberTwoTextView;
    private TextView cardNameTextView;
    private TextView purchaseNameTextView;
    private EditText phoneNumberEditText;
    private Button smsButtn;
    private Button cancelButton;
    private Button imageSmsButtn;

    private long clickTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        receiptLayout = findViewById(R.id.receiptLayout);
        mchtNameLayout = findViewById(R.id.mchtNameLayout);
        mchtAddrLayout = findViewById(R.id.mchtAddrLayout);
        mchtBizNumLayout = findViewById(R.id.mchtBizNumLayout);
        headerTitleTextView = (TextView) findViewById(R.id.headerTitleTextView);
        mchtNameTitleTextView = (TextView) findViewById(R.id.mchtNameTitleTextView);
        mchtNameTextView = (TextView) findViewById(R.id.mchtNameTextView);
        mchtAddrTitleTextView = (TextView) findViewById(R.id.mchtAddrTitleTextView);
        mchtAddrTextView = (TextView) findViewById(R.id.mchtAddrTextView);
        mchtBizNumTitleTextView = (TextView) findViewById(R.id.mchtBizNumTitleTextView);
        mchtBizNumTextView = (TextView) findViewById(R.id.mchtBizNumTextView);

        trxResultTextView = (TextView) findViewById(R.id.trxResultTextView);
        cardNumberTextView = (TextView) findViewById(R.id.cardNumberTextView);
        amountTextView = (TextView) findViewById(R.id.amountTextView);
        installmentTextView = (TextView) findViewById(R.id.installmentTextView);
        approvalDayTextView = (TextView) findViewById(R.id.approvalDayTextView);
        approvalNumberTwoTextView = (TextView) findViewById(R.id.approvalNumberTwoTextView);
        cardNameTextView = (TextView) findViewById(R.id.cardNameTextView);
        purchaseNameTextView = (TextView) findViewById(R.id.purchaseNameTextView);
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        smsButtn = (Button) findViewById(R.id.smsButtn);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        imageSmsButtn = (Button) findViewById(R.id.imageSmsButtn);

        String amount = getIntent().getStringExtra("amount");
        String authDate = getIntent().getStringExtra("authDate");
        String authNum = getIntent().getStringExtra("authNum");
        String payType = getIntent().getStringExtra("payType");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");

        if (phoneNumber != null && phoneNumber.length() > 0) {
            phoneNumberEditText.setText(phoneNumber);
        }

        PaymentInfo paymentInfo = realm.where(PaymentInfo.class)
                .equalTo("splpc", amount)
                .equalTo("confmNo", authNum)
                .equalTo("delngSe", payType)
                .equalTo("regDate", authDate).findFirst();

        if (paymentInfo == null) {
            Toast.makeText(this, "결제내역이 없습니다.", Toast.LENGTH_SHORT).show();
            LOG.w("결제내역이 없습니다.");
            finish();
            return;
        }

        if (getIntent().hasExtra("mchtName") && getIntent().getStringExtra("mchtName").length()>0) {
            mchtNameTextView.setText(getIntent().getStringExtra("mchtName"));
            mchtNameLayout.setVisibility(View.VISIBLE);
        } else {
            if (paymentInfo.getMchtName() != null && paymentInfo.getMchtName().length() > 0) {
                mchtNameTextView.setText(paymentInfo.getMchtName());
                mchtNameLayout.setVisibility(View.VISIBLE);
            } else {
                mchtNameLayout.setVisibility(View.GONE);
            }
        }
        if (getIntent().hasExtra("mchtAddr") && getIntent().getStringExtra("mchtAddr").length()>0) {
            mchtAddrTextView.setText(getIntent().getStringExtra("mchtAddr"));
            mchtAddrLayout.setVisibility(View.VISIBLE);
        } else {
            if (paymentInfo.getMchtAddr() != null && paymentInfo.getMchtAddr().length() > 0) {
                mchtAddrTextView.setText(paymentInfo.getMchtAddr());
                mchtAddrLayout.setVisibility(View.VISIBLE);
            } else {
                mchtAddrLayout.setVisibility(View.GONE);
            }
        }
        if (getIntent().hasExtra("mchtBizNum") && getIntent().getStringExtra("mchtBizNum").length()>0) {
            mchtBizNumTextView.setText(getIntent().getStringExtra("mchtBizNum"));
            mchtBizNumLayout.setVisibility(View.VISIBLE);
        } else {
            if (paymentInfo.getMchtBizNum() != null && paymentInfo.getMchtBizNum().length() > 0) {
                mchtBizNumTextView.setText(paymentInfo.getMchtBizNum());
                mchtBizNumLayout.setVisibility(View.VISIBLE);
            } else {
                mchtBizNumLayout.setVisibility(View.GONE);
            }
        }



        trxResultTextView.setText(paymentInfo.getDelngSe());


        String cardNumber = paymentInfo.getCardNo();
        for (int i = cardNumber.length(); i < 16; i++) {
            cardNumber += "*";
        }

        cardNumber = cardNumber.substring(0, 4) + "-" + cardNumber.substring(4, 8) + "-" + cardNumber.substring(8, 12) + "-" + cardNumber.substring(12);

        String installment = paymentInfo.getInstlmtMonth().equals("00") ? "일시불" : Integer.parseInt(paymentInfo.getInstlmtMonth()) + "개월";

        installmentTextView.setText(installment);

        cardNumberTextView.setText(cardNumber);
        amountTextView.setText(String.format("%,d", Integer.parseInt(amount)) + "원");
        try {
            approvalDayTextView.setText(simpleDateFormatDetail.format(simpleDateFormatForParse.parse(paymentInfo.getRegDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        approvalNumberTwoTextView.setText(paymentInfo.getConfmNo());
        cardNameTextView.setText(paymentInfo.getIssuCmpnyNm());
        purchaseNameTextView.setText(paymentInfo.getPuchasCmpnyNm());


        smsButtn.setOnClickListener(v -> {
            if(clickTime != -1 && System.currentTimeMillis() - clickTime <3000){
                Toast.makeText(this, "짧은시간동안 연속해서 전송이 불가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            clickTime = System.currentTimeMillis();
            Toast.makeText(this, "문자 전송중입니다.",Toast.LENGTH_SHORT).show();
            try {
                getToken(phoneNumberEditText.getText().toString(),
                        mchtNameTextView.getText().toString(),
                        mchtAddrTextView.getText().toString(),
                        mchtBizNumTextView.getText().toString(),
                        cardNumberTextView.getText().toString(),
                        trxResultTextView.getText().toString(),
                        approvalNumberTwoTextView.getText().toString(),
                        String.format("%,d", Integer.parseInt(amount)) + "원",
                        installmentTextView.getText().toString(),
                        approvalDayTextView.getText().toString(),
                        cardNameTextView.getText().toString(),
                        purchaseNameTextView.getText().toString()
                );
            } catch (Exception e) {
                e.getMessage();
            }
        });

        cancelButton.setOnClickListener(v -> {
            finish();
        });

        imageSmsButtn.setOnClickListener(v -> {
            if(clickTime != -1 && System.currentTimeMillis() - clickTime <3000){
                Toast.makeText(this, "짧은시간동안 연속해서 전송이 불가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            clickTime = System.currentTimeMillis();

            Toast.makeText(this, "이미지 전송중입니다.",Toast.LENGTH_SHORT).show();
            String savePath = fileSave();
            fileUploadAndSMSSend(savePath,
                    phoneNumberEditText.getText().toString(),
                    mchtNameTextView.getText().toString(),
                    mchtAddrTextView.getText().toString(),
                    mchtBizNumTextView.getText().toString(),
                    cardNumberTextView.getText().toString(),
                    trxResultTextView.getText().toString(),
                    approvalNumberTwoTextView.getText().toString(),
                    String.format("%,d", Integer.parseInt(amount)) + "원",
                    installmentTextView.getText().toString(),
                    approvalDayTextView.getText().toString(),
                    cardNameTextView.getText().toString(),
                    purchaseNameTextView.getText().toString());

        });

        init();
    }

    private void init() {
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());
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
                        Toast.makeText(ReceiptActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ReceiptActivity.this, "network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String fileSave() {

        Bitmap bm = createViewToBitmap(this, receiptLayout);
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
        ApiUtils.getAPIService().getStringToken("mtm198f-b18ca0-5b3-7df1a").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        String authorization = responseobj.data.get("Authorization");



                        File file = new File(path);

                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),
                                file);
                        Map<String, RequestBody> requestBodyMap = new HashMap<>();
                        requestBodyMap.put("file\"; filename=\"" + System.currentTimeMillis()+"_receipt.jpeg",requestFile);


                        ApiUtils.getSMSFileUploadService().uploadReceipt(authorization,requestBodyMap).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        String responseData = new String(response.body().bytes());
                                        try{
                                            JSONObject responseJSON = new JSONObject(responseData);
                                            String fileKey = responseJSON.has("fileKey")?responseJSON.getString("fileKey"):null;
                                            if(fileKey != null){

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
                                                        acquirer,
                                                        fileKey);
                                            }
                                        }catch (Exception e){}

                                    } else {
                                        Toast.makeText(ReceiptActivity.this, "network error", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(ReceiptActivity.this, "response data error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(ReceiptActivity.this, "network error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(ReceiptActivity.this, "network error", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ReceiptActivity.this, "network error", Toast.LENGTH_SHORT).show();
            }
        });
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
                            logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
                            logJson.put("os", Build.VERSION.SDK_INT + "");
                            logJson.put("model",Build.MODEL + "");
                            new NotiAsyncTask(ReceiptActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
                        } catch (Exception ee){}

                        Toast.makeText(ReceiptActivity.this, "SMS 전송완료", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(ReceiptActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ReceiptActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.mtouch.caddie.network.model.Response> call, Throwable t) {
                t.getMessage();
                Toast.makeText(ReceiptActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
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
//                            logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
//                            logJson.put("os", Build.VERSION.SDK_INT + "");
//                            logJson.put("model",Build.MODEL + "");
//                            new NotiAsyncTask(ReceiptActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
//                        } catch (Exception ee){}
//
//                        Toast.makeText(ReceiptActivity.this, "SMS 전송완료", Toast.LENGTH_SHORT).show();
//                        finish();
////                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
////                        resp.setDataMap(GsonUtil.toJson(responseobj.data));
//
////                        System.out.println(resp.toJsonString());
//
//
//                    } else {
//                        Toast.makeText(ReceiptActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(ReceiptActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
////                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
//                Toast.makeText(ReceiptActivity.this, "SMS 전송실패", Toast.LENGTH_SHORT).show();
//            }
//        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm!=null) realm.close();
    }
}