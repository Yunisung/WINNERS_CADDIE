package com.bkwinners.caddie.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bkwinners.caddie.DefaultActivity;
import com.bkwinners.caddie.R;
import com.bkwinners.ksnet.dpt.design.OldMainActivity;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;

public class SignUpCompleteActivity extends DefaultActivity {

    // Content View Elements

    private TextView headerTitleTextView;
    private TextView mchtNameTextView;
    private TextView ceoNameTextView;
    private TextView businessNumberTextView;
    private TextView addressTextView;
    private TextView telNumberTextView;
    private Button confirmButton;

    // End Of Content View Elements

    private void bindViews() {

        headerTitleTextView = findViewById(R.id.headerTitleTextView);
        mchtNameTextView = findViewById(R.id.mchtNameTextView);
        ceoNameTextView = findViewById(R.id.ceoNameTextView);
        businessNumberTextView = findViewById(R.id.businessNumberTextView);
        addressTextView = findViewById(R.id.addressTextView);
        telNumberTextView = findViewById(R.id.telNumberTextView);
        confirmButton = findViewById(R.id.confirmButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_complete);

        bindViews();

        headerTitleTextView.setText("인증완료");
        mchtNameTextView.setText(SharedPreferenceUtil.getData(this,"name"));
        ceoNameTextView.setText(SharedPreferenceUtil.getData(this,"ceoName"));
        businessNumberTextView.setText(SharedPreferenceUtil.getData(this,"identity"));
        addressTextView.setText(SharedPreferenceUtil.getData(this,"addr"));
        telNumberTextView.setText(SharedPreferenceUtil.getData(this,"telNo"));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpCompleteActivity.this, OldMainActivity.class));
                finish();
            }
        });
    }
}
