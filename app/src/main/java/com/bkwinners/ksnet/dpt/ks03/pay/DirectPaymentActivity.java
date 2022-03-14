package com.bkwinners.ksnet.dpt.ks03.pay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import java.util.Arrays;
import java.util.Calendar;

public class DirectPaymentActivity extends AppCompatActivity {

    private TextView previewAmountTextView;
    private EditText cardNumberEditText;
    private EditText amountEditText;
    private Spinner expiryYearSpinner;
    private Spinner expiryMonthSpinner;
    private Spinner installMentSpinner;
    private EditText payerTelEditText;
    private EditText nameEditText;
    private Button paymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_payment);

        bindViews();

        init();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void bindViews() {

        previewAmountTextView = (TextView) findViewById(R.id.previewAmountTextView);
        cardNumberEditText = (EditText) findViewById(R.id.cardNumberEditText);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        expiryYearSpinner = (Spinner) findViewById(R.id.expiryYearSpinner);
        expiryMonthSpinner = (Spinner) findViewById(R.id.expiryMonthSpinner);
        installMentSpinner = (Spinner) findViewById(R.id.installMentSpinner);
        payerTelEditText = (EditText) findViewById(R.id.payerTelEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        paymentButton = (Button) findViewById(R.id.paymentButton);

        String[] yearStrings = new String[7];
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 7; i++) {
            yearStrings[i] = String.valueOf(year);
            year++;
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(yearStrings));
        expiryYearSpinner.setAdapter(adapter);

        //6 - 0 2 3 4 5 6
        int apiMaxInstall = Integer.parseInt(getIntent().getStringExtra("apiMaxInstall"));
        String[] installStrings = new String[apiMaxInstall];

        for (int i = 0; i < apiMaxInstall; i++) {
            if (i == 0) {
                installStrings[i] = "일시불";
            } else {
                installStrings[i] = (i + 1) + "개월";
            }
        }

        ArrayAdapter<CharSequence> installAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.asList(installStrings));
        installMentSpinner.setAdapter(installAdapter);

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    previewAmountTextView.setText("￦" + new String().format("%,d", Integer.parseInt(s.toString())));
                } catch (Exception e) {
                    previewAmountTextView.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        amount = data.getStringExtra("amount");
//        payerName = data.getStringExtra("payerName");
//        payerEmail = data.getStringExtra("payerEmail");
//        payerTel = data.getStringExtra("payerTel");
//        name = data.getStringExtra("name");
//        cardNum = data.getStringExtra("cardNum");
//        expiry_year = data.getStringExtra("expiry_year");
//        expiry_month = data.getStringExtra("expiry_month");
//        installment = data.getStringExtra("installment");

        paymentButton.setOnClickListener(v -> {

            String amount = amountEditText.getText().toString();
            String payerTel = payerTelEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String cardNum = cardNumberEditText.getText().toString();
            String expiryYear = expiryYearSpinner.getSelectedItem().toString();
            String expiryMonth = expiryMonthSpinner.getSelectedItem().toString();
            String installment = installMentSpinner.getSelectedItem().toString();
            if (installment.equals("일시불")) {
                installment = "00";
            } else {
                installment = installment.replaceAll("개월", "");
            }

            if (cardNum == null || cardNum.length() == 0) {
                Toast.makeText(DirectPaymentActivity.this, "카드번호를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount == null || amount.length() == 0) {
                Toast.makeText(DirectPaymentActivity.this, "금액을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name == null || name.length() == 0) {
                Toast.makeText(DirectPaymentActivity.this, "상품명을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (payerTel == null || payerTel.length() == 0) {
                Toast.makeText(DirectPaymentActivity.this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }


            Intent intent = new Intent();
            intent.putExtra("amount", amount);
            intent.putExtra("payerTel", payerTel);
            intent.putExtra("name", name);
            intent.putExtra("cardNum", cardNum);
            intent.putExtra("expiry_year", expiryYear.substring(2, 4));
            intent.putExtra("expiry_month", expiryMonth);
            intent.putExtra("installment", installment);
            setResult(RESULT_OK, intent);
            finish();

        });
    }

    private void init() {
        if (getIntent() != null) {

            String amount = getIntent().getStringExtra("amount");
            String installment = getIntent().getStringExtra("installment");
            String name = getIntent().getStringExtra("name");
            String payerTel = getIntent().getStringExtra("payerTel");

            if (amount != null && amount.length() > 0) {
                amountEditText.setText(amount);
                amountEditText.setEnabled(false);
            }

            if (installment != null && installment.length() > 0) {
                try {
                    int index = Integer.parseInt(installment) - 1;
                    installMentSpinner.setSelection(index<0?0:index);
                    installMentSpinner.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (name != null && name.length() > 0) {
                nameEditText.setText(name);
                nameEditText.setEnabled(false);
            }
            if (payerTel != null && payerTel.length() > 0) {
                payerTelEditText.setText(payerTel);
                payerTelEditText.setEnabled(false);
            }
        }


    }


    private boolean vaildate(String param) {


        return true;
    }


}
