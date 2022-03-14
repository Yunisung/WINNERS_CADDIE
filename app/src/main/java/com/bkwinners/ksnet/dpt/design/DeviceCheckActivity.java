package com.bkwinners.ksnet.dpt.design;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bkwinners.ksnet.dpt.Toasty;
import com.bkwinners.ksnet.dpt.action.PayResultActivity;
import com.bkwinners.ksnet.dpt.action.process.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.PermissionActivity;
import com.bkwinners.ksnet.dpt.ks03.bluetooth.DeviceRegistActivity;
import com.bkwinners.ksnet.dpt.ks03.obj.KSnetRequestObj;
import com.bkwinners.ksnet.dpt.ks03.obj.KsnetPrnObj;
import com.bkwinners.ksnet.dpt.ks03.obj.KsnetResponseObj;
import com.bkwinners.ksnet.dpt.ks03.obj.Response;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus;
import com.bkwinners.ksnet.dpt.ks03.pay.ksnet.KSNETStatus;
import com.nordicsemi.nrfUARTv2.UartService;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
import com.pswseoul.comunity.http.HttpGetTask;
import com.pswseoul.comunity.http.HttpPostTask;
import com.pswseoul.comunity.imp.AsyncTaskCompleteListener;
import com.pswseoul.util.GsonUtil;
import com.pswseoul.util.NetworkUtil;
import com.pswseoul.util.SunUtil;
import com.pswseoul.util.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

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
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.TEST;
import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.menu_status;

public class DeviceCheckActivity extends AppCompatActivity implements AsyncTaskCompleteListener {

    public static final int REQUEST_CODE_REGIST_ACTIVITY = 1111;
    public static final int REQUEST_CODE_PERMISSION_ACTIVITY = 2222;

    private static final String CHINESE = "GBK";
    private static final String THAI = "CP874";
    private static final String KOREAN = "EUC-KR";
    private static final String BIG5 = "BIG5";

    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;

    public static final int MESSAGE_RECONNECT_CONNECT = 8;

    // Intent request codes
    protected static final int REQUEST_CONNECT_DEVICE = 1000;
    protected static final int REQUEST_ENABLE_BT = 1200;
    protected static final int REQUEST_ENABLE_GPS = 1400;

    // Ksnet Connect Test
    protected static final int REQUEST_APPROVE_KSNET = 10000;
    protected static final int REQUEST_APPROVE_CANCEL_KSNET = 11000;

    protected int KSNETSATTUS = 0;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    int latestVersion = 0;

    protected KSnetRequestObj ksnetreq = new KSnetRequestObj();
    protected KsnetResponseObj ksnetresp = new KsnetResponseObj();


    protected LocationManager locationManager;

    // Name of the connected device
    String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    protected BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    protected BluetoothService mService = null;
    private boolean isNeedRegist = true;

//    public JsCallHandler _jsCallHandler;


    public void setNeedRegist(boolean needRegist) {
        isNeedRegist = needRegist;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    public void initialize() {

        //블루투스 확인
//        if (this.mBluetoothAdapter == null) {
//            Toast.makeText(this, "블루투스를 사용할 수 없습니다.\n앱을 종료합니다.", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        } else if (!this.mBluetoothAdapter.isEnabled()) {
//            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
//            return;
//        }
//
//            //위치옵션 확인
//            if (locationManager == null) {
//                Toast.makeText(this, "GPS를 사용할 수 없습니다.\n앱을 종료합니다.", Toast.LENGTH_SHORT).show();
//                finish();
//                return;
//            } else if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    Toast.makeText(getApplicationContext(), "위치옵션을 설정해주시기 바랍니다.", Toast.LENGTH_LONG).show();
//                }else {
//                    Toast.makeText(getApplicationContext(), "위치옵션을 설정해주시기 바랍니다.\n위치 인식 방식에서 GPS를 사용하는 옵션으로 설정해야 합니다.", Toast.LENGTH_LONG).show();
//                }
//                new Handler().postDelayed(() -> {
//                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS);
//                }, 500);
//                return;
//            }


//        service_init();

        requestPermission();

        if (SharedPreferenceUtil.getData(this, Constants.KEY_MAC_ADDRESS, "NONE").equals("NONE") && isNeedRegist) {
            startActivityForResult(new Intent(this, DeviceRegistActivity.class), REQUEST_CODE_REGIST_ACTIVITY);
        }
    }

    public boolean requestPermission() {
        if (SharedPreferenceUtil.getData(this, "FIRST_VIEW", "false").equals("false")
                || SharedPreferenceUtil.getData(this, "FIRST_VIEW", "").length() == 0) {
            //최초설치사용자

            Intent intent = new Intent(this, PermissionActivity.class);
            intent.putExtra("permission", new String[]{"android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.GET_ACCOUNTS"});
            intent.putExtra("permission1", "- 포그라운드 위치 : BLE사용\n- 저장공간 : 앱설정 저장\n- 전화 : 휴대폰번호 전송\n");
            intent.putExtra("permission2", "- SMS : 문자메시지 발송\n- 주소록 :이메일 발송\n");
            intent.putExtra("require", new String[]{"ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION", "WRITE_EXTERNAL_STORAGE", "READ_PHONE_STATE"});
            startActivityForResult(intent, 0);
            return false;
        } else if (SharedPreferenceUtil.getData(this, "SECOND_VIEW", "false").equals("false")
                || SharedPreferenceUtil.getData(this, "SECOND_VIEW", "").length() == 0) {

            Intent intent = new Intent(this, PermissionActivity.class);
            intent.putExtra("permission", new String[]{"android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.GET_ACCOUNTS"});
            intent.putExtra("permission1", "- 포그라운드 위치 : BLE사용\n- 저장공간 : 앱설정 저장\n- 전화 : 휴대폰번호 전송\n");
            intent.putExtra("permission2", "- SMS : 문자메시지 발송\n- 주소록 :이메일 발송\n");
            intent.putExtra("require", new String[]{"ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION", "WRITE_EXTERNAL_STORAGE", "READ_PHONE_STATE"});
            startActivityForResult(intent, 1);
            return false;
        }

        //사용중에 거부설정한 사람대상체크
        if(ContextCompat.checkSelfPermission(this,"android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this,"android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this,"android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this,"android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this,"android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this,"android.permission.GET_ACCOUNTS") == PackageManager.PERMISSION_DENIED
        ){
            Intent intent = new Intent(this, PermissionActivity.class);
            intent.putExtra("permission", new String[]{"android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.GET_ACCOUNTS"});
            intent.putExtra("permission1", "- 포그라운드 위치 : BLE사용\n- 저장공간 : 앱설정 저장\n- 전화 : 휴대폰번호 전송\n");
            intent.putExtra("permission2", "- SMS : 문자메시지 발송\n- 주소록 :이메일 발송\n");
            intent.putExtra("require", new String[]{"ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION", "WRITE_EXTERNAL_STORAGE", "READ_PHONE_STATE"});
            startActivityForResult(intent, 1);
            return false;
        }

        return true;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LOG.w("requestCode: " + requestCode + " resultCode : " + resultCode);

        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    LOG.w(String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
                }
            }
        }

        if (resultCode == PermissionActivity.RESULT_PERMISSION_OK) {
            SharedPreferenceUtil.putData(this, "FIRST_VIEW", "true");
            SharedPreferenceUtil.putData(this, "SECOND_VIEW", "true");
            if (!this.mBluetoothAdapter.isEnabled()) {
                startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), REQUEST_ENABLE_BT);
            } else {
                if (SharedPreferenceUtil.getData(this, Constants.KEY_MAC_ADDRESS, "NONE").equals("NONE") && isNeedRegist) {
                    startActivityForResult(new Intent(this, DeviceRegistActivity.class), REQUEST_CODE_REGIST_ACTIVITY);
                }
            }


        } else if (resultCode == PermissionActivity.RESULT_PERMISSION_CANCEL) {
            System.out.println("####RESULT_CANCELED");
            new MtouchDialog(this, v -> finish(),false).setTitleText(getResources().getString(R.string.app_name)).setContentText("필수권한 사용 거부로 인하여 앱이 종료됩니다.").show();
        } else if (resultCode == -2) {
//            if(getPackageName().contains("pay"))
            startActivityForResult(new Intent(this, DeviceRegistActivity.class), REQUEST_CODE_REGIST_ACTIVITY);
        }

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                if (resultCode == RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                    Log.w("tag", "onActivityResult " + resultCode + " resultCode " + resultCode + " address >>>>>>>>>" + address + "<<<<<<<<<");

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
                    initialize();
                } else {
                    // User did not enable Bluetooth or an error occured
                    LOG.w("BT not enabled");
                    Toasty.error(getApplicationContext(), "블루투스가 활성화 되지 않았습니다 프로그램을 종료합니다", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;

            case REQUEST_ENABLE_GPS: {
                if (locationManager == null || !LocationManagerCompat.isLocationEnabled(locationManager)) {
                    new MtouchDialog(this,v->{finish();},false).setTitleText("알림").setContentText("GPS를 사용할 수 없습니다.\n앱을 종료합니다.").show();
                    return;
                }
                initialize();
            }
            break;

        }
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
            Toasty.info(DeviceCheckActivity.this, "발송 전 받는 분 전화번호 확인", Toast.LENGTH_SHORT).show();

            //  mSmsManager.sendTextMessage("01032616447", null, sb.toString().substring(0,38) , sentIntent, deliveredIntent);
            //   카드사 카드번호 승인취소여부, 승인 번호(authCd) 금액(amount), 할부 installment regDate, name
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    protected UartService mmService;
    //UART service connected/disconnected
    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            LOG.w("BTJYP", "ServiceConnection: Connected ");
            mmService = ((UartService.LocalBinder) rawBinder).getService();
            if (!mmService.initialize()) {
                LOG.e("TAG", "Unable to initialize Bluetooth");
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

    protected void service_init() {
//        mService = new BluetoothService(this, mHandler);
//        bindService(new Intent(this, UartService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
//        LocalBroadcastManager.getInstance(this).registerReceiver(this.BlueToothStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    protected final BroadcastReceiver BlueToothStatusChangeReceiver = new BroadcastReceiver() {

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

    protected static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
//            if(mServiceConnection!=null) unbindService(mServiceConnection);
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(BlueToothStatusChangeReceiver);
        }catch (Exception e){}
    }

    @SuppressLint("HandlerLeak")
    protected final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("tag", "bt printer Handler " + msg.what + "/" + msg.getData());
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   // BT Print Connect
                            KSNETStatus.BT_PRN_FLAG = true;
//                            _jsCallHandler.sendPrint("1");
                            SunUtil.setAppPreferences(DeviceCheckActivity.this, "bt_address", KSNETStatus.BT_PRN_ADDRESS);
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
//                    _jsCallHandler.sendPrint("1");
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toasty.info(getApplicationContext(),
                            mConnectedDeviceName + "접속 중입니다",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    // 이곳에 프린터가 끊어진 곳입니다 .
//                    _jsCallHandler.sendPrint("0");

                    Toasty.info(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_CONNECTION_LOST:
                    KSNETStatus.BT_PRN_FLAG = false;
//                    _jsCallHandler.sendPrint("0");
                    Toasty.error(getApplicationContext(), "프린터기와 연결이 끊어졌습니다", Toast.LENGTH_SHORT, true).show();
                    break;
                case MESSAGE_UNABLE_CONNECT:     //
                    KSNETStatus.BT_PRN_FLAG = false;
//                    _jsCallHandler.sendPrint("0");
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
                            SunUtil.setAppPreferences(DeviceCheckActivity.this, key, jo.getString(key));
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

                            ksnetresp.put("Message1", jo.getString("Message1"));
                            ksnetresp.put("CardNo", jo.getString("CardNo"));
                            ksnetresp.put("PurchaseName", jo.getString("PurchaseName"));
                            ksnetresp.put("TotalAmount", jo.getString("TotalAmount"));
                            ksnetresp.put("AuthNum", jo.getString("AuthNum"));
                            ksnetresp.put("Authdate", jo.getString("Authdate").substring(2));
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
                                        ksnetresp.PrnTempleteData(DeviceCheckActivity.this, 1,
                                                jo.getString("vat"))
                                );
                            } else {
                                prnSendData(" 신용카드전표",
                                        ksnetresp.PrnTempleteData(DeviceCheckActivity.this, 0,
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
//                    if (ksnetresp.getString("trxId") == null || ksnetresp.getString("trxId").length() < 5)
//                        _jsCallHandler.payResult(msg.getData().getString("data"));
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
                    } else {
                        BTPrinter();

                        try {
                            Message message = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_CALC_PRINTER);
                            Bundle bundle = new Bundle();
                            bundle.putString("data", msg.getData().getString("data"));
                            message.setData(bundle);
                            mHandler.sendMessageDelayed(message, 1000 * 10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        LOG.w("프린터가 연결되어 있지 않습니다. state : " + mService.getState());
                        Toast.makeText(DeviceCheckActivity.this, "프린터가 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
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
                            String taxflag = SunUtil.getAppPreferences(DeviceCheckActivity.this, "taxflag");
                            String serviceflag = SunUtil.getAppPreferences(DeviceCheckActivity.this, "serviceflag");

                            //taxAmountValue
                            //chargeAmountValue
                            String taxAmountValue = SunUtil.getAppPreferences(DeviceCheckActivity.this, "taxAmountValue");
                            String chargeAmountValue = SunUtil.getAppPreferences(DeviceCheckActivity.this, "chargeAmountValue");


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


//                            new SweetAlertDialog(DeviceCheckActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                if (SharedPreferenceUtil.getData(this, "vat", "Y").equals("N")) {
                    TaxAmount = 0;
                }
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
                    LOG.w("tag", "==============" + ksnetreq.data.size() + "/" + ksnetreq.data.toString());

                Iterator<String> keys = ksnetreq.getMap().keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (Constants.IS_TEST)
                        LOG.w("tag", "==============" + key + "/" + new String(ksnetreq.data.get(key)) + "/" + ksnetreq.data.get(key).length());
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
                ksnetreq.put("trxId", jo.getString("trxId"));
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

    public void KSNET_APP_CALL(HashMap<String, byte[]> m_byte_hash, int menu) {
        Intent intent = new Intent(DeviceCheckActivity.this, PayResultActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.putExtra("AdminInfo_Hash", m_byte_hash);
        ksnetreq.put("key", SharedPreferenceUtil.getData(this, "key"));
        intent.putExtra("payment", ksnetreq.data);
        intent.putExtra("trackId", ksnetreq.data.get("trackId"));
        startActivityForResult(intent, menu);
    }



    /*
    프리터가 연결 되어 있지 않으면 2초간 한던 돌린 후 작업 합니다
     */
    public void prnSendData(String title, String message) {
        if (mService == null) return;
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

    public void BTPrinter() {
        Toasty.info(DeviceCheckActivity.this, "프린터를 검색중입니다.", Toast.LENGTH_SHORT);
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
                Toasty.info(DeviceCheckActivity.this, "프린터를 연결하고 있습니다", Toast.LENGTH_LONG);
                // custom_toast("프린터 연결중입니다......");
            }
        } else {
            Intent serverIntent = new Intent(DeviceCheckActivity.this, DeviceListDialogtActivity.class);
            //Intent serverIntent = new Intent(DeviceCheckActivity.this, DeviceListActivity.class);
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
            Intent serverIntent = new Intent(DeviceCheckActivity.this, DeviceListDialogtActivity.class);
            serverIntent.putExtra("name", "Printer");
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
        */
    }

    private void SendDataByte(byte[] data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toasty.error(this, "프린터가 연결되지 않았습니다", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
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
        Toasty.info(DeviceCheckActivity.this, "발송 전 받는 분 전화번호 확인.", Toast.LENGTH_SHORT).show();

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
            Toasty.info(DeviceCheckActivity.this, "전송되었습니다.", Toast.LENGTH_SHORT).show();
        }  catch ( Exception e) {
            e.printStackTrace();
        }
*/
    }

    public void StoreInfoHttp() {
        //KSNETStatus.getInstance(this);
        //버젼을 체크 합니다
        sendMessage(NETWORK_AVALABLE_KEY, KSNETStatus.token);

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
                                    SunUtil.setAppPreferences(DeviceCheckActivity.this, "approve", "");
                                }
                                if (Constants.IS_TEST) {
                                    Log.d("tag", "=====_jsCallHandler .toJsonString()=======" + response.toJsonString());
                                    Log.d("tag", "=====ksnetresp .toJsonString()=======" + ksnetresp.toJsonString());
                                }
                                ksnetresp.put("STX", "");
                                ksnetresp.put("amount", ksnetresp.getString("TotalAmount"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {    // 다시 보냅시다
                            ksnetresp.put("STX", "");

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








}
