package com.mtouch.ksnet.dpt.design;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.mtouch.ksnet.dpt.ReceiptPrintActivity;
import com.mtouch.ksnet.dpt.design.appToApp.ResponseObj;
import com.mtouch.ksnet.dpt.design.appToApp.network.APIService;
import com.mtouch.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.mtouch.ksnet.dpt.design.appToApp.network.model.Request;
import com.mtouch.ksnet.dpt.design.util.GsonUtil;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.MtouchSMSDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentCompleteActivity extends DeviceCheckActivity {


    private APIService mAPIService;
    private APIService mAPIDirectService;
    private APIService mAPISMSService;

    private ImageView backButton;
    private TextView headerTitleTextView;

    private TextView trackIdTextView;
    private TextView cardNumberTextView;
    private TextView amountTextView;
    private TextView approvalDayTextView;
    private TextView approvalNumberTwoTextView;
    private TextView trxIdTextView;
    private Button cancelButtn;
    private Button printButtn;
    private Button smsButtn;
    private Button receiptButtn;

    String trackId = "";
    String cardNumber = "";
    String amount = "";
    String approvalDay = "";
    String approvalNumber = "";
    String trxId = "";
    String delngSe = "";
    String authDate = "";
    String authNum = "";
    String issuCmpnyNm = "";
    String cardNo = "";
    String instlmtMonth = "";
    String puchasCmpnyNm = "";
    String setleMssage = "";
    String splpc = "";
    String vatt = "";

    String brand = "";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForParse = new SimpleDateFormat("yyMMddHHmmss");
    private SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // End Of Content View Elements

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);

        trackIdTextView = findViewById(R.id.trackIdTextView);
        cardNumberTextView = findViewById(R.id.cardNumberTextView);
        amountTextView = findViewById(R.id.amountTextView);
        approvalDayTextView = findViewById(R.id.approvalDayTextView);
        approvalNumberTwoTextView = findViewById(R.id.approvalNumberTwoTextView);
        trxIdTextView = findViewById(R.id.trxIdTextView);
        cancelButtn = findViewById(R.id.cancelButtn);
        printButtn = findViewById(R.id.printButtn);
        smsButtn = findViewById(R.id.smsButtn);
        receiptButtn = findViewById(R.id.receiptButtn);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("결제 완료 내역");

        printButtn.setOnClickListener(v -> {
            try {
                JSONObject json = new JSONObject();
                json.put("brand", brand);
                json.put("PurchaseName", puchasCmpnyNm);
                json.put("number", cardNo);
                json.put("amount", amount);
                json.put("authCd", authNum);
                json.put("installment", instlmtMonth);

                Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_SENDPRINT);
                Bundle bundle = new Bundle();
                bundle.putString("data", json.toString());
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } catch ( Exception e) { e.printStackTrace(); }
        });
        smsButtn.setOnClickListener(v -> {
            new MtouchSMSDialog(this).setSendData(getSMSJSONString(brand,cardNumber, delngSe,authNum,amount,instlmtMonth,authDate)).show();
        });
        cancelButtn.setOnClickListener(v->{
            cancelButtn.setEnabled(false);
            sendCheckApproveRefund(amount,instlmtMonth,trxId);
        });

        receiptButtn.setOnClickListener(v->{
            Intent intent = new Intent(PaymentCompleteActivity.this, ReceiptPrintActivity.class);
            intent.putExtra("trackId",trackId);
            intent.putExtra("delngSe",delngSe);
            intent.putExtra("trxId",trxId);
            intent.putExtra("cardNumber",cardNumber);
            intent.putExtra("amount",amount);
            intent.putExtra("authDate",approvalDayTextView.getText().toString());
            intent.putExtra("authNum",authNum);
            intent.putExtra("issuCmpnyNm",issuCmpnyNm);
            intent.putExtra("puchasCmpnyNm",puchasCmpnyNm);
            intent.putExtra("instlmtMonth",instlmtMonth);
            intent.putExtra("brand",brand);
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_complete);

        bindViews();
        init();

    }

    private void init() {
        mAPIService = ApiUtils.getAPIService();
        mAPIDirectService = ApiUtils.getAPIDirectService();
        mAPISMSService = ApiUtils.getSMSSendService();

        if (getIntent() != null) {
            String printText = getIntent().getStringExtra("printText");
            if(printText!=null && printText.length()>0){

            }

            trackId = getIntent().getStringExtra("trackId");
            cardNumber = getIntent().getStringExtra("cardNumber");
            amount = getIntent().getStringExtra("amount");
            approvalDay = getIntent().getStringExtra("approvalDay");
            approvalNumber = getIntent().getStringExtra("approvalNumber");
            trxId = getIntent().getStringExtra("trxId");

            delngSe = getIntent().getStringExtra("delngSe");
            authDate = getIntent().getStringExtra("authDate");
            authNum = approvalNumber;
            issuCmpnyNm = getIntent().getStringExtra("issuCmpnyNm");
            cardNo = cardNumber;
            instlmtMonth = getIntent().getStringExtra("instlmtMonth");
            puchasCmpnyNm = getIntent().getStringExtra("puchasCmpnyNm");
            setleMssage = getIntent().getStringExtra("setleMssage");
            brand = getIntent().getStringExtra("brand");

            trackIdTextView.setText(trackId);
            cardNumberTextView.setText(cardNumber);
            amountTextView.setText(String.format("\\ %,d 원", Integer.parseInt(amount)));
            try {
                approvalDayTextView.setText(simpleDateFormatDetail.format(simpleDateFormatForParse.parse(authDate)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            approvalNumberTwoTextView.setText(approvalNumber);
            trxIdTextView.setText(trxId);

            if(delngSe.equals("승인취소")){
                headerTitleTextView.setText("결제 취소 내역");
                cancelButtn.setVisibility(View.GONE);
            } else if(delngSe.equals("승인")){
                headerTitleTextView.setText("결제 승인 내역");
                cancelButtn.setVisibility(View.VISIBLE);
            } else if(delngSe.equals("승인실패")){
                headerTitleTextView.setText("승인실패");
                cancelButtn.setVisibility(View.GONE);
                smsButtn.setVisibility(View.GONE);
                printButtn.setVisibility(View.GONE);
            }

            if(SharedPreferenceUtil.getData(this,"appDirect").equals("Y")){
                cancelButtn.setVisibility(View.GONE);
            }
            if(trackId==null || trackId.length()==0){
                cancelButtn.setVisibility(View.GONE);
            }
        }
    }

    public void sendCheckApproveRefund(final String _amount, String _installment, String _trxId) {
        Request req = new Request();
        req.data.put("amount", _amount);
        req.data.put("installment", _installment);
        req.data.put("trxId",_trxId);

        mAPIService.getRefundCheckApprove(SharedPreferenceUtil.getData(this,"key"), req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        ResponseObj responseobj = (ResponseObj) GsonUtil.fromJson(responseData, ResponseObj.class);

                        /**
                         * {
                         *   "data": {
                         *     "result": "취소승인",
                         *     "amount": 1004,
                         *     "van": "KSPAY1",
                         *     "vanId": "2006400005",
                         *     "authCd": "20245300",
                         *     "trackId": "TX200414001199",
                         *     "regDay": "20200414",
                         *     "secondKey": "DPT0A24555"
                         *   }
                         * }
                         */
                        String trmnlNo = (String) responseobj.data.get("secondKey");
                        String van = (String) responseobj.data.get("van");
                        String vanId = (String) responseobj.data.get("vanId");
                        String authCd = (String) responseobj.data.get("authCd");
                        String regDay = (String) responseobj.data.get("regDay");
                        String secondKey = (String) responseobj.data.get("secondKey");
                        String trackId = (String) responseobj.data.get("trackId");

                        if (van == null || van.length() == 0 || !van.contains("KSPAY") || trmnlNo == null || trmnlNo.length() == 0) {
                            new MtouchDialog(PaymentCompleteActivity.this, true).setTitleText("알림").setContentText(getString(R.string.tmn_setting_error)).show();
                            cancelButtn.setEnabled(true);
                            return;
                        }

                        //정상진행

                        //todo 취소

                        Intent refundIntent = new Intent();
                        refundIntent.putExtra("trxId",trxId);
                        refundIntent.putExtra("authCd",authCd);
                        refundIntent.putExtra("regDay",regDay);
                        refundIntent.putExtra("secondKey",secondKey);
                        refundIntent.putExtra("amount",amount);
                        refundIntent.putExtra("van",van);
                        refundIntent.putExtra("vanId",vanId);
                        refundIntent.putExtra("trackId",trackId);
                        refundIntent.putExtra("installment",_installment);


                        setResult(RESULT_OK,refundIntent);
                        finish();
                    } else {
                        cancelButtn.setEnabled(true);
                        String resultMsg = new String(response.errorBody().bytes());
//                        setReturnData(ACTION_REFUND, RESULT_CODE_E0002, resultMsg);
//                        delayedFinish();
                        try{
                            new MtouchDialog(PaymentCompleteActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                        }catch (Exception e){e.printStackTrace();}
                    }
                } catch (Exception e) {
                    cancelButtn.setEnabled(true);
                    e.printStackTrace();
//                    setReturnData(ACTION_REFUND, RESULT_CODE_E0002, getString(R.string.network_error_msg));
//                    delayedFinish();
                    new MtouchDialog(PaymentCompleteActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                cancelButtn.setEnabled(true);
//                setReturnData(ACTION_REFUND, RESULT_CODE_E0002, getString(R.string.network_error_msg));
//                delayedFinish();
                try{
                    new MtouchDialog(PaymentCompleteActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }


    protected String getSMSJSONString(String brand, String number, String trxResult, String authCd, String amount, String installment, String regDate){
        JSONObject jo = new JSONObject();

        try {
            jo.put("name",SharedPreferenceUtil.getData(this,"name"));
            jo.put("brand",brand);
            jo.put("number",number);
            jo.put("trxResult",trxResult);
            jo.put("authCd",authCd);
            jo.put("amount",amount);
            jo.put("installment",installment);
            jo.put("regDate",regDate);
            jo.put("Authorization", SharedPreferenceUtil.getData(this,"Authorization"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jo.toString();
    }
}
