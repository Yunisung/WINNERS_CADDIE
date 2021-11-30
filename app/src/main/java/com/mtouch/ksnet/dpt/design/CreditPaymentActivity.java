package com.mtouch.ksnet.dpt.design;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.ksnet.dpt.design.appToApp.ResponseObj;
import com.mtouch.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.mtouch.ksnet.dpt.design.appToApp.network.model.Request;
import com.mtouch.ksnet.dpt.design.util.GsonUtil;
import com.mtouch.ksnet.dpt.design.util.LOG;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.MtouchInstallmentDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditPaymentActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView headerTitleTextView;
    private EditText amountEditText;
    private RelativeLayout installmentButton;
    private TextView installmentTextView;
    private Button payButton;

    private String installmentString = "0"; //일시불

    // End Of Content View Elements

    private void bindViews() {

        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);
        amountEditText = findViewById(R.id.amountEditText);
        installmentButton = findViewById(R.id.installmentButton);
        installmentTextView = findViewById(R.id.installmentTextView);
        payButton = findViewById(R.id.payButton);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("결제하기");

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        installmentButton.setOnClickListener(v -> {
            new MtouchInstallmentDialog(this, new MtouchInstallmentDialog.OnListClickListener() {
                @Override
                public void onItemClick(String index) {
                    installmentString = index;
                    if (installmentString.equals("0")) {
                        installmentTextView.setText("일시불");
                    } else {
                        installmentTextView.setText(installmentString + "개월");
                    }

                }
            }).setTitleText("할부 기간")
                    .setPositiveButtonText("확인")
                    .setMaxInstallment(Integer.parseInt(SharedPreferenceUtil.getData(this, "apiMaxInstall", "0"))).show();
        });

        payButton.setOnClickListener(v -> {

            String amountString = amountEditText.getText().toString().trim();
            if (amountString.length() == 0 || amountString.equals("")) {
                new MtouchDialog(this).setContentText("금액을 입력해주세요.").show();
                return;
            }
            int amount;
            try {
                amount = Integer.parseInt(amountString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                new MtouchDialog(this).setTitleText("알림").setContentText("금액을 입력해주세요.").show();
                return;
            }

            int installment = Integer.parseInt(installmentString);

            if (amount < 50000 && installment > 0) {
                new MtouchDialog(this,false).setTitleText("알림").setContentText("5만원이상 할부가능합니다.").show();
                return;
            }

            payButton.setEnabled(false);
            payStart(amount, installment);


//            Intent intent = new Intent();
//            intent.putExtra("amount", amountString);
//            intent.putExtra("installment", installmentString);
//
//            setResult(RESULT_OK, intent);
//            finish();
//            overridePendingTransition(0, 0);
        });
    }

    private void payStart(int amount, int installment) {

        //정상진행
        sendCheckApprove(amount + "", String.format("%02d",installment));
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_payment);

        bindViews();
        init();
    }

    private void init() {

    }

    public void sendCheckApprove(final String _amount, String _installment) {
        Request req = new Request();
        req.data.put("amount", _amount);
        req.data.put("installment", _installment);

        ApiUtils.getAPIService().getCheckApprove(SharedPreferenceUtil.getData(this, "key"), req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        ResponseObj responseobj = (ResponseObj) GsonUtil.fromJson(responseData, ResponseObj.class);

                        /**
                         * {
                         *   "data": {
                         *     "result": "결제승인",
                         *     "van": "KSPAY1",
                         *     "vanId": "2006400005",
                         *     "trackId": "TX190829000193",
                         *     "secondKey": "DPT0A24555"
                         *   }
                         * }
                         */
                        String dptId = responseobj.getStringValue("secondKey");
                        String van = responseobj.getStringValue("van");
                        String vanId = responseobj.getStringValue("vanId");
                        String trackId = responseobj.getStringValue("trackId");

                        if (van == null || van.length() == 0 || !van.contains("KSPAY") || dptId == null || dptId.length() == 0) {
                            new MtouchDialog(CreditPaymentActivity.this, true).setTitleText("알림").setContentText(getString(R.string.tmn_setting_error)).show();
                            return;
                        }

                        //승인
                        Intent intent = new Intent();
                        intent.putExtra("amount",_amount);
                        intent.putExtra("dptId",dptId);
                        intent.putExtra("van",van);
                        intent.putExtra("vanId",vanId);
                        intent.putExtra("trackId",trackId);
                        intent.putExtra("installment",Integer.parseInt(_installment)==0?"00":_installment);

                        LOG.w("결제시작 @@@");
                        setResult(RESULT_OK,intent);
                        finish();
                    } else {
                        String resultMsg = new String(response.errorBody().bytes());
                        try{
                            new MtouchDialog(CreditPaymentActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                        }catch (Exception e){e.printStackTrace();}
                        payButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new MtouchDialog(CreditPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try{
                    new MtouchDialog(CreditPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }catch (Exception e){e.printStackTrace();}
                payButton.setEnabled(true);
            }
        });
    }


}
