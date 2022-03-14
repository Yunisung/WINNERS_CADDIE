package com.bkwinners.ksnet.dpt.design;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.bkwinners.ksnet.dpt.design.auth.AuthFirstActivity;
import com.bkwinners.ksnet.dpt.design.legacy.AndroidUtils;
import com.bkwinners.ksnet.dpt.design.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.bluetooth.DeviceRegistActivity;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class IntroActivity extends DeviceCheckActivity {

    public static final String KEY = "Variable";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_copy);

        if (BuildConfig.IS_DEVEL) {
            Constants.IS_TEST = true;
        }

        ((TextView) findViewById(R.id.versionTextView)).setText(BuildConfig.VERSION_NAME);
        findViewById(R.id.logoImageView).setOnLongClickListener(v -> {
            startActivity(new Intent(this, HiddenOptionActivity.class));
            return false;
        });

        findViewById(R.id.resetButton).setOnClickListener(v -> {

            //블루투스 확인
            if (this.mBluetoothAdapter == null) {
                Toast.makeText(this, "블루투스를 사용할 수 없습니다.\n앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else if (!this.mBluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
                return;
            }

            //위치옵션 확인
            if (locationManager == null) {
                Toast.makeText(getApplicationContext(), "GPS를 사용할 수 없습니다.\n앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Toast.makeText(getApplicationContext(), "위치옵션을 설정해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "위치옵션을 설정해주시기 바랍니다.\n위치 인식 방식에서 GPS를 사용하는 옵션으로 설정해야 합니다.", Toast.LENGTH_LONG).show();
                }
                new Handler().postDelayed(() -> {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS);
                }, 500);
                return;
            }

            if(!requestPermission()){
                return;
            }

            SharedPreferenceUtil.putData(this, Constants.KEY_MAC_ADDRESS, "NONE");
            startActivityForResult(new Intent(this, DeviceRegistActivity.class), REQUEST_CODE_REGIST_ACTIVITY);
        });

        if (!getPackageName().contains("pay")) {
            setNeedRegist(false);
        }

        //체크ic 설치여부확인
        String appPackageName = "ksnet.kscic";
        if (!appInstalledOrNot(appPackageName) && !BuildConfig.IS_DEVEL && getPackageName().contains("pay")) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(IntroActivity.this, R.style.DialogTheme);
            LayoutInflater inflater = IntroActivity.this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.customdialog_layout, null);
            dialogBuilder.setView(dialogView);

            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.setCancelable(false);
            Window window = alertDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView txtView = (TextView) dialogView.findViewById(R.id.description);
            txtView.setText("케이에스체크IC를 설치해야합니다.\n설치화면으로 이동하시겠습니까?");
            Button ok_btn = (Button) dialogView.findViewById(R.id.button);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GotoGooglePlayer("ksnet.kscic");
                    alertDialog.hide();
                    finish();
                }
            });

            alertDialog.show();


//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                }

            return;
        }

        //루팅 및 인터넷여부 확인
        if (com.pswseoul.util.AndroidUtils.rootCheck()) {
            final SweetAlertDialog dialog = new SweetAlertDialog(IntroActivity.this, SweetAlertDialog.ERROR_TYPE);
            //dialog.setTitleText("루팅된 폰은 사용할 수 없습니다");
            dialog.setContentText(getString(R.string.routing_msg));
            dialog.setConfirmText("확인바랍니다");
            dialog.setCancelable(false);
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    if (dialog.isShowing()) dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
        } else if (!com.pswseoul.util.AndroidUtils.isOnline()) {
            final SweetAlertDialog dialog = new SweetAlertDialog(IntroActivity.this, SweetAlertDialog.ERROR_TYPE);
            dialog.setContentText(getString(R.string.not_use_internet));
            dialog.setConfirmText("확인바랍니다");
            dialog.setCancelable(false);
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    if (dialog.isShowing()) dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
        }


        //기존데이터 유지작업.
        if (AndroidUtils.getAppPreferences(this, "token") != null && AndroidUtils.getAppPreferences(this, "token").length() > 0) {
            SharedPreferenceUtil.putData(this, AndroidUtils.getAllAppPreference(this));
            SharedPreferenceUtil.putData(this, "key", AndroidUtils.getAppPreferences(this, "token"));
            AndroidUtils.clearAppPreference(this);
        }

//        if (getSharedPreferences(KEY, MODE_PRIVATE).getString("MAC_Adress", "").length() > 0) {
//            SharedPreferenceUtil.putData(this, getSharedPreferences(KEY, MODE_PRIVATE).getAll());
//            getSharedPreferences(KEY, MODE_PRIVATE).edit().clear().commit();
//        }

        if (getSharedPreferences("USERINFO", MODE_PRIVATE).getInt("FIRST_VIEW", 0) != 0) {
            SharedPreferenceUtil.putData(this, "FIRST_VIEW", "true");
            getSharedPreferences("USERINFO", MODE_PRIVATE).edit().clear().commit();
        }


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
        }


        //fcm구독
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

        String lastVersion = SharedPreferenceUtil.getVersion(this);
        LOG.w("lastVersion: " + lastVersion);
        if (!BuildConfig.VERSION_NAME.equals(lastVersion)) {
//            Set<String> topicList = SharedPreferenceUtil.getSetStringData(this, Constants.KEY_TOPIC_LIST);
//            if (topicList != null) {
//                for (String topic : topicList)
//                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
//                SharedPreferenceUtil.removeData(this, Constants.KEY_TOPIC_LIST);
//            }
            if (lastVersion != null && !lastVersion.equals("0")) {
                LOG.w("lastVersion unsubscribe ");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(lastVersion);
            }

            FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.VERSION_NAME)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            LOG.w("subscribe " + BuildConfig.VERSION_NAME);
                            SharedPreferenceUtil.putVersion(this);
//                            Set<String> topicSet = new HashSet<>();
//                            topicSet.add(BuildConfig.VERSION_NAME);
//                            SharedPreferenceUtil.putSetStringData(this,Constants.KEY_TOPIC_LIST,topicSet);
                        }
                    });
        }


        if (SharedPreferenceUtil.getData(this, "key", "").equals("")) {
            initialize();

            findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(IntroActivity.this, AuthFirstActivity.class));
                    finish();
                }
            });
        } else {
            startActivity(new Intent(this, OldMainActivity.class));
            finish();
        }
    }

    public void GotoGooglePlayer(String pakage) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pakage));   //getPackageName();
        startActivity(intent);
        finish();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
