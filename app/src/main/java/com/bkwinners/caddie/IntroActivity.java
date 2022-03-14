package com.bkwinners.caddie;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.bkwinners.caddie.auth.LoginActivity;
import com.bkwinners.caddie.auth.SignUpActivity;
import com.bkwinners.ksnet.dpt.design.OldMainActivity;
import com.bkwinners.ksnet.dpt.design.auth.AuthFirstActivity;
import com.bkwinners.ksnet.dpt.design.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.pswseoul.util.AndroidUtils;
import com.bkwinners.caddie.R;
import com.bkwinners.caddie.BuildConfig;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class IntroActivity extends AppCompatActivity {

    private ImageView loadingImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        try {
            //키오스크 하단 네비게이션 삭제.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().setDecorFitsSystemWindows(false);
            }
//            else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadingImageView = findViewById(R.id.loading_imageview);

        if (BuildConfig.IS_DEVEL) {
            Constants.IS_TEST = true;
        }
        if(BuildConfig.DEBUG){
            findViewById(R.id.logoImageView).setOnLongClickListener(v -> {
                startActivity(new Intent(this, TempActivity.class));
                return false;
            });
        }

        ((TextView) findViewById(R.id.versionTextView)).setText(BuildConfig.VERSION_NAME);

        //루팅 및 인터넷여부 확인
        if (AndroidUtils.rootCheck()) {
            final SweetAlertDialog dialog = new SweetAlertDialog(IntroActivity.this, SweetAlertDialog.ERROR_TYPE);
            //dialog.setTitleText("루팅된 폰은 사용할 수 없습니다");
            dialog.setContentText(getString(R.string.routing_msg));
            dialog.setCancelable(false);
            dialog.setConfirmClickListener(sDialog -> {
                if (dialog.isShowing()) dialog.dismiss();
                finish();
            });
            dialog.show();
        } else if (!AndroidUtils.isOnline()) {

            ConnectivityManager connMgr =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isWifiConn = false;
            boolean isMobileConn = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (Network network : connMgr.getAllNetworks()) {
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        isWifiConn |= networkInfo.isConnected();
                    }
                    if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        isMobileConn |= networkInfo.isConnected();
                    }
                }

                Log.d("DEBUG_TAG", "Wifi connected: " + isWifiConn);
                Log.d("DEBUG_TAG", "Mobile connected: " + isMobileConn);

                if (!isWifiConn && !isMobileConn) {
                    new MtouchDialog(this, v -> {
                        finish();
                    }, false).setContentText("네트워크에 연결되어있지 않습니다.\n다시 시도해 주세요.").show();
                    return;
                }
            } else {
                final SweetAlertDialog dialog = new SweetAlertDialog(IntroActivity.this, SweetAlertDialog.ERROR_TYPE);
                dialog.setContentText(getString(R.string.not_use_internet));
                dialog.setCancelable(false);
                dialog.setConfirmClickListener(sDialog -> {
                    if (dialog.isShowing()) dialog.dismiss();
                    finish();
                });
                dialog.show();
            }
        }


        //fcm
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        LOG.w("Fetching FCM registration token failed" + task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    LOG.d("token: " + token);

                    SharedPreferenceUtil.putData(IntroActivity.this, Constants.KEY_TOKEN, token);
                    Constants.TOKEN = token;

                });



        findViewById(R.id.signupButton).setOnClickListener(v -> {
            //sign up
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });

        findViewById(R.id.startButton).setOnClickListener(v->{
            //login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        //자동로그인
        if (SharedPreferenceUtil.getData(this, Constants.KEY_AUTO_LOGIN, "N").equals("Y")) {
            rotateStart(45);
            findViewById(R.id.contentTextView1).setVisibility(View.GONE);
            findViewById(R.id.contentTextView2).setVisibility(View.GONE);
            findViewById(R.id.startButton).setVisibility(View.GONE);
//            loadingImageView.setVisibility(View.VISIBLE);

            new Handler().postDelayed(()->{
                startActivity(new Intent(this, MainActivity.class));
                finish();

            },800);

        }
    }

    private void rotateStart(float degree){
        if(isFinishing()) return;
        loadingImageView.setRotation(degree+loadingImageView.getRotation());
        loadingImageView.invalidate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rotateStart(degree);
            }
        },120);
    }


}
