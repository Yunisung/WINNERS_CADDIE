package com.mtouch.caddie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.mtouch.caddie.network.CaddieAPIService;
import com.mtouch.caddie.network.MtouchLoadingDialog;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.ksnet.dpt.Toasty;
import com.mtouch.ksnet.dpt.action.obj.responseObj;
import com.mtouch.ksnet.dpt.design.DeviceCheckActivity;
import com.mtouch.ksnet.dpt.design.HistoryActivity;
import com.mtouch.ksnet.dpt.design.InfomationActivity;
import com.mtouch.ksnet.dpt.design.PaymentCompleteActivity;
import com.mtouch.ksnet.dpt.design.SettingActivity;
import com.mtouch.ksnet.dpt.design.TotalActivity;
import com.mtouch.ksnet.dpt.design.appToApp.ResponseObj;
import com.mtouch.ksnet.dpt.design.appToApp.network.APIService;
import com.mtouch.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.mtouch.ksnet.dpt.design.appToApp.network.model.Request;
import com.mtouch.ksnet.dpt.design.util.GsonUtil;
import com.mtouch.ksnet.dpt.design.util.LOG;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;
import com.mtouch.ksnet.dpt.ks03.pay.ksnet.KSNETStatus;
import com.pswseoul.util.SunUtil;
import com.pswseoul.util.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mtouch.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_FAIL_CEHCK;
import static com.mtouch.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_SERVER_SEND;
import static com.mtouch.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.TEST;
import static com.mtouch.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.menu_status;

public class MainActivity extends DeviceCheckActivity {

    public static final String TAG = "MainActivity";

    public static final int REQUEST_CODE_CREDIT_PAYMENT = 101;
    public static final int REQUEST_CODE_DIRECT_PAYMENT = 102;
    public static final int REQUEST_CODE_SETTING = 103;
    public static final int REQUEST_CODE_COMPLETE = 104;
    public static final int REQUEST_CODE_CASH_PAYMENT = 105;
    public static final int REQUEST_CODE_HISTORY_PAYMENT = 106;

    protected MtouchLoadingDialog loadingDialog;

    private DrawerLayout drawerlayout;
    private com.google.android.material.navigation.NavigationView nav_view;
    private ImageView menuCloseButton;
    private ImageView logoImageView;
    private TextView menuTerminalIdTextView;
    private TextView menuMchtNameTextView;

    private TextView bankNameTextView;
    private TextView accountTextView;
    private TextView accountHolderTextView;

    private LinearLayout mainButton;
    private LinearLayout menuPaymentButton;
    private LinearLayout menuHistoryButton;
    private LinearLayout menuTotalButton;
    private LinearLayout menuServiceButton;
    private LinearLayout menuSettingButton;
    // Content View Elements

    private ImageView menuImageView;
    private TextView mchtNameTextView;
    private TextView terminalIdTextView;
    private TextView dailyTotalTextView;
    private TextView dailyCancelTotalTextView;
    private TextView mouthlyTotalTextView;
    private TextView mouthlyCancelTotalTextView;
    private LinearLayout creditPaymentButton;
    private LinearLayout cashPaymentButton;
    private LinearLayout historyButton;
    private LinearLayout totalButton;
    private TextView serviceButton;
    private TextView settingButton;

    private APIService mAPIService;
    private APIService mAPIDirectService;
    private APIService mAPISMSService;
    private CaddieAPIService caddieAPIService;

    private String ccName;

    private void bindViews() {
        menuCloseButton = findViewById(R.id.menuCloseButton);
        drawerlayout = findViewById(R.id.drawerlayout);
        nav_view = findViewById(R.id.nav_view);
        logoImageView = findViewById(R.id.logoImageView);
        menuTerminalIdTextView = findViewById(R.id.menuTerminalIdTextView);
        menuMchtNameTextView = findViewById(R.id.menuMchtNameTextView);
        mainButton = findViewById(R.id.mainButton);
        menuPaymentButton = findViewById(R.id.menuPaymentButton);
        menuHistoryButton = findViewById(R.id.menuHistoryButton);
        menuTotalButton = findViewById(R.id.menuTotalButton);
        menuServiceButton = findViewById(R.id.menuServiceButton);
        menuSettingButton = findViewById(R.id.menuSettingButton);

        bankNameTextView = findViewById(R.id.bankNameTextView);
        accountTextView = findViewById(R.id.accountTextView);
        accountHolderTextView = findViewById(R.id.accountHolderTextView);

        menuImageView = (ImageView) findViewById(R.id.menuImageView);
        mchtNameTextView = (TextView) findViewById(R.id.mchtNameTextView);
        terminalIdTextView = (TextView) findViewById(R.id.terminalIdTextView);
        dailyTotalTextView = (TextView) findViewById(R.id.dailyTotalTextView);
        dailyCancelTotalTextView = (TextView) findViewById(R.id.dailyCancelTotalTextView);
        mouthlyTotalTextView = (TextView) findViewById(R.id.mouthlyTotalTextView);
        mouthlyCancelTotalTextView = (TextView) findViewById(R.id.mouthlyCancelTotalTextView);
        creditPaymentButton = (LinearLayout) findViewById(R.id.creditPaymentButton);
        cashPaymentButton = (LinearLayout) findViewById(R.id.cashPaymentButton);
        historyButton = (LinearLayout) findViewById(R.id.historyButton);
        totalButton = (LinearLayout) findViewById(R.id.totalButton);
        serviceButton = (TextView) findViewById(R.id.serviceButton);
        settingButton = (TextView) findViewById(R.id.settingButton);

        //menu
        mainButton.setOnClickListener(this::onClickMenu);
        menuPaymentButton.setOnClickListener(this::onClickMenu);
        menuHistoryButton.setOnClickListener(this::onClickMenu);
        menuTotalButton.setOnClickListener(this::onClickMenu);
        menuServiceButton.setOnClickListener(this::onClickMenu);
        menuSettingButton.setOnClickListener(this::onClickMenu);

        //main
        creditPaymentButton.setOnClickListener(this::onClickMain);
        cashPaymentButton.setOnClickListener(this::onClickMain);
        historyButton.setOnClickListener(this::onClickMain);
        totalButton.setOnClickListener(this::onClickMain);
        serviceButton.setOnClickListener(this::onClickMain);
        settingButton.setOnClickListener(this::onClickMain);

        menuImageView.setOnClickListener(v -> {
            if (drawerlayout.isDrawerOpen(GravityCompat.START)) {
                drawerlayout.closeDrawer(GravityCompat.START);
            } else {
                drawerlayout.openDrawer(GravityCompat.START);
            }
        });
        menuCloseButton.setOnClickListener(v -> {
            drawerlayout.closeDrawer(GravityCompat.START);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingDialog = new MtouchLoadingDialog(this);

        bindViews();
        caddieInit();
//        init();
    }

    protected void showLoading(){
        try {
            if (!isFinishing() && !loadingDialog.isShowing() && loadingDialog != null) {
                loadingDialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void hideLoading(){
        try {
            if (!isFinishing() && loadingDialog != null) {
                loadingDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClickMenu(View view) {
        int id = view.getId();
        if (id == R.id.mainButton) {
//            Toast.makeText(this, "mainButton", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menuPaymentButton) {
            Intent intent =new Intent(this, OrderActivity.class);
            startActivity(intent);
        } else if (id == R.id.menuHistoryButton) {
            startActivityForResult(new Intent(this, HistoryActivity.class), REQUEST_CODE_HISTORY_PAYMENT);
        } else if (id == R.id.menuTotalButton) {
            startActivity(new Intent(this, TotalActivity.class));
        } else if (id == R.id.menuServiceButton) {
            startActivity(new Intent(this, InfomationActivity.class));
        } else if (id == R.id.menuSettingButton) {
            startActivityForResult(new Intent(this, SettingActivity.class), REQUEST_CODE_SETTING);
        }
        drawerlayout.closeDrawer(GravityCompat.START);
    }

    public void onClickMain(View view) {
        int id = view.getId();
        if (id == R.id.creditPaymentButton) {
            Intent intent =new Intent(this, OrderActivity.class);
            startActivity(intent);
        } else if (id == R.id.cashPaymentButton) {
            startActivity(new Intent(this, SchedualActivity.class));
        } else if (id == R.id.historyButton) {
            startActivityForResult(new Intent(this, HistoryActivity.class), REQUEST_CODE_HISTORY_PAYMENT);
        } else if (id == R.id.totalButton) {
            startActivity(new Intent(this, TotalActivity.class));
        } else if (id == R.id.serviceButton) {
            startActivity(new Intent(this, InfomationActivity.class));
        } else if (id == R.id.settingButton) {
            startActivityForResult(new Intent(this, SettingActivity.class), REQUEST_CODE_SETTING);
        }
    }

    private void caddieInit() {
        caddieAPIService = NetworkManager.getAPIService(this);

        HashMap<String, Object> map = new HashMap<>();
        map.put("token", Constants.TOKEN);
        showLoading();
        caddieAPIService.check(map).enqueue(new Callback<com.mtouch.caddie.network.model.Response>() {
            @Override
            public void onResponse(Call<com.mtouch.caddie.network.model.Response> call, Response<com.mtouch.caddie.network.model.Response> response) {
                hideLoading();
                try {
                    if (response.isSuccessful()) {
                        if (response.body().isSuccess()) {
                            String tmnId = (String) response.body().getData().get("tmnId");
                            String serial = (String) response.body().getData().get("serial");
                            String mchtId = (String) response.body().getData().get("mchtId");
                            ccName = (String) response.body().getData().get("ccName");

                            SharedPreferenceUtil.putData(MainActivity.this, "tmnId", tmnId);
                            SharedPreferenceUtil.putData(MainActivity.this, "serial", serial);
                            SharedPreferenceUtil.putData(MainActivity.this, "mchtId", mchtId);
                            SharedPreferenceUtil.putData(MainActivity.this, "ccName", ccName);

                            init();
                        } else {
                            String resultMsg = response.body().getResultMsg();
                            SharedPreferenceUtil.removeData(MainActivity.this, Constants.KEY_AUTO_LOGIN);
                            new MtouchDialog(MainActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                        }
                    } else {
                        String resultMsg = new String(response.errorBody().bytes());
                        new MtouchDialog(MainActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<com.mtouch.caddie.network.model.Response> call, Throwable t) {
                hideLoading();
                String resultMsg = getString(R.string.network_error_msg);
                new MtouchDialog(MainActivity.this, v -> finish()).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
            }
        });

    }

    private void init() {

        setNeedRegist(false);
        initialize();

        mAPIService = ApiUtils.getAPIService();
        mAPIDirectService = ApiUtils.getAPIDirectService();
        mAPISMSService = ApiUtils.getSMSSendService();

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
        menuMchtNameTextView.setText(SharedPreferenceUtil.getData(this, "name"));
        menuTerminalIdTextView.setText(SharedPreferenceUtil.getData(this, "tmnId"));
        mchtNameTextView.setText(SharedPreferenceUtil.getData(this, "name"));
        terminalIdTextView.setText(SharedPreferenceUtil.getData(this, "tmnId"));

        bankNameTextView.setText(SharedPreferenceUtil.getData(this, "bankName"));
        accountTextView.setText(SharedPreferenceUtil.getData(this, "account"));
        accountHolderTextView.setText(SharedPreferenceUtil.getData(this, "accntHolder"));



        HashMap<String, Object> param = new HashMap<>();
        param.put("tmnId", SharedPreferenceUtil.getData(this, "tmnId"));
        param.put("serial", SharedPreferenceUtil.getData(this, "serial"));
        param.put("mchtId", SharedPreferenceUtil.getData(MainActivity.this, "mchtId"));
        param.put("appId", getPackageName());
        param.put("version", BuildConfig.VERSION_NAME);
        param.put("telNo", "");

        showLoading();

        caddieAPIService.getStringToken(param).enqueue(new Callback<com.mtouch.caddie.network.model.Response>() {

            @Override
            public void onResponse(Call<com.mtouch.caddie.network.model.Response> call, retrofit2.Response<com.mtouch.caddie.network.model.Response> response) {
                hideLoading();
                if(response.isSuccessful()) {
                    HashMap<String, Object> responseData = response.body().getData();

                    //save
                    if(responseData.get("agencyEmail") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "agencyEmail", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "agencyEmail", responseData.get("agencyEmail").toString());

                    if(responseData.get("distEmail") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "distEmail", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "distEmail", responseData.get("agencyEmail").toString());

                    if(responseData.get("tmnId") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "tmnId", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "tmnId", responseData.get("tmnId").toString());

                    if(responseData.get("vat") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "vat", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "vat", responseData.get("vat").toString());

                    if(responseData.get("agencyTel") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "agencyTel", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "agencyTel", responseData.get("agencyTel").toString());

                    if(responseData.get("telNo") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "telNo", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "telNo", responseData.get("telNo").toString());

                    if(responseData.get("agencyName") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "agencyName", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "agencyName", responseData.get("agencyName").toString());

                    if(responseData.get("phoneNo") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "phoneNo", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "phoneNo", responseData.get("phoneNo").toString());

                    if(responseData.get("result") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "result", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "result", responseData.get("result").toString());

                    if(responseData.get("Authorization") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "Authorization", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "Authorization", responseData.get("Authorization").toString());

                    if(responseData.get("apiMaxInstall") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "apiMaxInstall", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "apiMaxInstall", responseData.get("apiMaxInstall").toString());

                    if(responseData.get("distTel") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "distTel", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "distTel", responseData.get("distTel").toString());

                    if(responseData.get("semiAuth") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "semiAuth", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "semiAuth", responseData.get("semiAuth").toString());

                    if(responseData.get("identity") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "identity", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "identity", responseData.get("identity").toString());

                    if(responseData.get("appDirect") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "appDirect", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "appDirect", responseData.get("appDirect").toString());

                    if(responseData.get("name") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "name", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "name", responseData.get("name").toString());

                    if(responseData.get("distName") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "distName", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "distName", responseData.get("distName").toString());

                    if(responseData.get("payKey") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "payKey", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "payKey", responseData.get("payKey").toString());

                    if(responseData.get("ceoName") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "ceoName", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "ceoName", responseData.get("ceoName").toString());

                    if(responseData.get("addr") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "addr", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "addr", responseData.get("addr").toString());

                    if(responseData.get("key") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "key", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "key", responseData.get("key").toString());

                    if(responseData.get("bankName") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "bankName", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "bankName", responseData.get("bankName").toString());

                    if(responseData.get("accntHolder") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "accntHolder", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "accntHolder", responseData.get("accntHolder").toString());

                    if(responseData.get("account") == null)
                        SharedPreferenceUtil.putData(MainActivity.this, "account", "");
                    else
                        SharedPreferenceUtil.putData(MainActivity.this, "account", responseData.get("account").toString());

                    if(responseData.get("key") == null)
                        KSNETStatus.token = "";
                    else
                        KSNETStatus.token = responseData.get("key").toString();

                    menuMchtNameTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "name"));
                    menuTerminalIdTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "tmnId"));
                    mchtNameTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "name"));
                    terminalIdTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "tmnId"));

                    bankNameTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "bankName"));
                    accountTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "account"));
                    accountHolderTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "accntHolder"));

                    /* getSummay추가 */
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("tmnId", SharedPreferenceUtil.getData(MainActivity.this, "tmnId"));
                    params.put("mchtId", SharedPreferenceUtil.getData(MainActivity.this, "mchtId"));

                    caddieAPIService.getSummary(params).enqueue(new Callback<com.mtouch.caddie.network.model.Response>() {
                        @Override
                        public void onResponse(Call<com.mtouch.caddie.network.model.Response> call, Response<com.mtouch.caddie.network.model.Response> response) {
                            hideLoading();
                            HashMap<String, Object> responseData = response.body().getData();

                            int monthPay = Integer.parseInt(responseData.get("monthPay").toString());
                            int monthRef = Integer.parseInt(responseData.get("monthRef").toString());
                            int dayPay = Integer.parseInt(responseData.get("dayPay").toString());
                            int dayRef = Integer.parseInt(responseData.get("dayRef").toString());

                            mouthlyTotalTextView.setText(String.format("%,d", monthPay) + " 원");
                            mouthlyCancelTotalTextView.setText(String.format("%,d", monthRef) + " 원");
                            dailyTotalTextView.setText(String.format("%,d", dayPay) + " 원");
                            dailyCancelTotalTextView.setText(String.format("%,d", dayRef) + " 원");
                        }

                        @Override
                        public void onFailure(Call<com.mtouch.caddie.network.model.Response> call, Throwable t) {
                            hideLoading();
                            String resultMsg = t.getMessage();
                            new MtouchDialog(MainActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<com.mtouch.caddie.network.model.Response> call, Throwable t) {
                hideLoading();
                String resultMsg = t.getMessage();
                new MtouchDialog(MainActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
            }
        });


//        Request request = new Request()
//                .put("tmnId", SharedPreferenceUtil.getData(this, "tmnId"))
//                .put("serial", SharedPreferenceUtil.getData(this, "serial"))
//                .put("mchtId", SharedPreferenceUtil.getData(MainActivity.this, "mchtId"))
//                .put("appId", getPackageName())
//                .put("version", BuildConfig.VERSION_NAME)
//                .put("telNo", "");
//        ApiUtils.getAPIService().getStringToken("", request).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    if (response.isSuccessful()) {
//                        String responseData = new String(response.body().bytes());
//                        ResponseObj responseobj = (ResponseObj) GsonUtil.fromJson(responseData, ResponseObj.class);
//
//                        //save
//                        SharedPreferenceUtil.putData(MainActivity.this, "agencyEmail", responseobj.geKeyValye("agencyEmail"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "distEmail", responseobj.geKeyValye("distEmail"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "tmnId", responseobj.geKeyValye("tmnId"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "vat", responseobj.geKeyValye("vat"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "agencyTel", responseobj.geKeyValye("agencyTel"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "telNo", responseobj.geKeyValye("telNo"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "agencyName", responseobj.geKeyValye("agencyName"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "phoneNo", responseobj.geKeyValye("phoneNo"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "result", responseobj.geKeyValye("result"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "Authorization", responseobj.geKeyValye("Authorization"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "apiMaxInstall", responseobj.geKeyValye("apiMaxInstall"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "distTel", responseobj.geKeyValye("distTel"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "semiAuth", responseobj.geKeyValye("semiAuth"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "identity", responseobj.geKeyValye("identity"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "appDirect", responseobj.geKeyValye("appDirect"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "name", responseobj.geKeyValye("name"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "distName", responseobj.geKeyValye("distName"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "payKey", responseobj.geKeyValye("payKey"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "ceoName", responseobj.geKeyValye("ceoName"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "addr", responseobj.geKeyValye("addr"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "key", responseobj.geKeyValye("key"));
//
//                        SharedPreferenceUtil.putData(MainActivity.this, "bankName", responseobj.geKeyValye("bankName"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "accntHolder", responseobj.geKeyValye("accntHolder"));
//                        SharedPreferenceUtil.putData(MainActivity.this, "account", responseobj.geKeyValye("account"));
//                        KSNETStatus.token = responseobj.geKeyValye("key");
//
//
//                        menuMchtNameTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "name"));
//                        menuTerminalIdTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "tmnId"));
//                        mchtNameTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "name"));
//                        terminalIdTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "tmnId"));
//
//                        bankNameTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "bankName"));
//                        accountTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "account"));
//                        accountHolderTextView.setText(SharedPreferenceUtil.getData(MainActivity.this, "accntHolder"));
//
//
//                        //-------------
//                        /**
//                         * {
//                         *   "data": {
//                         *     "list": [
//                         *     ],
//                         *     "result": "조회성공",
//                         *     "_idx": "1",
//                         *     "today": "20200324",
//                         *     "monthPay": "2200000", //월 결제금액
//                         *     "monthPayCnt": "1",  //월 결제건수
//                         *     "monthRef": "0",     //월 취소금액
//                         *     "monthRefCnt": "0",  //월 취소건수
//                         *     "dayPayCnt": "0",    //일 결제건수
//                         *     "dayPay": "0",       //일 결제금액
//                         *     "dayRefCnt": "0",    //일 취소건수
//                         *     "dayRef": "0",       //일 취소금액
//                         *   }
//                         * }
//                         */
//                        Request req = new Request()
//                                .put("tmnId", SharedPreferenceUtil.getData(MainActivity.this, "tmnId"))
//                                .put("mchtId", SharedPreferenceUtil.getData(MainActivity.this, "mchtId"));
//
//                        Log.d("DDDDDDDDDDDDDDDDD", req.toJsonString());
//
//                        ApiUtils.getAPIService().getSummary(SharedPreferenceUtil.getData(MainActivity.this, "key"), req).enqueue(new Callback<ResponseBody>() {
//                            @Override
//                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                hideLoading();
//                                try {
//                                    if (response.isSuccessful()) {
//                                        String responseData = new String(response.body().bytes());
//                                        ResponseObj responseObj = (ResponseObj) GsonUtil.fromJson(responseData, ResponseObj.class);
//
//                                        int monthPay = Integer.parseInt(responseObj.getStringValue("monthPay"));
//                                        int monthRef = Integer.parseInt(responseObj.getStringValue("monthRef"));
//                                        int dayPay = Integer.parseInt(responseObj.getStringValue("dayPay"));
//                                        int dayRef = Integer.parseInt(responseObj.getStringValue("dayRef"));
//
//                                        mouthlyTotalTextView.setText(String.format("%,d", monthPay) + " 원");
//                                        mouthlyCancelTotalTextView.setText(String.format("%,d", monthRef) + " 원");
//                                        dailyTotalTextView.setText(String.format("%,d", dayPay) + " 원");
//                                        dailyCancelTotalTextView.setText(String.format("%,d", dayRef) + " 원");
//
//
//                                    } else {
//                                        String resultMsg = new String(response.errorBody().bytes());
//                                        new MtouchDialog(MainActivity.this).setTitleText("알림").setContentText(resultMsg).show();
//                                    }
//
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                hideLoading();
//                                String resultMsg = t.getMessage();
//                                new MtouchDialog(MainActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
//                            }
//                        });
//                        //-------------
//
//
//                    } else {
//                        String resultMsg = new String(response.errorBody().bytes());
//                        new MtouchDialog(MainActivity.this).setTitleText("알림").setContentText(resultMsg).show();
//                    }
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                hideLoading();
//                String resultMsg = t.getMessage();
//                new MtouchDialog(MainActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
//            }
//        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CREDIT_PAYMENT) {
            if (resultCode == RESULT_OK) {

                try {
                    JSONObject json = new JSONObject();
                    json.put("amount", data.getStringExtra("amount"));
                    json.put("installment", data.getStringExtra("installment"));
                    json.put("secondKey", data.getStringExtra("dptId"));
                    json.put("van", data.getStringExtra("van"));
                    json.put("vanId", data.getStringExtra("vanId"));
                    json.put("trackId", data.getStringExtra("trackId"));
                    ksNetMessage(REQUEST_APPROVE_KSNET, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                
            }
        } else if (requestCode == REQUEST_CODE_HISTORY_PAYMENT) {
            if (resultCode == RESULT_OK) {
            }
        } else if (requestCode == REQUEST_CODE_DIRECT_PAYMENT) {
            if (resultCode == RESULT_OK) {
                String amount = data.getStringExtra("amount");
                String installment = data.getStringExtra("installment");
                LOG.d("amount: " + amount + " installment: " + installment);

            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == REQUEST_CODE_SETTING) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, IntroActivity.class));
                finish();
            } else if (resultCode == RESULT_CANCELED) {

            }
        } else if (requestCode == REQUEST_CODE_COMPLETE) {
            init();

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("trxId", data.getStringExtra("trxId"));
                    json.put("authCd", data.getStringExtra("authCd"));
                    json.put("regDay", data.getStringExtra("regDay"));
                    json.put("secondKey", data.getStringExtra("secondKey"));

                    json.put("amount", data.getStringExtra("amount"));
                    json.put("installment", data.getStringExtra("installment"));
//                    json.put("secondKey", data.getStringExtra("dptId"));
                    json.put("van", data.getStringExtra("van"));
                    json.put("vanId", data.getStringExtra("vanId"));
                    json.put("trackId", data.getStringExtra("trackId"));

                    ksNetMessage(REQUEST_APPROVE_CANCEL_KSNET, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_APPROVE_KSNET) {
            com.mtouch.ksnet.dpt.ks03.obj.Request rs = new com.mtouch.ksnet.dpt.ks03.obj.Request();   //
            rs.data.put("type", "승인실패");

            rs.data.put("amount", ksnetresp.getString("TotalAmount").trim());  // 서버에 보내기 위하여 부가세 포함가를 사용
            rs.data.put("installment", ksnetresp.getString("installment").trim());

            try {
                rs.data.put("van", ksnetresp.getString("van").trim());
                rs.data.put("vanId", ksnetresp.getString("vanId").trim());
                rs.data.put("vanTrxId", ksnetresp.getString("trackId").trim());
                rs.data.put("trackId", ksnetresp.getString("trackId").trim());
            } catch (Exception e) {
            }

            if (data != null) {

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String Message1 = "NOT";
                String Message2 = "NOT";

                Bundle extra = data.getExtras();

                responseObj responseobj = new responseObj();
                responseobj = (responseObj) com.pswseoul.util.GsonUtil.fromJson(extra.get("resultData").toString(), new responseObj());

                // HashMap<String, String> mhashApprove = (HashMap<String, String>) data.getSerializableExtra("result");
                HashMap<String, String> mhashApprove = responseobj.geMap();
                if (mhashApprove != null) {
                    Iterator<String> iterator = mhashApprove.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        LOG.w("debug", "key=" + key);
                        LOG.w("debug", " value=" + mhashApprove.get(key));

                        ksnetresp.put(key, mhashApprove.get(key));
                    }
                }


                if (responseobj.resultCd.equals("1")) {
                    Message1 = mhashApprove.get("Message1").trim();
                    Message2 = mhashApprove.get("Message2").trim();
                }
                rs.data.put("resultMsg", Message1 + ":" + Message2);
/*
성공시에는 직전 취소 인지만 구분합니다
 */
                if (resultCode == Activity.RESULT_OK) {

                    // String telegrame = mhashApprove.get("TelegramType");  // 승인 취소 구분
                    // System.out.println("telegrame 809: "+ telegrame );

                    if (Message2.indexOf("OK") >= 0) {   // 성공입니다
                        rs.data.put("type", "승인".trim());

                        Toasty.success(this, "정상 승인 되었습니다", Toast.LENGTH_LONG, true).show();

                        rs.data.put("number", "****************".trim());
                        if (mhashApprove.get("CardNo") != null && mhashApprove.get("CardNo").length() > 5) {
                            if (tools.CheckNumber(mhashApprove.get("CardNo").substring(0, 5))) {
                                rs.data.put("number", mhashApprove.get("CardNo").trim());
                            }
                        }

                        rs.data.put("authCd", ksnetresp.getString("AuthNum").trim());    //  승인 번호
                        rs.data.put("regDate", ksnetresp.getString("Authdate").trim());    //  승인 번호

                        rs.data.put("issuerCode", mhashApprove.get("IssueCode"));
                        rs.data.put("acquirerCode", mhashApprove.get("PurchaseCode"));


                        ksnetresp.put("prevAuthNum", mhashApprove.get("AuthNum"));
                        ksnetresp.put("prevAuthDate", mhashApprove.get("Authdate"));
                        ksnetresp.put("prevClassfication", mhashApprove.get("Classification"));


                        String ksnetResponseData = ksnetresp.toJsonString();

                        //       KSNETStatus.getInstance(this).KSApproveSystem(mhashApprove, ksnetresp);  //값 출력
                        //                        SendDataString(ksnetresp.PrnData());  //출력
                        //" 신용카드전표",

                        KSNETStatus.name = ksnetresp.getString("name");
                        KSNETStatus.ceoName = ksnetresp.getString("ceoName");
                        KSNETStatus.identity = ksnetresp.getString("identity");
                        KSNETStatus.telNo = ksnetresp.getString("telNo");
                        KSNETStatus.addr = ksnetresp.getString("addr");


                        if (mhashApprove.get("Classification").equals("HK")) {
                            //<<<<<<<<<<<<<<<<<<---------------------------------- 현금영수증
                            prnSendData(" 현금영수증",
                                    ksnetresp.PrnTempleteData(MainActivity.this, 0,
                                            ksnetresp.getString("vat")));


                            for (String key : rs.data.keySet()) {
                                if (!ksnetresp.data.containsKey(key)) {
                                    ksnetresp.data.put(key, (String) rs.data.get(key));
                                }
                            }


                            //결과화면 데이터 전달
                            APPROVE_RESULT_INFO(ksnetresp.toJsonString());

                            //----------------------------------->>>>>>>>>>>>>>>>>>>현금영수증
                        } else {
                            //<<<<<<<<<<<<<<<<<<----------------------------------- 일반결제 승인일경우
                            prnSendData(" 신용카드전표",
                                    ksnetresp.PrnTempleteData(MainActivity.this, 0,
                                            ksnetresp.getString("vat")));

                            rs.data.put("sendcount", "0".trim());

                            if (TEST) {
                                SunUtil.setAppPreferences(this, "approve", rs.toJsonString());
                            }


                            for (String key : rs.data.keySet()) {
                                if (!ksnetresp.data.containsKey(key)) {
                                    ksnetresp.data.put(key, (String) rs.data.get(key));
                                }
                            }

                            //결과화면 데이터 전달 서버등록후 화면 출력
//                            APPROVE_RESULT_INFO(ksnetresp.toJsonString());
//2019-04-04
                            sendMessage(NETWORK_SERVER_SEND, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다

                            //----------------------------------->>>>>>>>>>>>>>>>>>> 일반결제 승인일경우
                        }

                                                                /*
                            try {

                                 _jsCallHandler.payResult((JsonHelper.toJSON(ksnetresp.getMap())).toString());  // 웹에 보냅니다
                            } catch ( Exception e) {
                                e.printStackTrace();
                            }
*/
                    } else {    // 실패를 이야기 합니다

                        //   telegrame = mhashApprove.get("TelegramType");

                        //    System.out.println("telegrame 877: "+ telegrame );

                        sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
                        ksnetresp.put("STX", "");
//                        _jsCallHandler.payResult(rs.toJsonString());  // 웹에 보냅니다

                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                /*이곳에서 서비로 데이터를 전송합니다*/
//# 전송 정보,type : '승인', number 카드번호, van ,vanId, amount ,vanTrxId 는 전표번호, authCd 는 승인번호, trackId 는 trx/rule 에서 회신 받은 trackId, regDate 는 전달 받은 시간 ,
//# 전송 정보,type : '승인실패', number 카드번호는 있을 경우 , van ,vanId, amount ,vanTrxId 는 전표번호, resultMsg는 사용자 취소 또는 KSNET 전달된 message1,message2함께, trackId 는 trx/rule 에서 회신 받은 trackId, regDate 는 전달 받은 시간 ,


            } else if (resultCode == RESULT_FIRST_USER && data != null) {
//                      Toast.makeText(this, "케이에스체크IC 에서 가맹점 다운로드 후 사용하시기 바랍니다", Toast.LENGTH_LONG).show();
                Toasty.error(this, "케이에스체크IC 에서 가맹점 다운로드 후 사용하시기 바랍니다", Toast.LENGTH_LONG);
            } else {
                rs.data.put("type", "승인실패");
                rs.data.put("resultMsg", "응답값 리턴 실패");
                rs.data.put("regDate", tools.getSysDateTIme());
//                sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
//                        Toast.makeText(this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
                Toasty.error(this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
//                _jsCallHandler.forceGoHome();

                /* 사용자 실퍠* */
//# 응답 시 200 인 경우 수신되는 van,vanId,trackId,secondKey,authCd,regDay 는 Application 으로 전달되어야 하며 secondKey 의 경우 KSNET에서 사용한다.
            }
            // 수행을 제대로 하지 못한 경우
            if (resultCode == RESULT_CANCELED) {
                rs.data.put("type", "승인실패");
                rs.data.put("resultMsg", "취소키가 눌러짐");
                rs.data.put("regDate", tools.getSysDateTIme());
                Toasty.error(this, "취소가 되었습니다", Toast.LENGTH_SHORT).show();
                sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
//                _jsCallHandler.forceGoHome();
            }
        } else if (requestCode == REQUEST_APPROVE_CANCEL_KSNET) {
            com.mtouch.ksnet.dpt.ks03.obj.Request rs = new com.mtouch.ksnet.dpt.ks03.obj.Request();   //
            rs.data.put("type", "승인실패");
            rs.data.put("van", ksnetresp.getString("van"));
            rs.data.put("vanId", ksnetresp.getString("vanId"));
            rs.data.put("amount", ksnetresp.getString("amount"));
            rs.data.put("vanTrxId", ksnetresp.getString("trackId"));
            rs.data.put("trackId", ksnetresp.getString("trackId"));
            rs.data.put("trxId", ksnetresp.getString("trxId"));
            rs.data.put("installment", "00");

            if (data != null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Bundle extra = data.getExtras();
                responseObj responseobj = new responseObj();
                responseobj = (responseObj) com.pswseoul.util.GsonUtil.fromJson(extra.get("resultData").toString(), new responseObj());

                // HashMap<String, String> mhashApprove = (HashMap<String, String>) data.getSerializableExtra("result");
                HashMap<String, String> mhashApprove = responseobj.geMap();

                Iterator<String> iterator = mhashApprove.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    Log.d("debug", "key=" + key);
                    Log.d("debug", " value=" + mhashApprove.get(key));
                    ksnetresp.put(key, mhashApprove.get(key));
                }

                String Message1 = mhashApprove.get("Message1");
                String Message2 = mhashApprove.get("Message2");
                rs.data.put("resultMsg", Message1 + ":" + Message2);

                if (resultCode == Activity.RESULT_OK) {

                    if (Message2.indexOf("OK") >= 0) {   // 성공입니다
                        Toasty.success(this, "승인 취소 되었습니다", Toast.LENGTH_LONG, true).show();

                        rs.data.put("installment", ksnetresp.getString("installment"));
                        rs.data.put("type", "승인취소");

                        rs.data.put("number", "****************");
                        if (mhashApprove.get("CardNo").length() > 5) {
                            if (tools.CheckNumber(mhashApprove.get("CardNo").substring(0, 5))) {
                                rs.data.put("number", mhashApprove.get("CardNo"));
                            }
                        }
                        // rs.data.put("number", mhashApprove.get("CardNo"));
                        //rs.data.put("vanTrxId", ksnetresp.getString("Full_Text_Num"));    //  ? 무슨 값을 넣지

                        rs.data.put("authCd", ksnetresp.getString("AuthNum").trim());    //  승인 번호
                        rs.data.put("regDate", ksnetresp.getString("Authdate"));    //  승인 번호

                        rs.data.put("issuerCode", mhashApprove.get("IssueCode"));
                        rs.data.put("acquirerCode", mhashApprove.get("PurchaseCode"));

                        //        rs.data.put("AuthNum", ksnetresp.getString("AuthNum"));    //  승인 번호
                        //         rs.data.put("Authdate", ksnetresp.getString("Authdate"));    //  승인 번호

                        ksnetresp.put("prevAuthNum", mhashApprove.get("AuthNum"));
                        ksnetresp.put("prevAuthDate", mhashApprove.get("Authdate"));
                        ksnetresp.put("prevClassfication", mhashApprove.get("Classification"));
/*
                            ksnetresp.prevAuthNum = mhashApprove.get("AuthNum");
                            ksnetresp.prevAuthDate =mhashApprove.get("Authdate");
                            ksnetresp.prevClassfication = mhashApprove.get("Classification");
*/
                        String ksnetResponseData = ksnetresp.toJsonString();
                        KSNETStatus.getInstance(this).KSApproveSystem(mhashApprove, ksnetresp);  //값 출력
                        if (TEST) {
                            SunUtil.setAppPreferences(this, "approve", rs.toJsonString());
                        }

                        for (String key : rs.data.keySet()) {
//                            if (!ksnetresp.data.containsKey(key)) {
                            ksnetresp.data.put(key, (String) rs.data.get(key));
//                            }
                        }

                        //결과화면 데이터 전달
                        //서버 등록후 화면출력
//                        APPROVE_RESULT_INFO(ksnetresp.toJsonString());
                        Log.d("tag", "==================ksnetresp.toJsonString()====================" + ksnetresp.toJsonString());


                        sendMessage(NETWORK_SERVER_SEND, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
                        prnSendData(" 카드승인취소",
                                ksnetresp.PrnTempleteData(MainActivity.this, 1,
                                        ksnetresp.getString("vat")));
                        //    _jsCallHandler.payResult(ksnetresp.toJsonString());  // 웹에 보냅니다
                            /*
                            try {
                            //    Log.d("tag",  JsonHelper.toJSON(ksnetresp.getMap()).toString() ) ;

                                //   _jsCallHandler.payResult((JsonHelper.toJSON(ksnetresp.getMap())).toString());  // 웹에 보냅니다
                            } catch ( Exception e) {
                                e.printStackTrace();
                            }
                            */

                    } else {    // 실패를 이야기 합니다
                        sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
//                        _jsCallHandler.payResult(rs.toJsonString());  // 웹에 보냅니다
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                /*이곳에서 서비로 데이터를 전송합니다*/
//# 전송 정보,type : '승인', number 카드번호, van ,vanId, amount ,vanTrxId 는 전표번호, authCd 는 승인번호, trackId 는 trx/rule 에서 회신 받은 trackId, regDate 는 전달 받은 시간 ,
//# 전송 정보,type : '승인실패', number 카드번호는 있을 경우 , van ,vanId, amount ,vanTrxId 는 전표번호, resultMsg는 사용자 취소 또는 KSNET 전달된 message1,message2함께, trackId 는 trx/rule 에서 회신 받은 trackId, regDate 는 전달 받은 시간 ,


            } else {
                rs.data.put("type", "승인실패");
                rs.data.put("resultMsg", "응답값 리턴 실패");
                rs.data.put("regDate", tools.getSysDateTIme());
//                _jsCallHandler.forceGoHome();
                Toasty.error(this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
                sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
                //     Toast.makeText(this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();

                /* 사용자 실퍠* */
//# 응답 시 200 인 경우 수신되는 van,vanId,trackId,secondKey,authCd,regDay 는 Application 으로 전달되어야 하며 secondKey 의 경우 KSNET에서 사용한다.
            }
            // 수행을 제대로 하지 못한 경우
            if (resultCode == RESULT_CANCELED) {
                rs.data.put("type", "승인실패");
                rs.data.put("resultMsg", "취소키가 눌러짐");
                rs.data.put("regDate", tools.getSysDateTIme());
                Toasty.error(this, "취소되었습니다.", Toast.LENGTH_LONG).show();
//                _jsCallHandler.forceGoHome();
                sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다

            }

        } else if (requestCode == REQUEST_CODE_CASH_PAYMENT) {
            try {
//                        PosEntry	S는 현금영수증카드시, key-in 거래시 K사용
//                        ReceiptNo	"현금영수증카드 거래시 """", key-in시 해당값    1. 주민등록번호 13자리 2. 휴대폰번호 3. 사업자등록번호"
//                        TelegramType	승인(0200) ,취소(0420)
//                        TotalAmount	총금액
//                        TaxAmount	부가세
//                        ServicAmount	봉사료
//                        FreeAmount	면세금액
//                        Amount	공급금액 = 총금액 - 부가세 - 봉사료
//                        Filler	여유필드
//                        DPTID	KSNET 가맹점 정보
//                        PayType	"""00"" 앞자리 취소사유(1.거래취소,2.오류발급취소,3.기타) 승인시 0 / 뒷자리 0 - 개인소득공제, 1-사업자지출증빙"
//                        AuthNum	승인번호 (취소시 사용)
//                        Authdate	승인일자 (취소시 사용)

                ksnetreq.clearHash();
                ksnetresp.clearHash();
                String jsonString = data.getStringExtra("data");
                Log.d("tag", "HANDLER_ CASHPAY START :" + jsonString);
                try {
                    JSONObject object = new JSONObject(jsonString);
                    Iterator keys = object.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        ksnetreq.put(key, ("" + object.get(key)));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //-------------------------------------------------------------------------
                String amount = "0";
                String installment = "00";
                boolean isCancel = false;
                if (Constants.IS_TEST)
                    Log.d("tag", "========REQUEST_APPROVE_KSNET=========" + data + "========================");
                try {
                    JSONObject jo = new JSONObject(jsonString);
                    amount = jo.getString("amount");
                    installment = jo.getString("cashType");
                    isCancel = jo.getString("isCancel").equals("true");

//                                ksnetreq.put("van", jo.getString("van"));
//                                ksnetreq.put("vanId", jo.getString("vanId"));
//                                ksnetreq.put("trackId", jo.getString("trackId"));
                    ksnetreq.put("secondKey", jo.getString("dptid"));
                    if (Constants.IS_TEST)
                        Log.d("tag", "========ksnetreq======" + ksnetreq.data.size() + "/" + ksnetreq.data.toString());


                    if (BuildConfig.IS_DEVEL) {
                        ksnetreq.put("DPTID", getString(R.string.ksnet_DPT));                                     // 단말기번호 , 테스트단말번호 DPT0TEST03 -> KSCIC앱에 기등록된 단말기번호일 경우에만 정상 승인
                    } else {
                        ksnetreq.put("DPTID", jo.getString("dptid"));             // 단말기번호 , 테스트단말번호 DPT0TEST03 -> KSCIC앱에 기등록된 단말기번호일 경우에만 정상 승인
                    }
                    //   ksnetreq.put("DPTID", "DPT0A08808".getBytes());                                     // 단말기번호 , 테스트단말번호 DPT0TEST03 -> KSCIC앱에 기등록된 단말기번호일 경우에만 정상 승인

                    if (jo.getString("isCard").equals("true")) {
                        ksnetreq.put("PosEntry", "S");
                        ksnetreq.put("ReceiptNo", "");  // 현금T영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
                    } else {
                        ksnetreq.put("PosEntry", "K");
                        ksnetreq.put("ReceiptNo", jo.getString("number"));  // 현금T영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
                    }
                    // Pos Entry Mode , 현금영수증 거래 시 키인거래에만 'K'사용
                    ksnetreq.put("PayType", jo.getString("cashType"));                                            // [신용]할부개월수(default '00') [현금]거래자구분
                    ksnetreq.put("installment", jo.getString("cashType"));                                            // [신용]할부개월수(default '00') [현금]거래자구분
                    ksnetreq.put("TotalAmount", new String(tools.getStrMoneytoTgAmount("" + amount))); // 총금액


                    if (isCancel) {
                        //취소
                        ksnetreq.put("Authdate", jo.getString("authdate") + "000000");//원거래 승인일자 , 취소시에만 사용
                        ksnetreq.put("AuthNum", (jo.getString("authnum") + tools.SPACE).substring(0, 12));
                        ksnetreq.put("TelegramType", "0420");                                    // 전문 구분 ,  승인(0200) 취소(0420)
                    } else {
                        //승인
                        ksnetreq.put("TelegramType", "0200");                                    // 전문 구분 ,  승인(0200) 취소(0420)
                    }

                    //기본값 사용함
                    int TotalAmount = Integer.parseInt(amount);  //총금액
                    int TaxAmount = (int) ((TotalAmount / 1.1) * 0.1);     // 부가세
                    int Amount = Math.round(TotalAmount - TaxAmount); // 공급가액

                    ksnetreq.put("amount", new String(tools.getStrMoneytoTgAmount("" + TotalAmount))); // 총금액
                    ksnetreq.put("TaxAmount", new String(tools.getStrMoneytoTgAmount("" + TaxAmount)));     // 부가세
                    ksnetreq.put("Amount", new String(tools.getStrMoneytoTgAmount("" + Amount)));     // 공급금액 = 총금액 - 부가세 - 봉사료
//                                }


//                                if (serviceflag != null && serviceflag.equals("0")) {
//                                    //사용안함
//                                    ksnetreq.put("ServicAmount", new String(tools.getStrMoneytoTgAmount("0")));        // 봉사료
//                                } else if (serviceflag != null && serviceflag.equals("1")) {
//                                    //포함
//                                    ksnetreq.put("ServicAmount", new String(tools.getStrMoneytoTgAmount("0")));        // 봉사료
//                                } else if (serviceflag != null && serviceflag.equals("2")) {
                    //미포함
                    ksnetreq.put("ServicAmount", new String(tools.getStrMoneytoTgAmount("0")));        // 봉사료
//                                }

                    ksnetreq.put("FreeAmount", new String(tools.getStrMoneytoTgAmount("0")));                             // 면세 0처리


                    ksnetreq.put("Filler", "");                                             // 여유필드 - 판매차 처리
                    ksnetreq.put("SignTrans", "N");                                        // 서명거래 필드, 무서명(N) 50000원 이상 서명(S)
                /*전문항목에는 포함되어 있지 않으나
                케이에스체크IC와 연동을위해 필요한 항목*/
                    ksnetreq.put("PlayType", "D");                                         // 실행구분,  데몬사용시 고정값(D)
                    ksnetreq.put("CardType", "");                                          // 은련선택 여부필드 (현재 사용안함)
                    ksnetreq.put("BranchNM", ""); // 가매점명
                    ksnetreq.put("BIZNO", ""); // 사업자번호

                    ksnetreq.put("ksnet_server_ip", getString(R.string.ksnet_server_ip)); // 접속 서버 아이피
                    ksnetreq.put("ksnet_server_port", getString(R.string.ksnet_server_port)); // 접속 서버 포트
                    ksnetreq.put("ksnet_telegrametype", getString(R.string.ksnet_telegrametype)); // Telegrame Type
                    ksnetreq.put("ksnet_timeout", getString(R.string.ksnet_timeout)); // 타임아웃

                    ksnetresp.setMap(ksnetreq.getMap());  // request랑 response랑 데이타를 같이 만들었습니다

                    Iterator<String> keys = ksnetreq.data.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (Constants.IS_TEST)
                            Log.d("tag", "==============" + key + "/" + new String(ksnetreq.data.get(key)) + "/" + ksnetreq.data.get(key).length());
                    }

//
                    if (isCancel) {
//                                    SweetDialogStatus("현금영수증 취소중");
                    } else {
//                                    SweetDialogStatus("현금영수증 발급중");
                    }


//                                if(jo.getString("isCard").equals("true")){
                    KSNET_APP_CALL(ksnetreq.getByteMap(), REQUEST_APPROVE_KSNET);
//                                }else {
//                                    _ksnetbt_ksr04.adminInfo = (new AdminInfo(ksnetreq.getByteMap()));
//                                    _ksnetbt_ksr04.cashThreadAdmission();
//                                }
                } catch (Exception e) {
                    e.printStackTrace();
                }


//                            new SweetAlertDialog(MtouchPayKsnet.this, SweetAlertDialog.ERROR_TYPE)
//                                    .setTitleText(getString(R.string.terminal))
//                                    .setContentText(getString(R.string.not_connect))
//                                    .setConfirmText(getString(R.string.pleat_power_on))
//                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sDialog) {
//                                            sDialog.dismissWithAnimation();
//                                        }
//                                    })
//                                    .show();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }


    @Override
    public void onTaskComplete(String result) {
        super.onTaskComplete(result);
        LOG.w("result: " + result);

        if (menu_status == NETWORK_SERVER_SEND) {
            APPROVE_RESULT_INFO(ksnetresp.toJsonString());
        }
    }

    private void APPROVE_RESULT_INFO(String result) {
        /**
         * {
         *       "data": {
         *         "CardNo": "940915******2779",
         *         "IssueCode": "09",
         *         "ksnet_timeout": "16000",
         *         "Message2": "OK: 94268186    ",
         *         "ksnet_request_return_code_msg": "465",
         *         "PlayType": "D",
         *         "Dpt_Id": "DPT0TEST03",
         *         "Remain": "000000000",
         *         "STX": "\u0002",
         *         "Status": "O",
         *         "regDate": "200424163728",
         *         "ksnet_server_port": "9562",
         *         "Message1": "롯데카드        ",
         *         "type": "승인",
         *         "ReceiptNo": "X",
         *         "number": "940915******2779",
         *         "TaxAmount": "000000000091",
         *         "point3": "000000000",
         *         "prevAuthNum": "94268186    ",
         *         "PayType": "00",
         *         "SignTrans": "N",
         *         "Authdate": "200424163728",
         *         "ServicAmount": "000000000000",
         *         "FranchiseID": "1234           ",
         *         "TotalAmount": "1004",
         *         "Working": "43",
         *         "notice1": "TEL)1544-4700       ",
         *         "Approve": "01",
         *         "resultMsg": "롯데카드:OK: 94268186",
         *         "vanTrxId": "TX200424001321",
         *         "Length": "0461",
         *         "CardType": "N",
         *         "Amount": "000000000913",
         *         "point2": "000000000",
         *         "AuthNum": "94268186    ",
         *         "trackId": "TX200424001321",
         *         "Working Key": "97058233398E8B18",
         *         "ksnet_telegrametype": "5",
         *         "PosEntry": "S",
         *         "sendcount": "0",
         *         "PurchaseName": "롯데카드사      ",
         *         "BIZNO": "",
         *         "fillter": "                              ",
         *         "Classification": "IC",
         *         "van": "KSPAY1",
         *         "ksnet_server_ip": "210.181.28.116",
         *         "amount": "000000001004",
         *         "ksnet_request_return_code": "465",
         *         "Filler": "",
         *         "notice2": "KSNET제출                               ",
         *         "secondKey": "DPT0A24555",
         *         "TelegramType": "0210",
         *         "installment": "00",
         *         "prevAuthDate": "200424163728",
         *         "PurchaseCode": "09",
         *         "Full_Text_Num": "wr07m8j1d0tf",
         *         "point1": "000000000",
         *         "prevClassfication": "IC",
         *         "N": "N",
         *         "CardName": "롯데카드        ",
         *         "BranchNM": "",
         *         "authCd": "94268186",
         *         "KsnetCode": "0000",
         *         "FreeAmount": "000000000000",
         *         "CardCord": "0000",
         *         "vanId": "2006400005",
         *         "Enterprise_Info": "    ",
         *         "OriNumber": "977422788005        ",
         *         "processingCd": "1004",
         *         "DPTID": "DPT0TEST03",
         *         "null": "     "
         *       }
         *     }
         */


//        var requestObj = {
//                "number": Storage.payResult.CardNo,
//                "van": Storage.payResult.van,
//                "vanId": Storage.payResult.vanId,
//                "amount": Number(Storage.payResult.amount),
//                "vanTrxId": Storage.payResult.vanTrxId,
//                "authCd": Storage.payResult.AuthNum ? Storage.payResult.AuthNum.replace(/ /g, '') : '',
//                "trackId": Storage.payResult.trackId,
//                "installment": Storage.payResult.PayType,
//                "regDate": '20' + Storage.payResult.Authdate,
//                "issuer": Storage.payResult.PurchaseName ? Storage.payResult.PurchaseName.replace(/ /g, '') : '',
//                "brand": Storage.payResult.CardName ? Storage.payResult.CardName.replace(/ /g, '') : '',
//                "trxId": Storage.payResult.trxId
//  };
//        if(Storage.payResult.result == '취소승인') {
//            requestObj.trxResult = '취소';
//        } else {
//            requestObj.trxResult = '승인';
//        }


        try {
            JSONObject jsonData = new JSONObject(result).getJSONObject("data");
            LOG.w("result: " + jsonData.toString(4));

            Intent intent = new Intent(this, PaymentCompleteActivity.class);
            if (jsonData.has("trackId")) intent.putExtra("trackId", jsonData.getString("trackId"));
            intent.putExtra("cardNumber", jsonData.getString("number"));
            intent.putExtra("amount", jsonData.getString("amount"));
            intent.putExtra("approvalDay", jsonData.getString("Authdate"));
            intent.putExtra("approvalNumber", jsonData.getString("AuthNum"));
            if (jsonData.has("trxId")) intent.putExtra("trxId", jsonData.getString("trxId"));
            intent.putExtra("delngSe", jsonData.getString("type"));
            intent.putExtra("authDate", jsonData.getString("Authdate"));
            intent.putExtra("instlmtMonth", jsonData.getString("installment"));
            intent.putExtra("setleMssage", jsonData.getString("notice1"));
            intent.putExtra("brand", jsonData.getString("CardName"));
            intent.putExtra("issuCmpnyNm", jsonData.getString("Message1"));
            intent.putExtra("puchasCmpnyNm", jsonData.getString("PurchaseName"));

            startActivityForResult(intent, REQUEST_CODE_COMPLETE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mService != null)
//            mService.stop();
//        try {
//            unbindService(mServiceConnection);
//        } catch (Exception e) {
//        }
//        try {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(BlueToothStatusChangeReceiver);
//        } catch (Exception unused) {
//        }
    }
}
