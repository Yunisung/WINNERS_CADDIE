package com.bkwinners.ksnet.dpt.ks03.pay.ksr03;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bkwinners.ksnet.dpt.Toasty;
import com.bkwinners.ksnet.dpt.action.PayResultActivity;
import com.bkwinners.ksnet.dpt.action.obj.responseObj;
import com.bkwinners.ksnet.dpt.action.process.util.LOG;
import com.bkwinners.ksnet.dpt.ks03.PermissionActivity;
import com.bkwinners.ksnet.dpt.ks03.bluetooth.DeviceRegistActivity;
import com.bkwinners.ksnet.dpt.ks03.obj.KSnetRequestObj;
import com.bkwinners.ksnet.dpt.ks03.obj.KsnetPrnObj;
import com.bkwinners.ksnet.dpt.ks03.obj.KsnetResponseObj;
import com.bkwinners.ksnet.dpt.ks03.obj.Request;
import com.bkwinners.ksnet.dpt.ks03.obj.Response;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.bkwinners.ksnet.dpt.ks03.pay.inter.JsCallHandler;
import com.bkwinners.ksnet.dpt.ks03.pay.inter.JsReceiveHandler;
import com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus;
import com.bkwinners.ksnet.dpt.ks03.pay.ksnet.KSNETStatus;
import com.bkwinners.ksnet.dpt.ks03.pay.ksnet.imp.AynilPayKsnetImp;
import com.nordicsemi.nrfUARTv2.UartService;
import com.pswseoul.comunity.http.HttpGetTask;
import com.pswseoul.comunity.http.HttpPostTask;
import com.pswseoul.comunity.http.HttpPostUrlTask;
import com.pswseoul.comunity.imp.AsyncTaskCompleteListener;
import com.pswseoul.util.AndroidUtils;
import com.pswseoul.util.GsonUtil;
import com.pswseoul.util.NetworkUtil;
import com.pswseoul.util.SunUtil;
import com.pswseoul.util.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import zj.com.cn.bluetooth.sdk.BluetoothService;
import zj.com.cn.bluetooth.sdk.DeviceListActivity;
import zj.com.cn.bluetooth.sdk.DeviceListDialogtActivity;
import zj.com.command.sdk.Command;
import zj.com.command.sdk.PrinterCommand;

import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_APPROVE_CEHCK;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_APP_VERSION;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_AVALABLE_KEY;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_BASIC_VALUE;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_FAIL_CEHCK;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_REGISTRY_APP;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_REGISTRY_STORE;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.NETWORK_SERVER_SEND;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.PHONE;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.TEST;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.menu_status;

public class MtouchPayKsnet extends Activity implements AsyncTaskCompleteListener, AynilPayKsnetImp {

    private static final boolean DEBUG = true;

    private static String TAG = "tagtag";

    private long backKeyPressedTime = 0;

    private long ksnetsendTime = 0;

    /******************************************************************************************************/
    // Name of the connected device
    String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;

    /******************************************************************************************************/
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;

    public static final int MESSAGE_RECONNECT_CONNECT = 8;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1000;
    private static final int REQUEST_ENABLE_BT = 1200;

    // Ksnet Connect Test
    private static final int REQUEST_APPROVE_KSNET = 10000;
    private static final int REQUEST_APPROVE_CANCEL_KSNET = 11000;


    /*******************************************************************************************************/
    private static final String CHINESE = "GBK";
    private static final String THAI = "CP874";
    private static final String KOREAN = "EUC-KR";
    private static final String BIG5 = "BIG5";

    private KSnetRequestObj ksnetreq = new KSnetRequestObj();
    private KsnetResponseObj ksnetresp = new KsnetResponseObj();

    private WebView webView;
    public JsCallHandler _jsCallHandler;

    private String trx_id = "";
    int latestVersion = 0;


    private final int PERMISSION_READ_STATE = 1004;
    private final int PERMISSION_SEND_SMS = 1005;

    private int KSNETSATTUS = 0;
    private LocationManager locationManager;


    private SharedPreferences pref;
    private SharedPreferences.Editor prvEditor;


    public void initialize() {
        this.pref = getSharedPreferences("Variable", 0);
        this.prvEditor = this.pref.edit();


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            finish();
            return;
        }
        service_init();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 2);
//        } else if (this.locationManager.isProviderEnabled("gps") && pref.getString("MAC_Adress", "NONE").equals("NONE")) {
//            startActivityForResult(new Intent(this, com.bkwinners.ksnet.dpt.ks03.bluetooth.DeviceListActivity.class), 1);
//        } else if (Build.VERSION.SDK_INT >= 28) {
//           new AlertDialog.Builder(this).setTitle("위치기능 활성화").setCancelable(false).setIcon(R.drawable.warning).setMessage("안드로이드 9.0이상 기기에서는 [위치]기능을 사용해야 정상적인 리더기 사용이 가능합니다.\n\n[확인]버튼을 눌러 위치기능 설정화면에서 '사용' 설정을 해주시기 바랍니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
//                    intent.addCategory("android.intent.category.DEFAULT");
//                    startActivityForResult(intent, 8);
//                }
//            }).show();
        } else {
            if (pref.getString(Constants.KEY_MAC_ADDRESS, "NONE").equals("NONE")) {
//                startActivityForResult(new Intent(this, com.bkwinners.ksnet.dpt.ks03.bluetooth.DeviceListActivity.class), 1);
                startActivityForResult(new Intent(this, DeviceRegistActivity.class), 1);
            }
        }
    }

    private void service_init() {
        bindService(new Intent(this, UartService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.BlueToothStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private UartService mmService;
    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            LOG.w("BTJYP", "ServiceConnection: Connected ");
            mmService = ((UartService.LocalBinder) rawBinder).getService();
            if (!mmService.initialize()) {
                LOG.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            LOG.w("BTJYP", "ServiceConnection: Disconnected ");
            if (mmService != null) {
//                mmService.disconnect();
                mmService = null;
            }
        }
    };

    private boolean isBatChk = false;
    private final BroadcastReceiver BlueToothStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LOG.w("BTJYP", "status change receiver : action= " + action);

            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {

            } else if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {

            } else if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {  // 이곳서는 밧데리 체크등을 보냅니다

            } else if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {   // 이곳에서 데이타를 다 모아서 처리할 수 있게끔 해줍니다

            } else if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {

            }
        }

    }; // BroadcastReceiver

    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.IS_DEVEL) {
            Constants.IS_TEST = true;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.webview_main_activity);

        //체크ic 설치여부확인
        String appPackageName = "ksnet.kscic";
        if (!appInstalledOrNot(appPackageName) && !BuildConfig.IS_DEVEL) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MtouchPayKsnet.this, R.style.DialogTheme);
            LayoutInflater inflater = MtouchPayKsnet.this.getLayoutInflater();
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
/*
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    GotoGooglePlayer("ksnet.kscic");
                } else  {
                    GotoGooglePlayer("ksnet.kscic");
                }
*/
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
        if (AndroidUtils.rootCheck()) {
            final SweetAlertDialog dialog = new SweetAlertDialog(MtouchPayKsnet.this, SweetAlertDialog.ERROR_TYPE);
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
        } else if (!AndroidUtils.isOnline()) {
            final SweetAlertDialog dialog = new SweetAlertDialog(MtouchPayKsnet.this, SweetAlertDialog.ERROR_TYPE);
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
        } else {

            webView = (WebView) findViewById(R.id.webviewId);

            KSNETStatus.getInstance(this);  // 저장된 값을 세팅 하는 곳

            if (PHONE) {
                // Get local Bluetooth adapter
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                // If the adapter is null, then Bluetooth is not supported
                if (mBluetoothAdapter == null) {
                    Toasty.error(this, "블루투스가 활성화되어 있지 않습니다", Toast.LENGTH_LONG).show();
                    finish();
                }
                //       KSNETStatus.BT_PRN_ADDRESS = SunUtil.getAppPreferences(getApplicationContext(), "bt_address");
            }

            if (PHONE) {
                //      checkPhonePermission();
            }

//            FirebaseApp.initializeApp(this);

//// FCCM getToken
            if (PHONE) {
                KSNETStatus.appId = getPackageName(); //appId;
            }

            if (Constants.IS_TEST)
                Log.d("tag", "isNetworkAvailable : " + isNetworkAvailable());

            if (!isNetworkAvailable()) {
                timerShowDialog("네트워크상태", "네트워크가 연결되지 않았습니다. \n네트워크 확인 후 재 시작 바랍니다 ", 6000);
            } else {
                if (PHONE) {
                    getPhoneNum();
                } else {
                    KSNETStatus.telNo = "";
                }

//            FirstIntialize();  // 버젼을 채크한 곳입니다
                BTPrinter();
                initWebView();

                KSNETStatus.BT_PRN_ADDRESS = "";

                if (TEST) {
//                timerShowDialog("title", " message ", 5000);

                }


//                if (getIntent() != null) {
//                    String data = getIntent().getStringExtra("payData");
//                    try {
//                        Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_PAYSTART);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("data", data);
//                        msg.setData(bundle);
//                        mHandler.sendMessage(msg);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }


                //      getCheckApplication("ks.ksciclv");
            }
            //   sendMessage(NETWORK_SERVER_SEND, approve_data );  // 이곳에서 네트워크를 전송합니다

            /* WEBVIEW를 하기 위한 세팅 작업을 한 곳입니다 */
//       initWebView();

            //     menu_status = NETWORK_APP_VERSION;
            //     new HttpPostUrlTask(MtouchPayKsnet.this).execute(new String[] { "https://c3tms.cyrexpay.com/version" } );  //https://c3tms.cyrexpay.com/v0/version  http://1.234.27.138/version.html


            if (getSharedPreferences("USERINFO", MODE_PRIVATE).getInt("FIRST_VIEW", 0) == 0) {
                Intent intent = new Intent(MtouchPayKsnet.this, PermissionActivity.class);
                intent.putExtra("permission", new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE", "android.permission.GET_ACCOUNTS"});
                intent.putExtra("permission1", "- 위치 : BLE사용\n- 저장공간 : 앱설정 저장\n- 전화 : 휴대폰번호 전송\n");
                intent.putExtra("permission2", "- SMS : 문자메시지 발송\n- 주소록 :이메일 발송\n");
                intent.putExtra("require", new String[]{"ACCESS_COARSE_LOCATION", "WRITE_EXTERNAL_STORAGE", "READ_PHONE_STATE"});
                startActivityForResult(intent, 0);
                return;
            }


            initialize();
        }//인터넷, 루팅여부확인
    }

    // Custom Dialog
    private void ShowKSNETDialog(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MtouchPayKsnet.this, R.style.DialogTheme);
        LayoutInflater inflater = MtouchPayKsnet.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.customdialog_layout, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtView = (TextView) dialogView.findViewById(R.id.description);
        txtView.setText(msg);
        Button ok_btn = (Button) dialogView.findViewById(R.id.button);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    GotoGooglePlayer("ksnet.kscic");
                } else  {
                    GotoGooglePlayer("ks.ksciclv");
                }
*/
                GotoGooglePlayer("ks.ksciclv");
                alertDialog.hide();
                finish();
            }
        });

        alertDialog.show();
    }

    private void ShowCyrexOayDialog(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MtouchPayKsnet.this, R.style.DialogTheme);
        LayoutInflater inflater = MtouchPayKsnet.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.customdialog_layout, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtView = (TextView) dialogView.findViewById(R.id.description);
        txtView.setText(msg);
        Button ok_btn = (Button) dialogView.findViewById(R.id.button);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoGooglePlayer("com.aynil.parksuwon.myapplication.pay");
                alertDialog.hide();
                finish();
            }
        });

        alertDialog.show();
    }

    public void FirstIntialize() {
        //KSNETStatus.getInstance(this);
        //버젼을 체크 합니다
        menu_status = NETWORK_APP_VERSION;
        new HttpPostUrlTask(MtouchPayKsnet.this).execute(new String[]{"https://c3tms.cyrexpay.com/version"});  //https://c3tms.cyrexpay.com/v0/version  http://1.234.27.138/version.html

    }

    public void StoreInfoHttp() {
        //KSNETStatus.getInstance(this);
        //버젼을 체크 합니다
        sendMessage(NETWORK_AVALABLE_KEY, KSNETStatus.token);

    }


    public void initWebSetting() {
        WebSettings settings = webView.getSettings();        // Javascript 사용하기
        settings.setJavaScriptEnabled(true);        // WebView 내장 줌 사용여부
        settings.setBuiltInZoomControls(true);        // 화면에 맞게 WebView 사이즈를 정의
        settings.setLoadWithOverviewMode(true);        // ViewPort meta tag를 활성화 여부
        settings.setUseWideViewPort(true);        // 줌 컨트롤 사용 여부
        settings.setDisplayZoomControls(false);        // 사용자 제스처를 통한 줌 기능 활성화 여부
        settings.setSupportZoom(false);        // TextEncoding 이름 정의
        settings.setDefaultTextEncodingName("UTF-8");        // Setting Local Storage
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 캐쉬 사용 방법을 정의
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);


// 2017-12-20
        /*
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }
        */

        // Enable remote debugging via chrome://inspect
        if (Constants.IS_TEST && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

    }

    public void initWebView() {
        final JsReceiveHandler _jsReceiveHandler;

        initWebSetting();
        _jsCallHandler = new JsCallHandler(this, webView);

        _jsReceiveHandler = new JsReceiveHandler(this, webView, mHandler);
        webView.addJavascriptInterface(_jsReceiveHandler, "Web");

        webView.getSettings().setUseWideViewPort(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                //Toast.makeText(TableContentsWithDisplay.this, "url "+url, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Toast.makeText(TableContentsWithDisplay.this, "Width " + view.getWidth() +" *** " + "Height " + view.getHeight(), Toast.LENGTH_SHORT).show();
            }

        });
//       webView.loadUrl("file:///android_asset/www/test.html");
        // 2017-12-20

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.loadUrl("file:///android_asset/www/index.html");
//        webView.loadUrl("https://c4");
    }

    public void BTPrinter() {
//        if(KSNETStatus.BT_PRN_FLAG) return;
        if ((KSNETStatus.BT_PRN_ADDRESS != null) && (KSNETStatus.BT_PRN_ADDRESS.length() > 0)) {  // 바로 접솝합니다
            if (BluetoothAdapter.checkBluetoothAddress(KSNETStatus.BT_PRN_ADDRESS)) {
                BluetoothDevice device = mBluetoothAdapter
                        .getRemoteDevice(KSNETStatus.BT_PRN_ADDRESS);
                // Attempt to connect to the device
                try {
                    mService.connect(device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toasty.info(MtouchPayKsnet.this, "프린터를 연결하고 있습니다", Toast.LENGTH_LONG);
                // custom_toast("프린터 연결중입니다......");
            }
        } else {
            Intent serverIntent = new Intent(MtouchPayKsnet.this, DeviceListDialogtActivity.class);
            //Intent serverIntent = new Intent(MtouchPayKsnet.this, DeviceListActivity.class);
            serverIntent.putExtra("name", "Printer");
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }

        /*
        if((KSNETStatus.BT_PRN_ADDRESS != null) && (KSNETStatus.BT_PRN_ADDRESS.length() > 0)) {  // 바로 접솝합니다
            if (BluetoothAdapter.checkBluetoothAddress(KSNETStatus.BT_PRN_ADDRESS ) ) {
                BluetoothDevice device = mBluetoothAdapter
                        .getRemoteDevice(KSNETStatus.BT_PRN_ADDRESS );
                // Attempt to connect to the device
                mService.connect(device);
            }
        } else {
            Intent serverIntent = new Intent(MtouchPayKsnet.this, DeviceListDialogtActivity.class);
            serverIntent.putExtra("name", "Printer");
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
        */
    }
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(this,"시스템 BACK 버튼 눌림", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;

    }
*/

       public void ksNetMessage(int menustatus, String data) {
        if (NetworkUtil.checkStatus(this) == NetworkUtil.NETWORK_NONE) {
            Toasty.error(getApplicationContext(), "NETWORK_NONE", Toast.LENGTH_SHORT);
            return;
        }
        KSNETSATTUS = menustatus;
        if (menustatus == REQUEST_APPROVE_KSNET) {
            String amount = "0";
            String installment = "00";
            if (Constants.IS_TEST)
                Log.d("tag", "========REQUEST_APPROVE_KSNET=========" + data + "========================");
            try {
                JSONObject jo = new JSONObject(data);
                amount = jo.getString("amount");
                installment = jo.getString("installment");

                ksnetreq.put("van", jo.getString("van"));
                ksnetreq.put("vanId", jo.getString("vanId"));
                ksnetreq.put("trackId", jo.getString("trackId"));
                ksnetreq.put("secondKey", jo.getString("" + "secondKey"));
                if (Constants.IS_TEST)
                    Log.d("tag", "========ksnetreq======" + ksnetreq.data.size() + "/" + ksnetreq.data.toString());
                ksnetreq.put("TelegramType", "0200");                                    // 전문 구분 ,  승인(0200) 취소(0420)   승인번호없는망취소 (0440) / (0450)
                ksnetreq.put("DPTID", jo.getString("secondKey"));             // 단말기번호 , 테스트단말번호 DPT0TEST03 -> KSCIC앱에 기등록된 단말기번호일 경우에만 정상 승인
                ksnetreq.put("PosEntry", "S");                                            // Pos Entry Mode , 현금영수증 거래 시 키인거래에만 'K'사용
                ksnetreq.put("PayType", installment);                                            // [신용]할부개월수(default '00') [현금]거래자구분
                ksnetreq.put("installment", installment);                                            // [신용]할부개월수(default '00') [현금]거래자구분
                ksnetreq.put("TotalAmount", new String(tools.getStrMoneytoTgAmount("" + amount))); // 총금액

                if (BuildConfig.IS_DEVEL) {
                    ksnetreq.put("DPTID", "DPT0TEST03");
//                    ksnetreq.put("secondKey", jo.getString("" + "secondKey"));
                }

//2019-04-04


                int TotalAmount = Integer.parseInt(amount);  //총금액
                int TaxAmount = (int) ((TotalAmount / 1.1) * 0.1);     // 부가세
                int Amount = Math.round(TotalAmount - TaxAmount); // 공급가액

                ksnetreq.put("amount", new String(tools.getStrMoneytoTgAmount("" + TotalAmount))); // 총금액
                ksnetreq.put("TaxAmount", new String(tools.getStrMoneytoTgAmount("" + TaxAmount)));     // 부가세
                ksnetreq.put("ServicAmount", new String(tools.getStrMoneytoTgAmount("0")));        // 봉사료

//              ksnetreq.put("amount", tools.getStrMoneytoTgAmount(""+amount)); // 총금액
//              ksnetreq.put("ServicAmount", tools.getStrMoneytoTgAmount("0"));                           // 봉사료
//              ksnetreq.put("TaxAmount", tools.getStrMoneytoTgAmount("0"));                              // 부가세

                ksnetreq.put("Amount", new String(tools.getStrMoneytoTgAmount("" + Amount)));     // 공급금액 = 총금액 - 부가세 - 봉사료
                ksnetreq.put("FreeAmount", new String(tools.getStrMoneytoTgAmount("0")));                             // 면세 0처리
                ksnetreq.put("AuthNum", "");                                            //원거래 승인번호 , 취소시에만 사용
                ksnetreq.put("Authdate", "");                                           //원거래 승인일자 , 취소시에만 사용
                ksnetreq.put("Filler", "");                                             // 여유필드 - 판매차 처리
                ksnetreq.put("SignTrans", "N");                                        // 서명거래 필드, 무서명(N) 50000원 이상 서명(S)
                /*전문항목에는 포함되어 있지 않으나
                케이에스체크IC와 연동을위해 필요한 항목*/
                ksnetreq.put("PlayType", "D");                                         // 실행구분,  데몬사용시 고정값(D)
                ksnetreq.put("CardType", "");                                          // 은련선택 여부필드 (현재 사용안함)
                ksnetreq.put("BranchNM", ""); // 가매점명
                ksnetreq.put("BIZNO", ""); // 사업자번호
                ksnetreq.put("ReceiptNo", "X");  // 현금T영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K

                ksnetreq.put("ksnet_server_ip", getString(R.string.ksnet_server_ip)); // 접속 서버 아이피
                ksnetreq.put("ksnet_server_port", getString(R.string.ksnet_server_port)); // 접속 서버 포트
                ksnetreq.put("ksnet_telegrametype", getString(R.string.ksnet_telegrametype)); // Telegrame Type
                ksnetreq.put("ksnet_timeout", getString(R.string.ksnet_timeout)); // 타임아웃

                ksnetresp.setMap(ksnetreq.getMap());   // 요청의 데이터를 응답으로 넣습니다
                if (Constants.IS_TEST)
                    Log.d("tag", "==============" + ksnetreq.data.size() + "/" + ksnetreq.data.toString());

                Iterator<String> keys = ksnetreq.getMap().keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (Constants.IS_TEST)
                        Log.d("tag", "==============" + key + "/" + new String(ksnetreq.data.get(key)) + "/" + ksnetreq.data.get(key).length());
                }

                KSNET_APP_CALL(ksnetreq.getByteMap(), REQUEST_APPROVE_KSNET);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (menustatus == REQUEST_APPROVE_CANCEL_KSNET) {
            try {
//HashMap<String, String> m_hash = (HashMap<String, String>) data.getSerializableExtra("result");
//data={"trxId":"T171217000022","result":"취소승인","amount":1004,"van":"KSPAY1","vanId":"2823200001","authCd":"44657041","trackId":"TX171217000157","regDay":"20171217","secondKey":"DPT0A08808"}
                JSONObject jo = new JSONObject(data);
                //   ksnetresp.clearHash();;

                Iterator keys = jo.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    ksnetresp.put(key, "" + jo.get(key));
                }

                ksnetresp.put("TelegramType", "0420"); // 전문 구분 ,  승인(0200) 취소(0420)
                ksnetresp.put("ReceiptNo", "X");  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
                ksnetresp.put("AuthNum", (jo.getString("authCd") + tools.SPACE).substring(0, 12));  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
                ksnetresp.put("Authdate", jo.getString("regDay"));  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;

                ksnetresp.put("trxId", jo.getString("trxId"));
                ksnetresp.put("TotalAmount", jo.getString("amount"));

                HashMap<String, byte[]> m_hash = new HashMap<String, byte[]>();

                m_hash.put("TelegramType", "0420".getBytes()); // 전문 구분 ,  승인(0200) 취소(0420)
                m_hash.put("ReceiptNo", "X".getBytes());  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
                m_hash.put("AuthNum", (jo.getString("authCd") + tools.SPACE).substring(0, 12).getBytes());  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
                m_hash.put("Authdate", jo.getString("regDay").substring(2).getBytes());  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
                m_hash.put("DPTID", jo.getString("secondKey").getBytes());
//                m_hash.put("DPTID","DPT0TEST03" .getBytes());

                if (BuildConfig.IS_DEVEL) {
                    m_hash.put("DPTID", "DPT0TEST03".getBytes());
//                    ksnetreq.put("secondKey", jo.getString("" + "secondKey"));
                }

                m_hash.put("PosEntry", "S".getBytes());                                            // Pos Entry Mode , 현금영수증 거래 시 키인거래에만 'K'사용
                m_hash.put("PayType", jo.getString("installment").getBytes());                                            // [신용]할부개월수(default '00') [현금]거래자구분
                m_hash.put("TotalAmount", tools.getStrMoneytoTgAmount(jo.getString("amount"))); // 총금액
                m_hash.put("ServicAmount", tools.getStrMoneytoTgAmount("0"));                           // 봉사료
                m_hash.put("TaxAmount", tools.getStrMoneytoTgAmount("0"));                              // 부가세
                m_hash.put("FreeAmount", tools.getStrMoneytoTgAmount("0"));                             // 면세 0처리
                m_hash.put("Amount", tools.getStrMoneytoTgAmount("0")); // 총금액
                m_hash.put("Filler", "".getBytes());                                             // 여유필드 - 판매차 처리
                m_hash.put("SignTrans", "N".getBytes());                                        // 서명거래 필드, 무서명(N) 50000원 이상 서명(S)

                m_hash.put("PlayType", "D".getBytes());                                         // 실행구분,  데몬사용시 고정값(D)
                m_hash.put("CardType", "".getBytes());                                          // 은련선택 여부필드 (현재 사용안함)
                m_hash.put("BranchNM", "".getBytes()); // 가매점명
                m_hash.put("BIZNO", "".getBytes()); // 사업자번호


                m_hash.put("ksnet_server_ip", getString(R.string.ksnet_server_ip).getBytes()); // 접속 서버 아이피
                m_hash.put("ksnet_server_port", getString(R.string.ksnet_server_port).getBytes()); // 접속 서버 포트
                m_hash.put("ksnet_telegrametype", getString(R.string.ksnet_telegrametype).getBytes()); // Telegrame Type
                m_hash.put("ksnet_timeout", getString(R.string.ksnet_timeout).getBytes()); // 타임아웃


                KSNET_APP_CALL(m_hash, REQUEST_APPROVE_CANCEL_KSNET);  //

                Iterator<String> iterator = m_hash.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if (Constants.IS_TEST) {
                        Log.d("debug", "==========================================" + "key=" + key);
                        Log.d("debug", " value=" + new String(m_hash.get(key)));
                    }
                }

                /*
                ComponentName compName = new ComponentName("ksnet.kscic", "ksnet.kscic.PaymentDlg");

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(compName);
           //    intent.putExtra("AdminInfo_Hash",ksnetresp.getByteMap());
                intent.putExtra("AdminInfo_Hash",m_hash);
                intent.setAction("test");
                startActivityForResult(intent, REQUEST_APPROVE_CANCEL_KSNET);
                */
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void sendMessage(int menustatus, String str) {
        if (NetworkUtil.checkStatus(this) == NetworkUtil.NETWORK_NONE) {
            Toasty.error(getApplicationContext(), "NETWORK_NONE", Toast.LENGTH_SHORT);
            return;
        }
        menu_status = menustatus;
        if (menustatus == NETWORK_REGISTRY_STORE) {
            new HttpPostTask(this).execute(new String[]{REGISTRY_STORE, str});
        } else if (menustatus == NETWORK_REGISTRY_APP) {
            new HttpPostTask(this).execute(new String[]{REGISTRY_APP, str});
        } else if (menustatus == NETWORK_AVALABLE_KEY) {
            new HttpGetTask(this).execute(new String[]{"AVALABLE_KEY", ""});
        } else if (menustatus == NETWORK_BASIC_VALUE) {
            new HttpPostTask(this).execute(new String[]{BASIC_VALUE, str});
        } else if (menustatus == NETWORK_REGISTRY_APP) {
            new HttpPostTask(this).execute(new String[]{REGISTRY_STORE, str});
        } else if (menustatus == NETWORK_SERVER_SEND) {
            new HttpPostTask(this).execute(new String[]{"SERVER_SEND", str});
        } else if (menustatus == NETWORK_FAIL_CEHCK) {
            new HttpPostTask(this).execute(new String[]{"SERVER_SEND", str});
        }
    }

    /* 서버와의 통신이 이루워 지는 곳입니다  */
    public void onTaskComplete(String result) {
        if (Constants.IS_TEST)
            Log.d("RESPONSE", result);

        switch (menu_status) {
            case NETWORK_APP_VERSION: {
                if (Constants.IS_TEST)
                    Log.d("tag", ">>>>>>>>>>>>>>>>>>>>>>" + result.trim() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                try {
                    latestVersion = Integer.parseInt(result.trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case NETWORK_REGISTRY_STORE: {
                Response response = (Response) GsonUtil.fromJson(result, Response.class);
                if (Constants.IS_TEST)
                    Log.d("tag", response.data.toString());
            }
            break;

            case NETWORK_REGISTRY_APP: {
                Response response = (Response) GsonUtil.fromJson(result, Response.class);
                if (Constants.IS_TEST)
                    Log.d("tag", response.data.toString());
            }
            break;
            case NETWORK_AVALABLE_KEY: {
            }
            break;
            case NETWORK_BASIC_VALUE: {
            }
            break;
            case NETWORK_APPROVE_CEHCK: {

            }
            break;
            case NETWORK_SERVER_SEND: {
                Response response = (Response) GsonUtil.fromJson(result, Response.class);
                if (response != null) {
                    if (Constants.IS_TEST)
                        Log.d("tag", "*************SUCCESS**************************" + response.data);
                    try {
                        String dresult = response.data.getString("result");
                        String trxId = response.data.getString("trxId");

                        if (trxId != null || trxId.length() > 0)  // OK
                        {
                            ksnetresp.put("trxId", trxId);
                            try {
                                if (TEST) {
                                    SunUtil.setAppPreferences(MtouchPayKsnet.this, "approve", "");
                                }
                                //Log.d("tag", "============" + (JsonHelper.toJSON(ksnetresp.getMap())).toString());
                                //Log.d("tag", "=====response.toJsonString()=======" + response.toJsonString());
//                               _jsCallHandler.payResult((JsonHelper.toJSON(ksnetresp.getMap())).toString());
                                if (Constants.IS_TEST) {
                                    Log.d("tag", "=====_jsCallHandler .toJsonString()=======" + response.toJsonString());
                                    Log.d("tag", "=====ksnetresp .toJsonString()=======" + ksnetresp.toJsonString());
                                }
                                ksnetresp.put("STX", "");
                                ksnetresp.put("amount", ksnetresp.getString("TotalAmount"));
                                _jsCallHandler.payResult(ksnetresp.toJsonString().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {    // 다시 보냅시다
                            ksnetresp.put("STX", "");
                            _jsCallHandler.payResult(ksnetresp.toJsonString());
                            /*
                            Request rs = (Request) GsonUtil.fromJson(SunUtil.getAppPreferences(MtouchPayKsnet.this,"approve" ), Request.class);
                            int sendcount = Integer.parseInt((String)rs.data.get("sendcount"));
                            if( sendcount < 2 ) {
                                rs.data.put("sendcount", ""+(++sendcount));
                                SunUtil.setAppPreferences(MtouchPayKsnet.this,"approve" , rs.toJsonString() );
                                sendMessage(NETWORK_SERVER_SEND, rs.toJsonString() ) ;  // 이곳에서 네트워크를 전송합니다
                            }
                            */
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            break;

            case NETWORK_FAIL_CEHCK: {
                Response response = (Response) GsonUtil.fromJson(result, Response.class);
                if (response != null) {
                    //if(data_trxId == null ) 다시 보냅시다
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CyrexNetworkStatus.KSNET_APP_CALL = false;
        Log.d("tag", "onActivityResult " + resultCode + " resultCode " + resultCode);
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.d(TAG, String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }
            }
        }

        if (resultCode == PermissionActivity.RESULT_PERMISSION_OK) {
            SharedPreferences.Editor edit = getSharedPreferences("USERINFO", MODE_PRIVATE).edit();
            edit.putInt("FIRST_VIEW", 1);
            edit.commit();
            startActivityForResult(new Intent(this, DeviceRegistActivity.class), 1);

        } else if (resultCode == PermissionActivity.RESULT_PERMISSION_CANCEL) {
            System.out.println("####RESULT_CANCELED");
            new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.app_name)).setMessage("필수권한 사용 거부로 인하여 앱이 종료됩니다.").setCancelable(false).setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        } else if (resultCode == -2) {
            startActivityForResult(new Intent(this, DeviceRegistActivity.class), 1);
        }

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                if (resultCode == RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                    Log.d("tag", "onActivityResult " + resultCode + " resultCode " + resultCode + " address >>>>>>>>>" + address + "<<<<<<<<<");

                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                        // Attempt to connect to the device
                        mService.connect(device);
                        KSNETStatus.BT_PRN_ADDRESS = address;
                    }
                }
            }
            break;
            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    KeyListenerInit();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toasty.error(getApplicationContext(), "블루투스가 활성화 되지 않았습니다 프로그램을 종료합니다", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            case REQUEST_APPROVE_KSNET: {
                Request rs = new Request();   //
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
                    responseobj = (responseObj) GsonUtil.fromJson(extra.get("resultData").toString(), new responseObj());

                    // HashMap<String, String> mhashApprove = (HashMap<String, String>) data.getSerializableExtra("result");
                    HashMap<String, String> mhashApprove = responseobj.geMap();
                    if (mhashApprove != null) {
                        Iterator<String> iterator = mhashApprove.keySet().iterator();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Log.d("debug", "key=" + key);
                            Log.d("debug", " value=" + mhashApprove.get(key));

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

                            Toasty.success(MtouchPayKsnet.this, "정상 승인 되었습니다", Toast.LENGTH_LONG, true).show();

                            rs.data.put("number", "****************".trim());
                            if (mhashApprove.get("CardNo") != null && mhashApprove.get("CardNo").length() > 5) {
                                if (tools.CheckNumber(mhashApprove.get("CardNo").substring(0, 5))) {
                                    rs.data.put("number", mhashApprove.get("CardNo").trim());
                                }
                            }

                            rs.data.put("authCd", ksnetresp.getString("AuthNum").trim());    //  승인 번호
                            rs.data.put("regDate", ksnetresp.getString("Authdate").trim());    //  승인 번호


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
                                        ksnetresp.PrnTempleteData(0,
                                                ksnetresp.getString("name"),
                                                ksnetresp.getString("ceoName"),
                                                ksnetresp.getString("identity"),
                                                ksnetresp.getString("telNo"),
                                                ksnetresp.getString("addr"),
                                                ksnetresp.getString("vat")));


                                for (String key : rs.data.keySet()) {
                                    if (!ksnetresp.data.containsKey(key)) {
                                        ksnetresp.data.put(key, (String) rs.data.get(key));
                                    }
                                }


                                APPROVE_RESULT_INFO(ksnetresp.toJsonString());

                                //----------------------------------->>>>>>>>>>>>>>>>>>>현금영수증
                            } else {
                                //<<<<<<<<<<<<<<<<<<----------------------------------- 일반결제 승인일경우
                                prnSendData(" 신용카드전표",
                                        ksnetresp.PrnTempleteData(0,
                                                ksnetresp.getString("name"),
                                                ksnetresp.getString("ceoName"),
                                                ksnetresp.getString("identity"),
                                                ksnetresp.getString("telNo"),
                                                ksnetresp.getString("addr"),
                                                ksnetresp.getString("vat")));

                                rs.data.put("sendcount", "0".trim());

                                if (TEST) {
                                    SunUtil.setAppPreferences(MtouchPayKsnet.this, "approve", rs.toJsonString());
                                }


                                for (String key : rs.data.keySet()) {
                                    if (!ksnetresp.data.containsKey(key)) {
                                        ksnetresp.data.put(key, (String) rs.data.get(key));
                                    }
                                }


                                APPROVE_RESULT_INFO(ksnetresp.toJsonString());
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
                            _jsCallHandler.payResult(rs.toJsonString());  // 웹에 보냅니다

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
                    Toasty.error(MtouchPayKsnet.this, "케이에스체크IC 에서 가맹점 다운로드 후 사용하시기 바랍니다", Toast.LENGTH_LONG);
                } else {
                    rs.data.put("type", "승인실패");
                    rs.data.put("resultMsg", "응답값 리턴 실패");
                    rs.data.put("regDate", tools.getSysDateTIme());
                    sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
//                        Toast.makeText(this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
                    Toasty.error(MtouchPayKsnet.this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
                    _jsCallHandler.forceGoHome();

                    /* 사용자 실퍠* */
//# 응답 시 200 인 경우 수신되는 van,vanId,trackId,secondKey,authCd,regDay 는 Application 으로 전달되어야 하며 secondKey 의 경우 KSNET에서 사용한다.
                }
                // 수행을 제대로 하지 못한 경우
                if (resultCode == RESULT_CANCELED) {
                    rs.data.put("type", "승인실패");
                    rs.data.put("resultMsg", "취소키가 눌러짐");
                    rs.data.put("regDate", tools.getSysDateTIme());
                    Toasty.error(MtouchPayKsnet.this, "취소가 되었습니다", Toast.LENGTH_SHORT).show();
                    sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
                    _jsCallHandler.forceGoHome();
                }
            }
            break;
            case REQUEST_APPROVE_CANCEL_KSNET: {
                Request rs = new Request();   //
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
                    responseobj = (responseObj) GsonUtil.fromJson(extra.get("resultData").toString(), new responseObj());

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
                            Toasty.success(MtouchPayKsnet.this, "승인 취소 되었습니다", Toast.LENGTH_LONG, true).show();

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
                                SunUtil.setAppPreferences(MtouchPayKsnet.this, "approve", rs.toJsonString());
                            }

                            for (String key : rs.data.keySet()) {
                                if (!ksnetresp.data.containsKey(key)) {
                                    ksnetresp.data.put(key, (String) rs.data.get(key));
                                }
                            }


                            APPROVE_RESULT_INFO(ksnetresp.toJsonString());
                            Log.d("tag", "==================ksnetresp.toJsonString()====================" + ksnetresp.toJsonString());


                            sendMessage(NETWORK_SERVER_SEND, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다
                            prnSendData(" 카드승인취소",
                                    ksnetresp.PrnTempleteData(1,
                                            ksnetresp.getString("name"),
                                            ksnetresp.getString("ceoName"),
                                            ksnetresp.getString("identity"),
                                            ksnetresp.getString("telNo"),
                                            ksnetresp.getString("addr"),
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
                            _jsCallHandler.payResult(rs.toJsonString());  // 웹에 보냅니다
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
                    Toasty.error(MtouchPayKsnet.this, "케이에스체크IC 에서 가맹점 다운로드 후 사용하시기 바랍니다", Toast.LENGTH_LONG);
                    //      Toast.makeText(this, "케이에스체크IC 에서 가맹점 다운로드 후 사용하시기 바랍니다", Toast.LENGTH_LONG).show();
                } else {
                    rs.data.put("type", "승인실패");
                    rs.data.put("resultMsg", "응답값 리턴 실패");
                    rs.data.put("regDate", tools.getSysDateTIme());
                    _jsCallHandler.forceGoHome();
                    Toasty.error(MtouchPayKsnet.this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
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
                    Toasty.error(this, "앱 호출 실패", Toast.LENGTH_LONG).show();
                    _jsCallHandler.forceGoHome();
                    sendMessage(NETWORK_FAIL_CEHCK, rs.toJsonString());  // 이곳에서 네트워크를 전송합니다

                }

            }
            break;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("tag", "bt printer Handler " + msg.what + "/" + msg.getData());
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   // BT Print Connect
                            KSNETStatus.BT_PRN_FLAG = true;
                            _jsCallHandler.sendPrint("1");
                            SunUtil.setAppPreferences(MtouchPayKsnet.this, "bt_address", KSNETStatus.BT_PRN_ADDRESS);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    _jsCallHandler.sendPrint("1");
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toasty.info(getApplicationContext(),
                            mConnectedDeviceName + "접속 중입니다",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    // 이곳에 프린터가 끊어진 곳입니다 .
                    _jsCallHandler.sendPrint("0");
                    ;
                    Toasty.info(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_CONNECTION_LOST:
                    KSNETStatus.BT_PRN_FLAG = false;
                    _jsCallHandler.sendPrint("0");
                    Toasty.error(getApplicationContext(), "프린터기와 연결이 끊어졌습니다", Toast.LENGTH_SHORT, true).show();
                    break;
                case MESSAGE_UNABLE_CONNECT:     //
                    KSNETStatus.BT_PRN_FLAG = false;
                    _jsCallHandler.sendPrint("0");
                    KSNETStatus.BT_PRN_ADDRESS = null;
                    Toasty.error(getApplicationContext(), "프린터기와 연결을 할 수 없습니다", Toast.LENGTH_SHORT, true).show();
                    break;

                /* WEB View Call Handler */
                case CyrexNetworkStatus.HANDLER_GFETDATA_KEY: {
                    Log.d("KSNETStatus", " KSNETStatus.HANDLER_GFETDATA_KEY ");
                    String data = msg.getData().getString("data");
                    try {
                        JSONObject jo = new JSONObject();
                        Iterator i = jo.keys();
                        while (i.hasNext()) {
                            String key = i.next().toString();
                            SunUtil.setAppPreferences(MtouchPayKsnet.this, key, jo.getString(key));
                            Log.d("WEB", key);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case CyrexNetworkStatus.HANDLER_FINISH: {
                    Log.d("KSNETStatus", " KSNETStatus.HANDLER_FINISH ");
                    finish();
                }
                break;
                case CyrexNetworkStatus.HANDLER_PAYSTART: {
                    ksnetreq.clearHash();
                    Log.d("tag", " KSNETStatus.HANDLER_PAYSTART ");
                    String data = msg.getData().getString("data");
                    Log.d("tag", "HANDLER_PAYSTART :" + data);
                    try {
                        JSONObject object = new JSONObject(data);
                        Iterator keys = object.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            ksnetreq.put(key, ((String) object.get(key)));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ksNetMessage(REQUEST_APPROVE_KSNET, data);  // 일시불 , 승인금액 , Trxkey

                }
                break;
                case CyrexNetworkStatus.HANDLER_SENDPRINT: {
                    if (KSNETStatus.BT_PRN_FLAG) {
                        String data = msg.getData().getString("data");
                        String trxResult = "";
                        Log.d("tag", " KSNETStatus.HANDLER_SENDPRINT " + data);
                        try {
                            JSONObject jo = new JSONObject(data);
                            Iterator keys = jo.keys();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                ksnetresp.put(key, ((String) jo.get(key)));
                            }

                            ksnetresp.put("Message1", jo.getString("brand"));
                            ksnetresp.put("CardNo", jo.getString("number"));
                            ksnetresp.put("PurchaseName", jo.getString("issuer"));
                            ksnetresp.put("TotalAmount", jo.getString("amount"));
                            ksnetresp.put("AuthNum", jo.getString("authCd"));
                            ksnetresp.put("installment", jo.getString("installment"));
                            try {
                                trxResult = jo.getString("trxResult");
                            } catch (JSONException j) {
                                ksnetresp.put("trxResult", "");
                            }
                            Log.d("tag", "menu_status =================" + menu_status + " trxResult : " + trxResult);
                            if (trxResult == null || trxResult.length() < 1) {
                                if (KSNETSATTUS == REQUEST_APPROVE_KSNET) {
                                    ksnetresp.put("trxResult", "승인");
                                } else if (KSNETSATTUS == REQUEST_APPROVE_CANCEL_KSNET) {
                                    ksnetresp.put("trxResult", "승인취소");
                                }
                            } else {
                                ksnetresp.put("trxResult", jo.getString("trxResult"));
                            }

                            //ksnetresp.put("trxResult", jo.getString("trxResult"));

                            if (ksnetresp.data.get("trxResult").indexOf("취소") >= 0) {
                                prnSendData(" 승인전표취소",
                                        ksnetresp.PrnTempleteData(1,
                                                jo.getString("name"),
                                                jo.getString("ceoName"),
                                                jo.getString("identity"),
                                                jo.getString("telNo"),
                                                jo.getString("addr"),
                                                jo.getString("vat"))
                                );
                            } else {
                                prnSendData(" 신용카드전표",
                                        ksnetresp.PrnTempleteData(0,
                                                jo.getString("name"),
                                                jo.getString("ceoName"),
                                                jo.getString("identity"),
                                                jo.getString("telNo"),
                                                jo.getString("addr"),
                                                jo.getString("vat"))
                                );

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        BTPrinter();  // 연결이 끊어지면 다시 연결 합니다
                    }

                }
                break;
                case CyrexNetworkStatus.HANDLER_REFUND: {
                    String data = msg.getData().getString("data");
                    ksNetMessage(REQUEST_APPROVE_CANCEL_KSNET, data);  // 일시불 , 승인금액 , Trxkey
                }
                break;
                case CyrexNetworkStatus.HANDLER_RETRY_PRINT: {
                    String title = msg.getData().getString("title");
                    String message = msg.getData().getString("msg");
                    if (mService.getState() == BluetoothService.STATE_CONNECTED) {
                        SendDataByte(PrinterCommand.POS_Print_Text(title, KOREAN, 0, 1, 1, 0));
                        SendDataByte(PrinterCommand.POS_Print_Text(message, KOREAN, 0, 0, 0, 0));
                        SendDataByte(Command.LF);
                    }
                }
                break;
                case CyrexNetworkStatus.HANDLER_SMS: {
                    String data = msg.getData().getString("data");
                    Log.d("debug", "===========HANDLER_SMS======================data " + data);
                    try {
                        String receive = new JSONObject(data).getString("receive");
                        sendSMS(receive, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case CyrexNetworkStatus.HANDLER_SEARCH_PRINTER: {
//                    timerShowDialog("프린터연결", "프린터를 찾아서 연결중입니다..",  2000);
                    BTPrinter();
                }
                break;
                case CyrexNetworkStatus.HANDLER_STORE_INFO: {
                    String data = msg.getData().getString("data");
                    StoreInfoHttp();
                }
                break;
                case CyrexNetworkStatus.HANDLER_APPROVE_RESULT_INFO: {
                    if (ksnetresp.getString("trxId") == null || ksnetresp.getString("trxId").length() < 5)
                        _jsCallHandler.payResult(msg.getData().getString("data"));
                         /*
                         try {
                             JSONObject jsonData = new JSONObject(msg.getData().getString("data")).getJSONObject("data");
                             jsonData.put("STX","");
                             if(! jsonData.has("trxId")) jsonData.put("trxId","");
                             String data = new JSONObject().put("data",jsonData).toString();

                             _jsCallHandler.payResult(data);
                         }catch (Exception e){

                         }
                         */
                }
                break;
                case CyrexNetworkStatus.HANDLER_CALC_PRINTER: {
                    KsnetPrnObj webresponseprn = (KsnetPrnObj) GsonUtil.fromJson(msg.getData().getString("data"), KsnetPrnObj.class);
                    Log.d("tag", "&&&&&&&&&&&&" + webresponseprn.PrnTempleteData() + "&&&&&&&&&&&&");
                    if (mService.getState() == BluetoothService.STATE_CONNECTED) {
                        SendDataByte(PrinterCommand.POS_Print_Text(webresponseprn.PrnTempleteData(), KOREAN, 0, 0, 0, 0));
                        SendDataByte(Command.LF);
                    }
                }
                break;
                case CyrexNetworkStatus.HANDLER_CALC_SMS: {
                    KsnetPrnObj webresponseprn = (KsnetPrnObj) GsonUtil.fromJson(msg.getData().getString("data"), KsnetPrnObj.class);
                    Log.d("tag", "&&&&&&&&&&&&" + webresponseprn.PrnTempleteData() + "&&&&&&&&&&&&");
                    sendSMS(webresponseprn.PrnTempleteData());
                }
                break;
                case CyrexNetworkStatus.HANDLER_TELL_CALL: {
                    if (KSNETStatus.telNo == null || KSNETStatus.telNo.length() < 5) {
                        Log.d("debug", "TTTTTTTTTTTTTTTTTTT" + KSNETStatus.telNo + ":" + msg.getData().getString("data") + "TTTTTTTTTTTTTTTTTTT");
                        Toasty.info(getApplicationContext(), "전화번호가 전화를 걸 수 없습니다", Toast.LENGTH_SHORT);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(msg.getData().getString("data")));
                        startActivity(intent);
                    }
                }
                break;
                case CyrexNetworkStatus.HANDLER_CASH_PAY: {
                    Log.w("handler", "HANDLER_CASH_PAY");

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
                        String data = msg.getData().getString("data");
                        Log.d("tag", "HANDLER_ CASHPAY START :" + data);

                        try {
                            JSONObject object = new JSONObject(data);
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
                            JSONObject jo = new JSONObject(data);
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

                            //부가세 및 봉사료
                            String taxflag = SunUtil.getAppPreferences(MtouchPayKsnet.this, "taxflag");
                            String serviceflag = SunUtil.getAppPreferences(MtouchPayKsnet.this, "serviceflag");

                            //taxAmountValue
                            //chargeAmountValue
                            String taxAmountValue = SunUtil.getAppPreferences(MtouchPayKsnet.this, "taxAmountValue");
                            String chargeAmountValue = SunUtil.getAppPreferences(MtouchPayKsnet.this, "chargeAmountValue");


//                                if (taxflag != null && taxflag.equals("0")) {
//                                    //사용함
//                                    int TotalAmount = Integer.parseInt(amount);  //총금액
//                                    int TaxAmount = (int) ((TotalAmount / 1.1) * 0.1);     // 부가세
//                                    int Amount = Math.round(TotalAmount - TaxAmount); // 공급가액
//
//                                    ksnetreq.put("amount", new String(tools.getStrMoneytoTgAmount("" + TotalAmount))); // 총금액
//                                    ksnetreq.put("TaxAmount", new String(tools.getStrMoneytoTgAmount("" + TaxAmount)));     // 부가세
//                                    ksnetreq.put("Amount", new String(tools.getStrMoneytoTgAmount(""+Amount)));     // 공급금액 = 총금액 - 부가세 - 봉사료
//
//                                } else if (taxflag != null && taxflag.equals("1")) {
//                                    //사용 안함
//                                    ksnetreq.put("amount", new String(tools.getStrMoneytoTgAmount("" + amount))); // 총금액
//                                    ksnetreq.put("TaxAmount", new String(tools.getStrMoneytoTgAmount("0")));     // 부가세
//                                    ksnetreq.put("Amount", new String(tools.getStrMoneytoTgAmount(""+amount)));
//
//                                } else {
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
                break;
            }
        }
    };

    public void KSNET_APP_CALL(HashMap<String, byte[]> m_byte_hash, int menu) {

        if (System.currentTimeMillis() > ksnetsendTime + CyrexNetworkStatus.KSNET_APP_CALL_EXPIRE_TIME)  // 2분동안 아무런 일이 발생되지 않으면 다시 KSNET어플을 호출 합니다
        {
            ksnetsendTime = System.currentTimeMillis();
            CyrexNetworkStatus.KSNET_APP_CALL = false;
        }

        if (!CyrexNetworkStatus.KSNET_APP_CALL) {
            CyrexNetworkStatus.KSNET_APP_CALL = true;
            /*
                ComponentName compName = new ComponentName("ks.ksciclv", "ks.ksciclv.PaymentDlg");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(compName);

                intent.putExtra("AdminInfo_Hash", m_byte_hash);
                //intent.putExtra("AdminInfo_Hash", m_hash);
                intent.setAction("test");
                startActivityForResult(intent, menu);
            */
            Intent intent = new Intent(MtouchPayKsnet.this, PayResultActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // intent.setComponent(compName);

            intent.putExtra("AdminInfo_Hash", m_byte_hash);
            //intent.putExtra("AdminInfo_Hash", m_hash);
            intent.setAction("test");
            startActivityForResult(intent, menu);

        }

    }

    public void APPROVE_RESULT_INFO(String result) {
        Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_APPROVE_RESULT_INFO);
        Bundle bundle = new Bundle();
        bundle.putString("data", result);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, 1000 * 10);
    }

    public void goHome() {
        if (!isFinishing() && webView != null)
            _jsCallHandler.forceGoHome();
    }

    public void sendSMS(String smsText) {
        if (KSNETStatus.telNo == null || KSNETStatus.telNo.length() < 5) {
            Toasty.info(this, "핸드폰의 전화번호가 없어 SMS를 발송할수 없습니다", Toast.LENGTH_SHORT);
            //       return;
        }

        Uri smsUri = Uri.parse("smsto:" + "");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
        intent.putExtra("address", "");
        intent.putExtra("sms_body", smsText);
//    intent.setType("vnd.android-dir/mms-sms");

        startActivity(intent);
        Toasty.info(MtouchPayKsnet.this, "발송 전 받는 분 전화번호 확인.", Toast.LENGTH_SHORT).show();

/*

        PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        SmsManager mSmsManager = SmsManager.getDefault();
        try {




            if (smsText.length() > 70) {
                ArrayList<String> contents = mSmsManager.divideMessage(smsText);
                mSmsManager.sendMultipartTextMessage(KSNETStatus.telNo, null, contents, null, null);

            } else {
                mSmsManager.sendTextMessage(KSNETStatus.telNo, null, smsText, null, null);
            }
            Toasty.info(MtouchPayKsnet.this, "전송되었습니다.", Toast.LENGTH_SHORT).show();
        }  catch ( Exception e) {
            e.printStackTrace();
        }
*/
    }

    public void sendSMS(String smsNumber, String smsText) {
        Log.d("debug", "==============================" + smsNumber + "====data " + smsText);

        if (KSNETStatus.telNo == null || KSNETStatus.telNo.length() < 5) {
            Toasty.info(this, "핸드폰의 전화번호가 없어 SMS를 발송이 안될 수 있습니다", Toast.LENGTH_SHORT);
//            return;
        }

        //   if(smsNumber == null  || smsNumber.length() < 5) return ;
        //    PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT_ACTION"), 0);
        //    PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        //    SmsManager mSmsManager = SmsManager.getDefault();
        try {
            JSONObject jo = new JSONObject(smsText);

            StringBuffer sb = new StringBuffer();
            sb.append("가맹점:");
            sb.append(jo.getString("name"));
            sb.append("\n");
            sb.append("카드사:");
            sb.append(jo.getString("brand"));
            sb.append("\n");
            sb.append("카드번호:");
            sb.append(jo.getString("number"));
            sb.append("\n");
            sb.append("승인결과:");
            sb.append(jo.getString("trxResult"));
            sb.append("\n");
            sb.append("승인번호:");
            sb.append(jo.getString("authCd"));
            sb.append("\n");
            sb.append("승인금액:");
            sb.append(jo.getString("amount"));
            sb.append("\n");
            sb.append("할부기간:");
            sb.append(jo.getString("installment"));
            sb.append("\n");
            sb.append("승인일자:");
            sb.append(jo.getString("regDate"));

//    Uri smsUri = Uri.parse("tel:" + smsNumber);
            Uri smsUri = Uri.parse("smsto:" + smsNumber);
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
            intent.putExtra("address", smsNumber);
            intent.putExtra("sms_body", sb.toString());
//    intent.setType("vnd.android-dir/mms-sms");

            startActivity(intent);
/*
    if (sb.toString().length() > 70) {
        ArrayList<String> contents = mSmsManager.divideMessage(sb.toString());
        mSmsManager.sendMultipartTextMessage(smsNumber, null, contents, null, null);

    } else {
        mSmsManager.sendTextMessage(smsNumber, null, sb.toString(), null, null);
    }
*/
            Toasty.info(MtouchPayKsnet.this, "발송 전 받는 분 전화번호 확인", Toast.LENGTH_SHORT).show();

            //  mSmsManager.sendTextMessage("01032616447", null, sb.toString().substring(0,38) , sentIntent, deliveredIntent);
            //   카드사 카드번호 승인취소여부, 승인 번호(authCd) 금액(amount), 할부 installment regDate, name
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLSMS(String smsNumber, String smsText) {
        if (KSNETStatus.telNo == null || KSNETStatus.telNo.length() < 5) {
            Toasty.info(this, "핸드폰의 전화번호가 없어 SMS를 발송할수 없습니다", Toast.LENGTH_SHORT);
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        String sendTo = smsNumber;
        ArrayList partMessage = smsManager.divideMessage(smsText);
        smsManager.sendMultipartTextMessage(sendTo, null, partMessage, null, null);
        Toasty.info(MtouchPayKsnet.this, "전송되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void BVT_PRN_START(int i) {
        if ((KSNETStatus.BT_PRN_ADDRESS != null) && (KSNETStatus.BT_PRN_ADDRESS.length() > 0)) {  // 바로 접솝합니다
            if (BluetoothAdapter.checkBluetoothAddress(KSNETStatus.BT_PRN_ADDRESS)) {
                BluetoothDevice device = mBluetoothAdapter
                        .getRemoteDevice(KSNETStatus.BT_PRN_ADDRESS);
                // Attempt to connect to the device
                mService.connect(device);
            }
        } else {
            if (i == 1) {
                Intent serverIntent = new Intent(MtouchPayKsnet.this, DeviceListActivity.class);
                serverIntent.putExtra("name", "Printer");
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        }
    }

    /*
    프리터가 연결 되어 있지 않으면 2초간 한던 돌린 후 작업 합니다
     */
    public void prnSendData(String title, String message) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toasty.info(this, "프린터가 연결되지 않았습니다", Toast.LENGTH_SHORT)
                    .show();
            BTPrinter();

            try {
                Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_RETRY_PRINT);
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("msg", message);
                msg.setData(bundle);
                mHandler.sendMessageDelayed(msg, 1000 * 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SendDataByte(PrinterCommand.POS_Print_Text(title, KOREAN, 0, 1, 1, 0));
            SendDataByte(PrinterCommand.POS_Print_Text(message, KOREAN, 0, 0, 0, 0));
            SendDataByte(Command.LF);
        }
    }

    /*
     * SendDataString
     */
    private void SendDataString(String data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toasty.error(this, "프린터가 연결되지 않았습니다", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
     *SendDataByte
     */
    private void SendDataByte(byte[] data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toasty.error(this, "프린터가 연결되지 않았습니다", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }


    private void Print_Test() {
        String msg = tools.getMysqlDate() + "ABC한글\n\n\n\n\n";
        SendDataByte(PrinterCommand.POS_Print_Text(msg, KOREAN, 0, 0, 0, 0));
        SendDataByte(PrinterCommand.POS_Set_Cut(1));
        SendDataByte(PrinterCommand.POS_Set_PrtInit());
    }


    private void KeyListenerInit() {
        Log.d("debug", "start bt printer ==================");
        mService = new BluetoothService(this, mHandler);
    }


    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPhonePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("tag", "ss" + checkSelfPermission(Manifest.permission.READ_PHONE_STATE));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
                // We do not have this permission. Let's ask the user
            } else {
                getPhoneNum();
            }
        } else {
            getPhoneNum();
        }
    }

    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.M)
    private void checkSmsPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("tag", "ss" + checkSelfPermission(Manifest.permission.SEND_SMS));
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
                // We do not have this permission. Let's ask the user
            } else {
                getPhoneNum();
            }
        } else {
            getPhoneNum();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhoneNum();
                    checkSmsPermission();
                    // permission granted!
                    // you may now do the action that requires this permission
                } else {
                    // permission denied
                    finish();
                }
                return;
            }
            case PERMISSION_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                } else {
                    // permission denied
                }
                return;
            }
        }
    }


    public String getPhoneNum() {
        /*
        String myNumber = null;
        TelephonyManager mgr;
        mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                // TODO: Consider calling
                myNumber = mgr.getLine1Number();
                myNumber = myNumber.replace("+82", "0");
                KSNETStatus.telNo = myNumber;
                Log.d("tag" ,"checkSelfPermission true " + KSNETStatus.telNo );
                //return KSNETStatus.telNo;
            } else {
                Log.d("tag" ,"checkSelfPermission false");
                KSNETStatus.telNo = "" ;
            }

		}catch(Exception e){
            KSNETStatus.telNo = "" ;
        }

//        firstIntialize();  // 웹 뷰를 불러 옵니다
*/
        return "";
    }

    public boolean getCheckApplication(String pakage) {
        boolean isAppInstalled = false;
        isAppInstalled = appInstalledOrNot("ks.ksciclv");
        if (isAppInstalled) {
            return true;
        } else {
            ShowKSNETDialog("KSNETIC(LowV) 어플을 설치해야합니다");
            return false;
            //finish();
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

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityMgr.getActiveNetworkInfo();
        /// if no network is available networkInfo will be null
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void ShowLog() {
        AlertDialog.Builder aDialog = new AlertDialog.Builder(MtouchPayKsnet.this);
        aDialog.setTitle("프로그램을 종료 하시겠습니까?");

        aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                finish();
            }
        });
        aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        AlertDialog ad = aDialog.create();
        ad.show();
    }

    public void timerShowDialog(String title, String message, int millis) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle(title).setMessage(message);

        final AlertDialog d = dialog.create();
        d.show();

        // Hide after some seconds

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (d.isShowing()) {
                    d.dismiss();
                }
            }
        };

        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, millis);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        if(webView!=null && webView.canGoBack()){
//            webView.goBack();
//            return;
//        }

        if (_jsCallHandler != null)
            _jsCallHandler.onBack();

        if (System.currentTimeMillis() > backKeyPressedTime + 1000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toasty.info(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 1000) {
            ShowLog();
            //finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PHONE) {
            if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the session
            } else {
                if (mService == null)
                    KeyListenerInit();
            }
        }
    }

    private void SystemExit() {
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void closeBTConnection() {
        System.out.println("######closeBTConnection()");
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(BlueToothStatusChangeReceiver);
        } catch (Exception unused) {
        }
    }


    @Override
    public synchronized void onResume() {
        super.onResume();
        if (PHONE) {
            if (mService != null) {
                if (mService.getState() == BluetoothService.STATE_NONE) {
                    // Start the Bluetooth services
                    try {
                        mService.start();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (DEBUG)
            Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG)
            Log.e(TAG, "-- ON STOP --");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (PHONE) {
            if (mService != null)
                mService.stop();
            if (DEBUG)
                Log.e(TAG, "--- ON DESTROY ---");
        }
        try {
            unbindService(mServiceConnection);
        } catch (Exception e) {
        }
        closeBTConnection();
        System.out.println("#####OnDestroy_TabMain ActionConnect");
        SystemExit();
    }


}


//        Toasty.info(MtouchPayKsnet.this,  "정상 승인 되었습니다", Toast.LENGTH_LONG, true).show();
//        Toasty.error(MtouchPayKsnet.this,  "승인 실폐", Toast.LENGTH_LONG, true).show();
//     Toasty.error(MtouchPayKsnet.this, "This is an error toast.", Toast.LENGTH_SHORT, true).show();
//    Toasty.success(MainActivity.this, "Success!", Toast.LENGTH_SHORT, true).show();
//     Toasty.info(MainActivity.this, "Here is some info for you.", Toast.LENGTH_SHORT, true).show();
//     Toasty.warning(MainActivity.this, "Beware of the dog.", Toast.LENGTH_SHORT, true).show();
//    Toasty.normal(MainActivity.this, "Normal toast w/o icon").show();
//    Toasty.normal(MainActivity.this, "Normal toast w/ icon", icon).show();
//    Toasty.info(MainActivity.this, getFormattedMessage()).show();
//    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},1);
//          BTPrinter();



