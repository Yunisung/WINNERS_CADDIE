package com.mtouch.ksnet.dpt.design;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;


public class InfomationActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView headerTitleTextView;

    private TextView agencyNameTextView;
    private TextView agencyCallTextView;
    private TextView agencyMailTextView;
    private TextView distNameTextView;
    private TextView distCallTextView;
    private TextView distMailTextView;

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);

        agencyNameTextView = (TextView) findViewById(R.id.agencyNameTextView);
        agencyCallTextView = (TextView) findViewById(R.id.agencyCallTextView);
        agencyMailTextView = (TextView) findViewById(R.id.agencyMailTextView);
        distNameTextView = (TextView) findViewById(R.id.distNameTextView);
        distCallTextView = (TextView) findViewById(R.id.distCallTextView);
        distMailTextView = (TextView) findViewById(R.id.distMailTextView);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("고객센터");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        bindViews();
        init();
    }

    private void init() {
        /**
         *    agencyEmail       -
         *    distEmail         -
         *    tmnId             -
         *    vat               -
         *    agencyTel         -
         *    telNo             -
         *    agencyName        -
         *    phoneNo           -
         *    result            -
         *    Authorization     -
         *    distTel           -
         *    semiAuth          -
         *    identity          -
         *    appDirect         -
         *    name              -
         *    distName          -
         *    payKey            -
         *    ceoName           -
         *    addr              -
         *    key               -
         * @param context
         * @param key
         * @return
         */
        agencyNameTextView.setText(SharedPreferenceUtil.getData(this, "agencyName"));
        agencyCallTextView.setText(SharedPreferenceUtil.getData(this, "agencyTel"));
        agencyMailTextView.setText(SharedPreferenceUtil.getData(this, "agencyEmail"));
        distNameTextView.setText(SharedPreferenceUtil.getData(this, "distName"));
        distCallTextView.setText(SharedPreferenceUtil.getData(this, "distTel"));
        distMailTextView.setText(SharedPreferenceUtil.getData(this, "distEmail"));

        //PYS : 고객센터 정보 추가
        agencyNameTextView.setText("부국위너스");
        agencyCallTextView.setText("051-751-6422");
        agencyMailTextView.setText("bukook@bkwinners.com");
    }
}
