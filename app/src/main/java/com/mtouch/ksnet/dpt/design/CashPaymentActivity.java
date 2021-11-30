package com.mtouch.ksnet.dpt.design;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;
import org.json.JSONException;
import org.json.JSONObject;


public class CashPaymentActivity extends AppCompatActivity {

    public static final String TYPE_BIZ = "biz";
    public static final String TYPE_PERSONAL = "personal";
    public static final String TYPE_KEYIN = "keyin";
    public static final String TYPE_CARD = "card";


    private ImageView backButton;
    private TextView headerTitleTextView;

    /*
     * Exceptions found during parsing
     *
     * References a layout (@layout/header_layout)
     */

    // Content View Elements

    private RadioGroup radioGroup;
    private RadioButton approvalRadioButton;
    private RadioButton approvalCancelRadioButton;
    private EditText dptIdEditText;
    private LinearLayout cancelLayout;
    private EditText cancelAuthNumberEditText;
    private EditText cancelAuthDateEditText;
    private RadioButton bizReceiptRadioButton;
    private RadioButton personalReceiptRadioButton;
    private RadioButton cardRadioButton;
    private RadioButton numberRadioButton;
    private EditText cashNumberEditText;
    private EditText amountEditText;
    private Button payButton;
    private EditText bizIdEditText;

    private String bizAndPersonCheckValue;
    private String cardAndNumber;
    private boolean isCancel = false;
    // End Of Content View Elements

    private void bindViews() {

        bizIdEditText = findViewById(R.id.bizIdEditText);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        approvalRadioButton = (RadioButton) findViewById(R.id.approvalRadioButton);
        approvalCancelRadioButton = (RadioButton) findViewById(R.id.approvalCancelRadioButton);
        dptIdEditText = (EditText) findViewById(R.id.dptIdEditText);
        cancelLayout = (LinearLayout) findViewById(R.id.cancelLayout);
        cancelAuthNumberEditText = (EditText) findViewById(R.id.cancelAuthNumberEditText);
        cancelAuthDateEditText = (EditText) findViewById(R.id.cancelAuthDateEditText);
        bizReceiptRadioButton = (RadioButton) findViewById(R.id.bizReceiptRadioButton);
        personalReceiptRadioButton = (RadioButton) findViewById(R.id.personalReceiptRadioButton);
        cardRadioButton = (RadioButton) findViewById(R.id.cardRadioButton);
        numberRadioButton = (RadioButton) findViewById(R.id.numberRadioButton);
        cashNumberEditText = (EditText) findViewById(R.id.cashNumberEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        payButton = (Button) findViewById(R.id.payButton);

        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("현금영수증");

        bizIdEditText.setText(SharedPreferenceUtil.getData(this, "identity"));

        approvalRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isCancel = false;
                cancelLayout.setVisibility(View.GONE);
            }
        });
        approvalCancelRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isCancel = true;
                cancelLayout.setVisibility(View.VISIBLE);
            }
        });

        bizReceiptRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                bizAndPersonCheckValue = TYPE_BIZ;
        });
        personalReceiptRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                bizAndPersonCheckValue = TYPE_PERSONAL;
        });
        cardRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                cardAndNumber = TYPE_CARD;
        });
        numberRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                cardAndNumber = TYPE_KEYIN;
        });

        payButton.setOnClickListener(v -> payStart());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);

        bindViews();
        init();
    }

    private void init() {
        if (BuildConfig.IS_DEVEL) {
            bizReceiptRadioButton.setChecked(true);
            cardRadioButton.setChecked(true);
            bizIdEditText.setText("1208197322");
            dptIdEditText.setText("DPT0TEST03");
            cashNumberEditText.setText("01012341234");
        }
    }


    private void payStart() {

        if (bizIdEditText.getText().toString().equals("") || bizIdEditText.getText().toString().length() == 0) {
            new MtouchDialog(this,false).setTitleText("알림").setContentText("사업자번호를 입력하세요.").show();
            return;
        }
        if (dptIdEditText.getText().toString().equals("") || dptIdEditText.getText().toString().length() == 0) {
            new MtouchDialog(this,false).setTitleText("알림").setContentText("단말기번호를 입력하세요.").show();
            return;
        }
        if (amountEditText.getText().toString().equals("") || amountEditText.getText().toString().length() == 0) {
            new MtouchDialog(this,false).setTitleText("알림").setContentText("금액을 입력하세요.").show();
            return;
        }

        if (isCancel) {
            if (cancelAuthDateEditText.getText().toString().equals("") || cancelAuthDateEditText.getText().toString().length() == 0) {
                new MtouchDialog(this,false).setTitleText("알림").setContentText("승인일자를 입력하세요.").show();
                return;
            }
            if (cancelAuthNumberEditText.getText().toString().equals("") || cancelAuthNumberEditText.getText().toString().length() == 0) {
                new MtouchDialog(this,false).setTitleText("알림").setContentText("승인번호를 입력하세요.").show();
                return;
            }
        }

        int vat = Integer.parseInt(amountEditText.getText().toString()) / 11;
        int splpc = (Integer.parseInt(amountEditText.getText().toString()) - vat);



        //todo 승인
        JSONObject json = new JSONObject();

        try {
            json.put("amount",amountEditText.getText().toString());
            /**
             * 앞자리 1Byte : 취소 사유(취소시만 사용 : 1.거래취소, 2.오류발급취소, 3.기타) - 승인시 '0' Set
             *              뒷자리 1Byte : "0" - 개인 소득공제, "1" - 사업자 지출증빙
             */
            json.put("cashType",(isCancel?"1":"0") + (bizAndPersonCheckValue.equals(TYPE_BIZ)?"1":"0"));
            json.put("isCancel",isCancel?"true":"false");
            json.put("secondKey",dptIdEditText.getText().toString());
            json.put("number",cashNumberEditText.getText().toString());
            json.put("authdate",cancelAuthDateEditText.getText().toString());
            json.put("authnum",cancelAuthNumberEditText.getText().toString());
            json.put("dptid",dptIdEditText.getText().toString());
            json.put("isCard",cardAndNumber.equals(TYPE_CARD)?"true":"false");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent requestIntent = new Intent();
        requestIntent.putExtra("data",json.toString());
        setResult(RESULT_OK,requestIntent);
        finish();
    }
}
