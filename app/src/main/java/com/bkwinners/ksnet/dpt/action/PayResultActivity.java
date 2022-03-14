package com.bkwinners.ksnet.dpt.action;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ksnet.interfaces.Approval;
import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
import com.bkwinners.ksnet.dpt.ErrorActivity;
import com.bkwinners.ksnet.dpt.MainApplication;
import com.bkwinners.ksnet.dpt.action.imp.BlueToothCompleteListener;
import com.bkwinners.ksnet.dpt.action.imp.DefineFuction;
import com.bkwinners.ksnet.dpt.action.obj.receiveObj;
import com.bkwinners.ksnet.dpt.action.obj.responseObj;
import com.bkwinners.ksnet.dpt.action.process.ksnetmodule.EncMSRManager;
import com.bkwinners.ksnet.dpt.action.process.ksnetmodule.obj.AdminInfo;
import com.bkwinners.ksnet.dpt.action.process.searchdevice.SearchBluetTooth;
import com.bkwinners.ksnet.dpt.action.process.util.KsnetParsingByte;
import com.bkwinners.ksnet.dpt.action.process.util.KsnetUtils;
import com.bkwinners.ksnet.dpt.action.process.util.LOG;
import com.bkwinners.ksnet.dpt.action.process.view.PayCreditSign;
import com.bkwinners.ksnet.dpt.common.Utils;
import com.bkwinners.ksnet.dpt.db.PaymentInfo;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.pay.CompleteCheckReceiver;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.nordicsemi.nrfUARTv2.UartService;
import com.pswseoul.comunity.library.BataTime;
import com.pswseoul.comunity.library.BataTimeCallback;
import com.pswseoul.util.AndroidUtils;
import com.pswseoul.util.GsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;

import static com.bkwinners.ksnet.dpt.action.process.util.KsnetParsingByte.IDX_DATA;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.ACTIVITY_KSNET_READER_CONNECT;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.ACTIVITY_KSNET_READER_CONNECTED;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.ACTIVITY_MENU_GET_SIGN;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_ACTION_DATA_AVAILABLE;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_ACTION_GATT_CONNECTED;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_ACTION_GATT_DISCONNECTED;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_ACTION_GATT_SERVICES_DISCOVERED;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_BAT;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_COMPLETE;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_C0;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_C2;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_C3;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_C4;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_C5;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_C6;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_D0;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_D2;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_D3;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_D4;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_D5;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_DPT_D6;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_ICCARD_APPROVE_COMPLETE;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_ICCARD_CANCEL_APPROVE_COMPLETE;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_KSNET_READER_SEARCH;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_RETURN_NOK_ACTIVITY;
import static com.bkwinners.ksnet.dpt.action.process.values.menuStatus.MESSAGE_RETURN_OK_ACTIVITY;


/**
 * Created by parksuwon on 2018-01-29.
 */

public class PayResultActivity extends Activity implements DefineFuction, BlueToothCompleteListener, View.OnClickListener {

    private Realm realm = Realm.getDefaultInstance();

    public final String TAG = "BTJYP";

    public Handler retryHandler = new Handler();
    public Runnable retryRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isReceive) {
                w("BTJYP", "재시도@@@@@@@@@@@@@@@@@@@@@");
                if (mService != null)
                    mService.disconnect();
            }
        }
    };

    public String ReaderModelName;
    public String PhoneNumber;
    public String app_id;
    public String BT_Printer;
    public String BT_Reader;

    public Date appTime = new Date();

    /*  블루투스 관련 자료 */
    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBtAdapter = null;

    private boolean isActionConnected = false;  // 블루투스 연결 여부
    Boolean mBound = false;

    /* 블루투스에서 받은 데이터 입니다 */
    public byte[] _responseTelegram = new byte[2048];
    public byte[] _requestTelegram;
    public byte[] _recvData = new byte[1024];
    public int _recvDataLength = 0;

    public int _recvTmpDataLength = 0;   // 0x02로 들어오는 카드를 막습니다

    /* 클래스 정의 */
    public EncMSRManager encMSRManager;
    public AdminInfo adminInfo;

    public SearchBluetTooth searchBlue;

    public receiveObj receiveobj = new receiveObj();
    public responseObj responseobj = new responseObj();

    //망취소용 필드값
    private byte[] _AuthNum;
    private byte[] _AuthDate;
    private byte[] _TelegramNo = " ".getBytes();
    private byte[] _vanTrxId;

    HashMap<String, byte[]> _HashReaderData = new HashMap<String, byte[]>(); //데이터 처리용 임시변수 , 암호화 정보저장

    public int menuStatus = 0;
    public final int delaytime = 100;
    public final int long_delaytime = 1000;

    //리더기 송신 민감한데이터 목록
    byte[] EMVData;
    byte[] reqEMVData;
    byte[] readerModelNum;
    byte[] trackII;
    byte[] EncryptInfo;
    byte[] Cardbin;

    Intent return_data;

    Boolean isBatChk = false;
    Boolean isICFirstAct = false;
    Boolean isNetCancel = false;
    Boolean isCardCancel = false;
    Boolean isFallback = false;
    Boolean isFirstCardNunResq = true; // 2th 시 카드번호 전송요청 한번 더 날라온다..1th거래 확인용도 추가

    private boolean isOnCreateStart = false;

    private boolean isReceive = false;
    private int retryCount = 0;
    
    public SweetAlertDialog pDialog;

    /* 승인 취소 할때 사용 하는 값입니다 */
    byte[] bAdminNum = null;
    byte[] bAdmindate = null;
    byte[] bEncSign = null;

    BataTime basetime = null;

    final static int TERMINAL_TICK_TIME = 30000;   // 30초
    final static int TERMINAL_FINISH_TIME = 30000 * 12;   // 30초


    private boolean isCancelable = true;
    private boolean isPressBackButton = false;
    private AlarmManager alarmManager;
    private HashMap<String, String> paymentMap;
    private String trackId;

    boolean isOtherPayment = false;
    private boolean isFirstPayment = true;

    private boolean isSettingKeepConnecting = false;

    private long testTime;
    
    //LOG 표시여부
    public static boolean TEST = false;

    /**
     * 개발중 앱이 죽을때 리포팅을 위하여 화면에 보여준다.
     */
    private void registerErrorHandlerForDebuging() {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Intent intent = new Intent(PayResultActivity.this, ErrorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                ex.printStackTrace();
                try {
                    sw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String errorMsg = sw.toString();
                intent.putExtra(ErrorActivity.EXTRA_ERROR_MESSAGE, errorMsg);
                startActivity(intent);

                System.exit(0);
            }
        });
    }
    private void w(String... s){
        if(TEST)LOG.w(s);
    }
    private void d(String... s){
        if(TEST)LOG.d(s);
    }
    private void i(String... s){
        if(TEST)LOG.i(s);
    }
    private void e(String... s){
        if(TEST)LOG.e(s);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        if(!getIntent().hasExtra("isOtherPayment")) {
//            setContentView(R.layout.activity_loading);
//        }

//        if(BuildConfig.IS_DEVEL)
//            registerErrorHandlerForDebuging();

        isSettingKeepConnecting = SharedPreferenceUtil.getData(this, Constants.KEY_KEEP_CONNECTION, "false").equals("true");
        testTime = System.currentTimeMillis();
        isOnCreateStart = true; //BT연결 재시도시 연결이벤트에서 무한호출 방지.
        isReceive = false;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        /*Activity 값 얻기 */

        w("BTJYP", "BT TEST @@@@ BT서비스시작 @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(BlueToothStatusChangeReceiver, makeGattUpdateIntentFilter());

        return_data = new Intent();

        encMSRManager = new EncMSRManager();
        searchBlue = new SearchBluetTooth(this, this);

        adminInfo = new AdminInfo((HashMap<String, byte[]>) getIntent().getSerializableExtra("AdminInfo_Hash"));
        paymentMap = (HashMap<String, String>) getIntent().getSerializableExtra("payment");
        trackId = getIntent().getStringExtra("trackId");

        isOtherPayment = getIntent().getBooleanExtra("isOtherPayment", false);

        receiveobj.adminInfo = adminInfo;

//        try {
//            Thread.sleep(500);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }




        //현금영수증 체크
        if ("K".equals(new String(adminInfo.getPosEntry())) && !"X".equals(adminInfo.getReceiptNo()) && adminInfo.getReceiptNo().length > 1) {
            _HashReaderData.put("trackII", adminInfo.getReceiptNo());
            _HashReaderData.put("EncryptInfo", "".getBytes());
            ThreadAdmission(_HashReaderData);
            return;
        }

        if (isOtherPayment) {
//            ThreadAdmission(_HashReaderData);
//            return;
        }

        //Van 결제. 가맹점별도 DPTID 사용시
        if (new String(adminInfo.getTelegramType()).equals("0420")) {
            isCardCancel = true;

            if (adminInfo == null) {
                responseobj.setResultCd("-1");
                responseobj.setResultMsg("결제정보가 없습니다.\n다시 시도해주세요");

                sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                return;
            }
            if (adminInfo.getAuthDate().length < 6) {
                responseobj.setResultCd("-1");
                responseobj.setResultMsg(getString(R.string.reader_iccard_cancel_length_fail));

                sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
            }
        }

        //서비스 바인딩 이후시작.
        //startBTPayment();
    }

    private void connectBLE() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            responseobj.setResultCd("-2");
            responseobj.setResultMsg(getString(R.string.phone_not_bluetooth));

            sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
        } else {
            if (!mBtAdapter.isEnabled()) {
                responseobj.setResultCd("-2");
                responseobj.setResultMsg(getString(R.string.phone_not_bluetooth));

                sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
            } else {
                sendHandlerMessage(MESSAGE_KSNET_READER_SEARCH, "", delaytime);

                responseobj.setProcessingCd("1000");

                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText(getString(R.string.reader_search));
                pDialog.setCancelable(false);
                pDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (isCancelable) {
                            isPressBackButton = true;
                            d("tag", "======================================== 취소 ========================================");
                            responseobj.setResultCd("-1");
                            responseobj.setResultMsg(getString(R.string.reader_iccard_key_backpress));
                            returnActivity(RESULT_CANCELED);
                        } else {
//                                Toast.makeText(PayResultActivity.this, "승인이 끝나고 취소하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });

                if (!TEST) pDialog.show();

//                   TimerCheck(20000);
            }
        }
    }


    private Handler mHandler = new Handler() {
        boolean isCardRead = false;

        @Override
        public void handleMessage(Message msg) {
            menuStatus = msg.what;
            w("menuStatus :" + menuStatus);

            String key = msg.getData().getString("key");
            HashMap<String, String> map = new HashMap<String, String>();

            if (key != null) {
                map.put(key, msg.getData().getString(key));
            }

            switch (msg.what) {
                case MESSAGE_ICCARD_APPROVE_COMPLETE: {
                    SweetDialog(getString(R.string.reader_iccard_approce_complete));
                    sendHandlerMessage(MESSAGE_RETURN_OK_ACTIVITY, "", 1500);
                }
                break;
                case MESSAGE_ICCARD_CANCEL_APPROVE_COMPLETE: {
                    SweetDialog(getString(R.string.reader_iccard_cancel_approce_complete));
                    sendHandlerMessage(MESSAGE_RETURN_OK_ACTIVITY, "", 1500);
                }
                break;
                /* 이곳에서 값을 전달하는 것을 해볼까요 */
                case MESSAGE_RETURN_OK_ACTIVITY: {
                    returnActivity(RESULT_OK);
                }
                break;
                /* 이곳에서 값을 전달하는 것을 해볼까요 */
                case MESSAGE_RETURN_NOK_ACTIVITY: {
                    returnActivity(RESULT_CANCELED);
                }
                break;

                case MESSAGE_KSNET_READER_SEARCH: {
                    /*  이곳에 프린터와 리더기를 찾습니다 */
                    if (isSettingKeepConnecting) {
                        //블루투스 유지설정시 연결시도 안함.
                        return;
                    }
                    String address = SharedPreferenceUtil.getData(PayResultActivity.this, Constants.KEY_MAC_ADDRESS, "NONE");
                    if (!address.equals("NONE")) {
                        if (mService != null) {
                            mService.connect(address);
                        } else {
                            responseobj.setResultCd("-2");
                            responseobj.setResultMsg(getString(R.string.btreader_not_search));
                            sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                        }
                    } else {
                        HashMap<String, String> bsearchmap = searchBlue.getBlutToothAddress("KSNET_BTIC");
                        if (bsearchmap.get("resultcd").equals("1")) {
                            BT_Reader = bsearchmap.get("deviceaddress");
                            if (BT_Reader != null) {
                                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(BT_Reader);
                                d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
                                if (mService != null) {
                                    mService.connect(BT_Reader);
                                } else {
                                    responseobj.setResultCd("-2");
                                    responseobj.setResultMsg(getString(R.string.btreader_not_search));
                                    sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                                }
                            }

                        } else {


                            if (!isFinishing()) {
                                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(PayResultActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("리더기 미등록!")
                                        .setContentText("리더기가 등록되지 않았습니다.\n등록하고 사용해주세요.")
                                        .setConfirmText("닫기")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                if(!isFinishing() && sDialog.isShowing()) {
                                                    try{sDialog.dismissWithAnimation();}catch (Exception e){e.printStackTrace();}
                                                }
                                                responseobj.setResultCd("-2");
                                                responseobj.setResultMsg(getString(R.string.btreader_not_search));

                                                sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", 0);
                                            }
                                        });
                                sweetAlertDialog.setCancelable(false);
                                try{sweetAlertDialog.show();}catch (Exception e){e.printStackTrace();}
                            }
                        }

                    }
                }
                break;
                case MESSAGE_ACTION_GATT_CONNECTED: {
                    if (pDialog != null) {
                        pDialog.setTitle("기기와 연결되었습니다.\n초기화를 시작합니다.");
                    }

                    isBatChk = true;

                    w("BTJYP", "BT TEST @@@@ 배터리체크 전송 @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);
                    isReceive = false;
                    if (mService != null) {
                        try {
                            mService.writeRXCharacteristic("#bat".getBytes());  // 밧데리를 측정하기 위하여 보냅니다
                        } catch (NullPointerException error) {
                            error.printStackTrace();
                        }
                        timeoutRetry();

                    } else {
                        responseobj.setResultCd("-2");
                        responseobj.setResultMsg(getString(R.string.btreader_not_search));
                        sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                    }

                }
                break;
                case MESSAGE_ACTION_GATT_DISCONNECTED: {
                }
                break;
                case MESSAGE_ACTION_GATT_SERVICES_DISCOVERED: {
                }
                break;
                case MESSAGE_ACTION_DATA_AVAILABLE: {   // 이곳은 블루투스 데이터가 오는 곳입니다
                }
                break;
                case MESSAGE_BAT: {
                    String data = msg.getData().getString("data");
                    int RemainBat = Integer.parseInt(new String(data));
                    String mesage = "";
                    if (RemainBat > 800)
                        mesage = "배터리가 충분합니다";
                    if (RemainBat > 700 && RemainBat <= 800)
                        mesage = "배터리가 보통입니다";
                    if (RemainBat <= 700) {
                        mesage = "배터리가 부족합니다";
                    }
                    w("BTJYP", "@@@@@@@@ 배터리체크 응답 @@@@@@@@@ " + mesage);

                    Toast.makeText(PayResultActivity.this, mesage, Toast.LENGTH_SHORT).show();
                    sendHandlerMessage(MESSAGE_DPT_C0, "", delaytime);
                }
                break;
                case MESSAGE_DPT_C0: {
                    w("BTJYP", "encMSRManager: " + encMSRManager + " mService: " + mService + " 동글정보요청!!!!!!!!!!!!!!!!");
                    if (encMSRManager != null) {
                        byte[] requestDongleInfo = encMSRManager.makeDongleInfo();
                        if (mService != null) {
                            mService.writeRXCharacteristic(requestDongleInfo);
                        } else {
                            responseobj.setResultCd("-2");
                            responseobj.setResultMsg(getString(R.string.btreader_not_search));
                            sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                        }
                    } else {   // 소스에 문제가 생긴것으로 Error 리턴합니다

                    }
                }
                break;
                case MESSAGE_DPT_D0: {
                    w("BTJYP", "BT TEST @@@@ 동글정보 응답 [D0] @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);
                    w("BTJYP", "BT TEST @@@@ 카드삽입 요청 [C6]-D6 @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);
                    //리더기 상태정보 응답부분
                    //여기서 카드삽입체크 후 카드번호요청으로 이동해야함.
                    //C6
                    isFirstPayment = true;


                    sendBTMessage(encMSRManager.makeRequestCardInOutStatus());

                    /** 카드체크안하고 바로 번호요청.

                     if(adminInfo.getReceiptNo().length==0 && "S".equals(new String(adminInfo.getPosEntry()))){
                     SweetDialog(getString(R.string.reader_card_read_msr));
                     }else {
                     SweetDialog(getString(R.string.reader_card_read_ic));
                     }
                     String readerName = KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA, 16);


                     //카드번호요청
                     byte[] s_data = encMSRManager.makeCardNumSendReq(new String(adminInfo.getTotalAmount()).substring(3, 12).getBytes(), "99".getBytes());
                     sendBTMessage(s_data);   // 블루투스 리더기에 데이터 쓰기


                     */

                    //     byte[] s_data = encMSRManager.makeIntegrityReq(getString(R.string.ksr03ReaderName).getBytes(),  (byte)75) ;   // K : 75 , S : 83
                    //     sendBTMessage(s_data);   // 블루투스 리더기에 데이터 쓰기

                    /*
                    byte[] buf = msg.getData().getByteArray("data");

                    if(KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA + 32, 1).equals("O")) {
                        addText("카드를 읽혀주세요");
                        SweetDialog(getString(R.string.reader_card_read));
                        responseobj.setProcessingCd("1001");

                        byte[] s_data = encMSRManager.makeCardNumSendReq(new String(adminInfo.getTotalAmount()).substring(3, 12).getBytes(), "99".getBytes());
                        sendBTMessage(s_data);   // 블루투스 리더기에 데이터 쓰기
                    } else {    // 소스에 문제가 생긴것으로 Error 리턴합니다
                        new AlertDialog.Builder(PayResultActivity.this)
                                .setTitle("안내")
                                .setMessage("무결성 점검에 실패하여 프로그램을 종료합니다")
                                .setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
//                                        System.exit(0);
                                    }
                                }).show();
                    }
                    */
                }
                break;
                case MESSAGE_DPT_C2: {
                }
                break;
                case MESSAGE_DPT_D2: {
                    byte[] buf = msg.getData().getByteArray("data");
                    String tradeCnt;
                    int encInfoLen;
                    int encCardNum16Len; //암호화된 카드번호 필드 길이
                    int noEncCardNumLen; //암호화하지 않은 카드번호 길이
                    int reqEMVDataLen;   //EMV 요청 Data 길이
                    int trackIILen;      //Track2 Data 길이


                    //AndroidUtils.byteToString(_recvData, IDX_DATA, 2)
                    String ICGubu = KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA, 2);

                    if (map.containsKey("CardNumSendResp")) {//카드번호 전송요청 응답
                        String FallBack_ErrCode = encMSRManager.chkFallBack(new String(map.get("CardNumSendResp")));

                        //정상응답
                        if (FallBack_ErrCode.equals("")) {   // 정상적인 응답인 경우
                            if (ICGubu.equals("IC") || ICGubu.equals("MS")) {

                                tradeCnt = KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA + "IC".length(), 3);  //카드거래 카운트
                                readerModelNum = KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA + 5, 16).getBytes(); //리더기모델번호

                                encInfoLen = ((int) KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA + 5 + 16, 1).getBytes()[0] + 1);

                                if (new String(adminInfo.getPlayType()).equals("D")) {//데몬일 경우 식별정보 출력
                                    addText("Deamon"); // 데몬
                                }
                                //암호화 정보생성
                                EncryptInfo = new byte[encInfoLen];
                                System.arraycopy(_recvData, KsnetParsingByte.IDX_DATA + 5 + 16, EncryptInfo, 0, EncryptInfo.length);
                                _HashReaderData.put("EncryptInfo", EncryptInfo);

                                //EMV DATA
                                encCardNum16Len = ((int) KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA + 5 + 16 + encInfoLen, 1).getBytes()[0] + 1);                     //암호화된 카드번호 16자리 길이
                                if (encCardNum16Len == 0)
                                    encCardNum16Len = 1;                                                                                     //암호화 미사용시 길이값이 0이 들어오므로 1바이트 별도 할당
                                noEncCardNumLen = ((int) KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA + 5 + 16 + encInfoLen + encCardNum16Len, 1).getBytes()[0] + 1);//암호화 하지 않은 카드번호 길이
                                int cardDataIdx = KsnetParsingByte.IDX_DATA + "IC".length() + tradeCnt.length() + readerModelNum.length + encInfoLen;                             //카드데이터 필드 인덱스

                                _HashReaderData.put("tradeCnt", tradeCnt.getBytes());
                                _HashReaderData.put("readerModelNum", readerModelNum);

                                if (ICGubu.equals("IC")) {
                                    if (new String(adminInfo.getReceiptNo()).equals(""))//현금영수증 카드 승인일 경우  park 이경우에도 넘겨 주어야 합니다
                                    {
//                                        clearTempBuffer();
//                                        Toast.makeText(PayResultActivity.this, "현금영수증 카드를 사용해주세요", Toast.LENGTH_LONG).show();
//                                        finish();
//                                        return;
                                        _HashReaderData.put("transType", "HK".getBytes());
                                        addText("현금영수증카드 IC승인이 진행중입니다.");
                                    } else {
                                        _HashReaderData.put("transType", "IC".getBytes());
                                        addText("IC승인이 진행중입니다.");
                                    }

                                    if (!isCardCancel) {
                                        if (new String(adminInfo.getReceiptNo()).equals("")) {
                                            SweetDialog(getString(R.string.reader_iccard_cash));
                                        } else {
                                            SweetDialog(getString(R.string.reader_iccard_approce));
                                        }
                                    }

                                    responseobj.setProcessingCd("1003");

                                    reqEMVDataLen = _recvDataLength - (cardDataIdx + encCardNum16Len + noEncCardNumLen) - 2;         //EMV DATA 길이

                                    Cardbin = new byte[noEncCardNumLen - 2];
                                    System.arraycopy(_recvData, cardDataIdx + encCardNum16Len + 2, Cardbin, 0, Cardbin.length);

                                    _HashReaderData.put("Cardbin", Cardbin);

                                    addText("ICardbin" + Cardbin.length);
                                    addText("ICardbin" + new String(Cardbin));
                                    w("=========================================================");
                                    w(AndroidUtils.printHex(Cardbin));
                                    w("=========================================================");

                                    //수신 EMV데이터로 EMV 요청전문 생성
                                    EMVData = new byte[reqEMVDataLen];
                                    System.arraycopy(_recvData, cardDataIdx + encCardNum16Len + noEncCardNumLen, EMVData, 0, EMVData.length);
                                    reqEMVData = new byte[EMVData.length + 4];
                                    String telegramLength = String.format("%04d", EMVData.length);
                                    System.arraycopy(telegramLength.getBytes(), 0, reqEMVData, 0, 4);
                                    System.arraycopy(EMVData, 0, reqEMVData, 4, EMVData.length);

                                    _HashReaderData.put("reqEMVData", reqEMVData);

//                                    trackIILen = ((int) KsnetUtils.byteToString(_recvData, IDX_DATA + 5 + 16 + encInfoLen + encCardNum16Len + noEncCardNumLen, 1).getBytes()[0] + 1);
//
//                                    trackII = new byte[trackIILen + 1];
//                                    System.arraycopy(_recvData, IDX_DATA + 5 + 16 + encInfoLen + encCardNum16Len + noEncCardNumLen, trackII, 0, trackII.length);
//                                    String[] arrTrackII = new String(trackII).split("=");
//                                    _HashReaderData.put("trackII", trackII);

                                    _HashReaderData.put("trackII", " ".getBytes());

                                    //50000만원 이하 무서명 거래
                                    if (Long.parseLong(new String(adminInfo.getTotalAmount())) >= 50000) {
                                        Intent i = new Intent(PayResultActivity.this, PayCreditSign.class);  // card view
                                        i.putExtra("amount", new String(adminInfo.getTotalAmount()));
                                        startActivityForResult(i, ACTIVITY_MENU_GET_SIGN);
                                    } else {
                                        ThreadAdmission(_HashReaderData);
                                    }
                                }  // IC CARD
                                //IC우선거래가 아닌 일반 MS 거래시 거래진행

                                if (ICGubu.equals("MS")) {
//                                    w("tag data","recvData: "+new String(_recvData));
//                                    w("tag data","recvData: "+new String(KsnetUtils.byteToString(_recvData, IDX_DATA + 5 + 16 + encInfoLen + encCardNum16Len, noEncCardNumLen-1)));
//                                    w("tag data","receiptNo: "+new String(adminInfo.getReceiptNo()) +" length: "+adminInfo.getReceiptNo().length+" value: "+adminInfo.getReceiptNo());

                                    if (!isFallback &&
                                            (adminInfo.getReceiptNo().length != 0 && KsnetUtils.byteToString(_recvData, IDX_DATA + 5 + 16 + encInfoLen + encCardNum16Len + noEncCardNumLen - 1, 1).equals("2") ||
                                                    KsnetUtils.byteToString(_recvData, IDX_DATA + 5 + 16 + encInfoLen + encCardNum16Len + noEncCardNumLen - 1, 1).equals("6"))) {

                                        SweetDialog(getString(R.string.reader_card_read_ic));

                                        isFirstCardNunResq = true;
                                        isICFirstAct = true;

                                        clearTerminalBuffer();

                                    } else {

                                        SweetDialog("MS카드 승인이 진행중입니다");
                                        _HashReaderData.put("transType", "MS".getBytes());

                                        if (new String(adminInfo.getReceiptNo()).equals("X")) {
                                            SweetDialog("MS카드 승인이 진행중입니다");
                                            _HashReaderData.put("transType", "MS".getBytes());
                                        } else {
                                            SweetDialog("현금영수증 승인이 진행중입니다");
                                            _HashReaderData.put("transType", "HK".getBytes());
                                        }

                                    }

                                    if (!isICFirstAct) {
                                        trackIILen = ((int) KsnetUtils.byteToString(_recvData, IDX_DATA + 5 + 16 + encInfoLen + encCardNum16Len + noEncCardNumLen, 1).getBytes()[0] + 1);

                                        trackII = new byte[trackIILen + 1];
                                        System.arraycopy(_recvData, IDX_DATA + 5 + 16 + encInfoLen + encCardNum16Len + noEncCardNumLen, trackII, 0, trackII.length);
                                        String[] arrTrackII = new String(trackII).split("=");
                                        _HashReaderData.put("trackII", trackII);

                                        if (new String(_HashReaderData.get("transType")).equals("HK") || new String(_HashReaderData.get("transType")).equals("PC")) {
                                            Cardbin = new byte[noEncCardNumLen - 3];
                                            //Cardbin = Utils.byteToSubByte(_recvData, cardDataIdx + encCardNum16Len + 2, noEncCardNumLen - 3);
                                            System.arraycopy(_recvData, cardDataIdx + encCardNum16Len + 2, Cardbin, 0, Cardbin.length);
                                        } else {
                                            if (noEncCardNumLen < 4) {
                                                SweetDialog(getString(R.string.reader_card_read_fail2));

                                                clearTerminalBuffer();
                                                responseobj.setResultCd("-1");
                                                responseobj.setResultMsg(getString(R.string.reader_card_read_fail2));
                                                returnActivity(RESULT_CANCELED);
                                                return;
                                            }
                                            Cardbin = new byte[noEncCardNumLen - 4];
                                            System.arraycopy(_recvData, cardDataIdx + encCardNum16Len + 2, Cardbin, 0, Cardbin.length);
                                        }

                                        _HashReaderData.put("Cardbin", Cardbin);
                                        _HashReaderData.put("reqEMVData", reqEMVData);

                                        addText("=========================================================");
                                        addText("ICardbin" + Cardbin.length + " : " + new String(Cardbin) + " : " + AndroidUtils.printHex(Cardbin));
                                        addText("=========================================================");


                                        //50000만원 이하 무서명 거래
                                        isICFirstAct = false;

                                        if (Long.parseLong(new String(adminInfo.getTotalAmount())) >= 50000) {
                                            Intent i = new Intent(PayResultActivity.this, PayCreditSign.class);  // card view
                                            //	Intent i = new Intent(activity, PayCreditSignCyrexPay.class);  // card view

                                            i.putExtra("amount", new String(adminInfo.getTotalAmount()));
                                            startActivityForResult(i, ACTIVITY_MENU_GET_SIGN);
                                        } else {
                                            ThreadAdmission(_HashReaderData);
                                        }
                                    }
                                }

                            }
                        }  //if (FallBack_ErrCode.equals("")) {
                        else {
                            if (FallBack_ErrCode.equals("01") || FallBack_ErrCode.equals("02") || FallBack_ErrCode.equals("03") || FallBack_ErrCode.equals("04") ||
                                    FallBack_ErrCode.equals("05") || FallBack_ErrCode.equals("06") || FallBack_ErrCode.equals("07")) {

                                isFallback = true;

                                String FallBack_Error_Msg = getString(R.string.reader_iccard_fbprocess);

                                if (FallBack_ErrCode.equals("01")) {
                                    FallBack_Error_Msg = "Chip 미 응답";
                                } else if (FallBack_ErrCode.equals("02")) {
                                    FallBack_Error_Msg = "Application 미 존재";
                                } else if (FallBack_ErrCode.equals("03")) {
                                    FallBack_Error_Msg = "Chip 데이터 읽기 실패";
                                } else if (FallBack_ErrCode.equals("04")) {
                                    FallBack_Error_Msg = "Mandatory 데이터 미 포함";
                                } else if (FallBack_ErrCode.equals("05")) {
                                    FallBack_Error_Msg = "CVM 커맨드 응답실패";
                                } else if (FallBack_ErrCode.equals("06")) {
                                    FallBack_Error_Msg = "EMV 커맨드 오 설정";
                                } else if (FallBack_ErrCode.equals("07")) {
                                    FallBack_Error_Msg = "터미널(리더기) 오 동작";
                                }

                                clearTempBuffer();

                                addText("FB거래를 해주시기 바랍니다");
                                SweetDialog(getString(R.string.reader_iccard_fbprocess) + "\n(" + FallBack_Error_Msg + ")");
                                e("FB Error: " + FallBack_Error_Msg);


                                byte[] s_data = encMSRManager.makeFallBackCardReq(ICGubu, "99");
                                w("[FB요청]:: " + new BigInteger(s_data).toString(16));
                                sendBTMessage(s_data);


                            } else //비정상 Fallback 처리부
                            {
                                addText("[에러]\n" + FallBack_ErrCode);
                                SweetDialog("[에러]\n" + FallBack_ErrCode);
                                clearTempBuffer();

                                responseobj.setResultCd("7770");
                                responseobj.setProcessingCd("7770");
                                responseobj.resultMsg = new String(KsnetUtils.byteToMString(_responseTelegram, 62, 16));

                                sendHandlerMessage(MESSAGE_COMPLETE, new String(KsnetUtils.byteToMString(_responseTelegram, 62, 16)), delaytime);


                            }
                            //     sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                        }
                    }
                }


                break;
                case MESSAGE_DPT_C3: {
                }
                break;
                case MESSAGE_DPT_D3: {
                }
                break;
                case MESSAGE_DPT_C4: {
                }
                break;
                case MESSAGE_DPT_D4: {
                    byte[] buf = msg.getData().getByteArray("data");

                    if (!KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA + 32, 1).equals("O")) {
                        addText("카드를 읽혀주세요");
                        SweetDialog(getString(R.string.reader_card_read));
                        responseobj.setProcessingCd("1001");

                        byte[] s_data = encMSRManager.makeCardNumSendReq(new String(adminInfo.getTotalAmount()).substring(3, 12).getBytes(), "99".getBytes());
                        sendBTMessage(s_data);   // 블루투스 리더기에 데이터 쓰기
                    } else {    // 소스에 문제가 생긴것으로 Error 리턴합니다
                        new AlertDialog.Builder(PayResultActivity.this)
                                .setTitle("안내")
                                .setMessage("무결성 점검에 실패하여 프로그램을 종료합니다")
                                .setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        responseobj.setResultCd("-1");
                                        responseobj.setResultMsg(getString(R.string.reader_iccard_instantment_fail));
                                        returnActivity(RESULT_CANCELED);

                                        // TODO Auto-generated method stub
//                                        System.exit(0);
                                        /* 결과 값을 다시 리턴 합니다 PARK */
                                        /* 망취소에 대하여 속성값으로 합시다 */
                                    }
                                }).show();
                    }
                }
                break;
                case MESSAGE_DPT_C5: {
                }
                break;
                case MESSAGE_DPT_D5: {
                    String tradeCnt;
                    int encInfoLen;
                    int encCardNum16Len; //암호화된 카드번호 필드 길이
                    int noEncCardNumLen; //암호화하지 않은 카드번호 길이
                    int reqEMVDataLen;   //EMV 요청 Data 길이
                    int trackIILen;      //Track2 Data 길이

                    if (map.containsKey("FallBackCardResp") && ((String) map.get("FallBackCardResp")).equals("FB")) {
                        tradeCnt = Utils.byteToString(_recvData, "FB".length() + 4, 3);
                        readerModelNum = Utils.byteToString(_recvData, 9, 16).getBytes();
                        encInfoLen = Utils.byteToString(_recvData, 25, 1).getBytes()[0] + 1;
                        EncryptInfo = new byte[encInfoLen];
                        System.arraycopy(_recvData, 25, EncryptInfo, 0, EncryptInfo.length);
                        _HashReaderData.put("EncryptInfo", EncryptInfo);
                        encCardNum16Len = Utils.byteToString(_recvData, encInfoLen + 25, 1).getBytes()[0] + 1;
                        noEncCardNumLen = Utils.byteToString(_recvData, encInfoLen + 25 + encCardNum16Len, 1).getBytes()[0] + 1;
                        int cardDataIdx2 = "IC".length() + 4 + tradeCnt.length() + readerModelNum.length + encInfoLen;
                        Cardbin = new byte[(noEncCardNumLen - 2)];
                        System.arraycopy(_recvData, encCardNum16Len + cardDataIdx2 + 2, Cardbin, 0, Cardbin.length);
                        _HashReaderData.put("Cardbin", Cardbin);
                        SweetDialog("MSR 승인을 진행중입니다");
                        reqEMVDataLen = (_recvDataLength - ((encCardNum16Len + cardDataIdx2) + noEncCardNumLen)) - 2;
                        EMVData = new byte[reqEMVDataLen];
                        System.arraycopy(_recvData, encCardNum16Len + cardDataIdx2 + noEncCardNumLen, EMVData, 0, EMVData.length);
                        reqEMVData = new byte[(EMVData.length + 4)];
                        System.arraycopy(String.format("%04d", new Object[]{Integer.valueOf(EMVData.length)}).getBytes(), 0, reqEMVData, 0, 4);
                        System.arraycopy(EMVData, 0, reqEMVData, 4, EMVData.length);
                        _HashReaderData.put("tradeCnt", tradeCnt.getBytes());
                        _HashReaderData.put("transType", "FB".getBytes());
                        _HashReaderData.put("reqEMVData", reqEMVData);
                        _HashReaderData.put("readerModelNum", readerModelNum);
                        if (!isCardRead) {
                            isCardRead = true;
                            if (Long.parseLong(new String(adminInfo.getTotalAmount())) <= 50000) {
                                ThreadAdmission(_HashReaderData);
                            } else {
                                Intent intent5 = new Intent(PayResultActivity.this, PayCreditSign.class);
                                intent5.putExtra("amount", new String(adminInfo.getTotalAmount()));
                                startActivityForResult(intent5, ACTIVITY_MENU_GET_SIGN);

//                                intent5.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                intent5.putExtra("AdminInfo_Amount", new String(adminInfo.getTotalAmount()));
//                                startActivityForResult(intent5, 0);
                            }
                        }
                    }
                }
                break;
                case MESSAGE_DPT_C6: {


                }
                break;
                case MESSAGE_DPT_D6: {   // card insert
                    w("BTJYP", "BT TEST @@@@ 카드삽입/제거 C6->[D6] @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);

                    w("BTJYP", "BT TEST D6 카드 삽입해제 여부: " + KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA, 3));

                    if (isFirstPayment) {
                        //삽입되어있는 상태라면 INS, 카드가 없는상태라면 DEL로 전달됨.
                        //따라서, INS 일경우 에러리턴처리, DEL 일경우 카드삽입요청 후 정상진행

                        byte[] buf = msg.getData().getByteArray("data");
                        if ((KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA, 3)).equals("INS")) {

                            //카드가 삽입되어있으므로 에러리턴.
                            clearTempBuffer();
                            Toast.makeText(PayResultActivity.this, "카드를 제거하고 다시 시도해주세요.", Toast.LENGTH_LONG).show();

                            SweetDialog("카드를 제거하고 다시 시도해주세요.");

                            responseobj.setResultCd("-3");
                            responseobj.setResultMsg("카드를 제거하고 다시 시도해주세요.");


                            sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", 1000);//delaytime);

                        } else if ((KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA, 3)).equals("DEL")) {
                            isFirstPayment = false;

                            if (adminInfo.getReceiptNo().length == 0 && "S".equals(new String(adminInfo.getPosEntry()))) {
                                SweetDialog(getString(R.string.reader_card_read_msr));
                            } else {
                                SweetDialog(getString(R.string.reader_card_read_ic));
                            }
                            String readerName = KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA, 16);


                            //카드번호요청
                            byte[] s_data = encMSRManager.makeCardNumSendReq(new String(adminInfo.getTotalAmount()).substring(3, 12).getBytes(), "99".getBytes());
                            sendBTMessage(s_data);   // 블루투스 리더기에 데이터 쓰기
                        }
                    } else {
                        //카드체크후 정상진행
                        byte[] buf = msg.getData().getByteArray("data");
                        if ((KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA, 3)).equals("INS")) {
                            byte[] s_data = encMSRManager.makeCardNumSendReq(new String(adminInfo.getTotalAmount()).substring(3, 12).getBytes(), "10".getBytes());
                            sendBTMessage(s_data);
                            addText("카드를 읽는 중입니다");
                            SweetDialog(getString(R.string.reader_card_reading));
                            responseobj.setProcessingCd("1002");

                        } else if ((KsnetUtils.byteToString(_recvData, KsnetParsingByte.IDX_DATA, 3)).equals("DEL")) {
                            if (isCancelable && !isFallback) {
                                clearTempBuffer();
                                Toast.makeText(PayResultActivity.this, "카드가 제거되어 승인을 종료합니다", Toast.LENGTH_LONG).show();

                                SweetDialog(getString(R.string.reader_iccard_reject_end));

                                responseobj.setResultCd("-3");
                                responseobj.setResultMsg(getString(R.string.reader_iccard_reject_end));

                                sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                            }
                            //여기서 망 취소를 던지면 됩니다
                        }


                    }

                }
                break;

                case MESSAGE_COMPLETE: {
                    String data = msg.getData().getString("data");
                    rejectKsnetApprove(data);
                }
                break;

            }  //switch (msg.what)
        } //handleMessage
    }; // mHandler

    private void timeoutRetry() {
        //타임아웃 체크시작.
        retryHandler.removeCallbacks(retryRunnable);
        retryHandler.postDelayed(retryRunnable, 1000);
    }


    public void ThreadAdmission(HashMap<String, byte[]> map) {
        if (isPressBackButton) return;

        isCancelable = false;

        _HashReaderData = map;

        if (_HashReaderData.get("readerModelNum") != null)
            ReaderModelName = new String(_HashReaderData.get("readerModelNum"));
//        mApp.setReaderModelName(new String(_HashReaderData.get("readerModelNum"))); //실행시 마다 필요 리더기 모델번호 재저장

        //리더기 카드데이터 수신 대기
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!new String(adminInfo.getPosEntry()).equals("K")) {
            switch (new String(_HashReaderData.get("transType"))) {
                case "IC":
                case "FB":
                    adminInfo.setTransType("IC".getBytes());
                    break;
                case "MS":
                    adminInfo.setTransType("MS".getBytes());
                    break;
                case "HK":
                    adminInfo.setTransType("HK".getBytes());
                    break;
                case "PC":
                    adminInfo.setTransType("PC".getBytes());
                    break;
            }
        } else {
            adminInfo.setTransType("HK".getBytes());
            _HashReaderData.put("transType", adminInfo.getTransType());
        }

        if (new String(adminInfo.getSignTrans()).equals("S"))
            bEncSign = _HashReaderData.get("encrptStrSign");

        //취소전문일 경우
        if (new String(adminInfo.getTelegramType()).equals("0420")) {
            bAdminNum = adminInfo.getAuthNum();
            w(":::::PaymentDlg : bAdminNum" + new String(bAdminNum));
            bAdmindate = KsnetUtils.byteToSubByte(adminInfo.getAuthDate(), 0, 6);

        }
        //망취소전문일 경우
        if (isNetCancel) {
            if (new String(_HashReaderData.get("TelegramType")).equals("0460")) {
                bAdminNum = _HashReaderData.get("AuthNum");
                bAdmindate = KsnetUtils.byteToSubByte(_HashReaderData.get("Authdate"), 0, 6);
                adminInfo.setTelegramType(_HashReaderData.get("TelegramType"));
            }
        }
        final String phone = AndroidUtils.getPhoneNumber(PayResultActivity.this);
        //검증용으로 승인전문 Filler 필드에 휴대폰 번호 | 실행타입을 추가한다
        // 직원ID | 휴대폰번호 | ...형

        String StrFiller;// = phone + "|" + new String(adminInfo.getPlayType()) + "|" + new String(adminInfo.getFiller());
//        StrFiller = "SUBMALL"+SharedPreferenceUtil.getData(this,"identity");
        StrFiller = "";
        adminInfo.addFiller(StrFiller.getBytes());

        _TelegramNo = KsnetUtils.generateString(12).getBytes();

//      _requestTelegram  = encMSRManager.makeRequestTelegram(adminInfo.getTransType(), adminInfo.getTelegramType(),adminInfo.getWorkType(), _TelegramNo, adminInfo.getPosEntry(), bAdminNum, bAdmindate, adminInfo.getDPTID(), _HashReaderData.get("readerModelNum"), getString(R.string.SWModelName).getBytes(), adminInfo.getPayType(), adminInfo.getTotalAmount(), adminInfo.getAmount(), adminInfo.getServicAmount(), adminInfo.getTaxAmount(), adminInfo.getFreeAmount(), adminInfo.getFiller(), adminInfo.getSignTrans(), bEncSign, _HashReaderData.get("EncryptInfo"), _HashReaderData.get("reqEMVData"),  _HashReaderData.get("trackII"));
//      _requestTelegram  = encMSRManager.makeRequestTelegram(adminInfo.getTransType(), adminInfo.getTelegramType(),adminInfo.getWorkType(), _TelegramNo, adminInfo.getPosEntry(), bAdminNum, bAdmindate, adminInfo.getDPTID(),  getString(R.string.ksr03ReaderName).getBytes(), getString(R.string.SWModelName).getBytes(), adminInfo.getPayType(), adminInfo.getTotalAmount(), adminInfo.getAmount(), adminInfo.getServicAmount(), adminInfo.getTaxAmount(), adminInfo.getFreeAmount(), adminInfo.getFiller(), adminInfo.getSignTrans(), bEncSign, _HashReaderData.get("EncryptInfo"), _HashReaderData.get("reqEMVData"),  _HashReaderData.get("trackII"));

        _requestTelegram = encMSRManager.makeRequestTelegram(adminInfo.getTransType(),
                adminInfo.getTelegramType(),
                adminInfo.getWorkType(),
                _TelegramNo,
                adminInfo.getPosEntry(),
                bAdminNum,
                bAdmindate,
                adminInfo.getDPTID(),
                getString(R.string.SWModelName).getBytes(),
                getString(R.string.ksr03ReaderName).getBytes(),
                adminInfo.getPayType(),
                adminInfo.getTotalAmount(),
                adminInfo.getAmount(),
                adminInfo.getServicAmount(),
                adminInfo.getTaxAmount(),
                adminInfo.getFreeAmount(),
                adminInfo.getFiller(),
                adminInfo.getSignTrans(),
                bEncSign,
                _HashReaderData.get("EncryptInfo"), //
                _HashReaderData.get("reqEMVData"),
                _HashReaderData.get("trackII"));

        w(new String(_requestTelegram));
        //  AndroidUtils.printHex(_requestTelegram);
        if (isPressBackButton) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isPressBackButton) return;

                Approval approval = new Approval();
                Configuration config = getResources().getConfiguration();
                String[] vs = KsnetUtils.getTransaction(config); //  vs[2] : IP   vs[3] : Port
//vs[2] = "210.181.28.116";  210.181.28.137   "210.181.28.116"
//vs[3] = "9582"; //9562 , 9582
                //getString(R.string.ksnet_server_ip),
                final int rtn = approval.request(adminInfo.getKsnetServerIp(),
                        Integer.parseInt(adminInfo.getKsnetServerPort()),
                        Integer.parseInt(adminInfo.getKsnetTelegrameType()),
                        _requestTelegram,
                        _responseTelegram,
                        Integer.parseInt(adminInfo.getKsnetTimeout())); //실제 사용코드

/*
4. 테스트 정보
- 테스트 IP      : 210.181.28.116
- 테스트 PORT   : 9562
- 테스트 단말ID  : DPT0TEST03 (*영문은 대문자 입력 요망)
- 신용승인 금액  : 5000원이하(1004원 권장)
- 현금영수증 금액: 700원 7000원 17000원 27000원
*/

                responseobj.setMap("ksnet_request_return_code", "" + rtn);
                responseobj.setMap("ksnet_request_return_code_msg", "" + KsnetUtils.getApproveRequestReturnCode(rtn));
                KsnetUtils.reqDataPrint(_requestTelegram);
                d(KsnetUtils.respGetHashData(_responseTelegram) + "");
                //   KsnetUtils.respGetHashData(_responseTelegram)
                if (rtn >= 0) {
                    if (KsnetUtils.byteToString(_responseTelegram, 40, 1).equals("O")) {
                        //2nd 실패 망취소 성공
                        if (isNetCancel) {
                            clearTempBuffer();
                            finish();
                        }
                        //2nd Generation 처리 ,  IC카드일때만 처리
                        if (new String(_HashReaderData.get("transType")).equals("IC")) {
                            int tempRespTgLen = Integer.parseInt(KsnetUtils.byteToString(_responseTelegram, 456, 4));
                            byte[] respEMVData = new byte[tempRespTgLen];
                            System.arraycopy(_responseTelegram, 456 + 4, respEMVData, 0, respEMVData.length);
                            final byte[] req2thGenerate = encMSRManager.make2ThGenerateReq(adminInfo.getTotalAmount(), "00".getBytes(), _HashReaderData.get("tradeCnt"), respEMVData);

                            sendBTMessage(req2thGenerate);
                            /*
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendBTMessage(req2thGenerate);
                                            clearTempBuffer();
                                        }
                                    });
                                }
                            }).start();
                            */
                        }
                        _AuthDate = KsnetUtils.byteToSubByte(_responseTelegram, 49, 12);
                        _AuthNum = KsnetUtils.byteToSubByte(_responseTelegram, 94, 12);
                        _vanTrxId = KsnetUtils.byteToSubByte(_responseTelegram, 106, 20);

                        _TelegramNo = KsnetUtils.byteToSubByte(_responseTelegram, 28, 12);
                        w("_AuthDate : " + new String(_AuthDate) + "/" + "==================================================");
                        w("_AuthNum : " + new String(_AuthNum) + "/" + "==================================================");
                        w("_vanTrxId : " + new String(_vanTrxId) + "/" + "==================================================");
                        w("_TelegramNo : " + new String(_TelegramNo) + "/" + "==================================================");

                        //addText("승인이 정상적으로 완료되었습니다");

                        responseobj.resultCd = "1";
                        responseobj.setDataMap(GsonUtil.toJson(KsnetUtils.respGetHashData(_responseTelegram)));
                        try {
                            int amount = Integer.parseInt(new String(adminInfo.getTotalAmount()));
                            responseobj.setMap("TotalAmount", String.valueOf(amount));
                        } catch (Exception e) {

                            responseobj.setMap("TotalAmount", new String(adminInfo.getTotalAmount()));
                        }

                        if (!new String(_HashReaderData.get("transType")).equals("HK")) {
                            responseobj.setMap("CardNo", new String(Cardbin));
                        } else if (new String(adminInfo.getPosEntry()).equals("K")) {
                            //todo keyin 개발필요로 카드번호에 입력한값 전달.
                            responseobj.setMap("CardNo", new String(adminInfo.getReceiptNo()));
                        } else {

                        }

                        if (!isCardCancel) {

                            responseobj.resultCd = "1";
                            if (adminInfo.getReceiptNo() != "".getBytes()) {
                                responseobj.resultMsg = "승인이 정상적으로 완료되었습니다";
                            } else {
                                responseobj.resultMsg = "현금영수증 승인이 정상적으로 완료되었습니다";
                            }
                            responseobj.setProcessingCd("1004");
                            sendHandlerMessage(MESSAGE_ICCARD_APPROVE_COMPLETE, "", delaytime);
                        } else {
                            responseobj.resultCd = "1";
                            responseobj.resultMsg = "승인취소가 정상적으로 완료되었습니다";
                            responseobj.setProcessingCd("1005");
                            sendHandlerMessage(MESSAGE_ICCARD_CANCEL_APPROVE_COMPLETE, "", delaytime);
                        }


                        approveFinish();

                        runOnUiThread(() -> {
                            realm.executeTransaction(realm -> {
                                PaymentInfo paymentInfo = realm.createObject(PaymentInfo.class);
//                        tmnId;
//                        serial;
//                        appId;
//                        mchtId;
//                        version;
//                        telNo;

                                paymentInfo.delngSe = new String(adminInfo.getTelegramType()).equals("0420") ? "승인취소" : "승인";
                                paymentInfo.cardCashSe = "CARD";
                                paymentInfo.cardNo = responseobj.data.get("CardNo").trim();
                                paymentInfo.instlmtMonth = new String(adminInfo.getPayType()).trim();
                                paymentInfo.splpc = responseobj.data.get("TotalAmount").trim();
                                paymentInfo.confmNo = responseobj.data.get("AuthNum").trim();
                                paymentInfo.regDate = responseobj.data.get("Authdate").trim();
                                paymentInfo.issuCmpnyCode = responseobj.data.get("IssueCode").trim();
                                paymentInfo.puchasCmpnyCode = responseobj.data.get("PurchaseCode").trim();
                                paymentInfo.issuCmpnyNm = responseobj.data.get("CardName").trim();
                                paymentInfo.puchasCmpnyNm = responseobj.data.get("PurchaseName").trim();
                                paymentInfo.setleMssage = responseobj.data.get("notice1").trim();


                                if (paymentMap != null) {
                                    paymentInfo.trackId = paymentMap.get("trackId");
                                    paymentInfo.mchtName = paymentMap.get("name");
                                    paymentInfo.mchtAddr = paymentMap.get("addr");
                                    paymentInfo.mchtBizNum = paymentMap.get("identity");
                                }
                            });
                        });


                        d("CompleteTest", "====================== 결제완료 ======================");

                        if (paymentMap != null && paymentMap.size() > 0 && !isOtherPayment) {
                            Intent completeCheckIntent = new Intent(PayResultActivity.this, CompleteCheckReceiver.class);
                            completeCheckIntent.putExtra("payment", paymentMap);
                            completeCheckIntent.putExtra("responseData", responseobj.data);
                            completeCheckIntent.putExtra("van", paymentMap.get("van"));
                            completeCheckIntent.putExtra("vanId", paymentMap.get("vanId"));
                            completeCheckIntent.putExtra("vanTrxId", paymentMap.get("trackId"));
                            completeCheckIntent.putExtra("amount", responseobj.data.get("TotalAmount"));
                            completeCheckIntent.putExtra("regDate", responseobj.data.get("Authdate"));
                            completeCheckIntent.putExtra("authCd", responseobj.data.get("AuthNum"));
                            completeCheckIntent.putExtra("trackId", trackId);
                            if (paymentMap.get("trxId") != null)
                                completeCheckIntent.putExtra("trxId", paymentMap.get("trxId"));

                            if (new String(adminInfo.getTelegramType()).equals("0420")) {
                                completeCheckIntent.putExtra("type", "승인취소");
                            } else {
                                completeCheckIntent.putExtra("type", "승인");
                            }

                            completeCheckIntent.putExtra("number", responseobj.data.get("CardNo"));
                            completeCheckIntent.putExtra("installment", paymentMap.get("installment"));

                            completeCheckIntent.putExtra("prodQty", "1");
                            completeCheckIntent.putExtra("prodDesc", paymentMap.get("payerAddr"));
                            completeCheckIntent.putExtra("prodName", paymentMap.get("prodName"));
                            try {
                                completeCheckIntent.putExtra("prodPrice", String.valueOf(Long.parseLong(responseobj.data.get("TotalAmount"))));
                            } catch (Exception e) {
                                completeCheckIntent.putExtra("prodPrice", responseobj.data.get("TotalAmount"));
                            }
                            completeCheckIntent.putExtra("payerName", paymentMap.get("payerName"));
                            completeCheckIntent.putExtra("payerEmail", paymentMap.get("payerEmail"));
                            completeCheckIntent.putExtra("payerTel", paymentMap.get("payerTel"));


                            long currentTime = System.currentTimeMillis();

                            completeCheckIntent.putExtra("alarmTime", currentTime);

                            // 알람셋팅
                            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + 30000,
                                    PendingIntent.getBroadcast(PayResultActivity.this, (int) System.currentTimeMillis(), completeCheckIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT));

                            d("CompleteTest", "====================== 알람설정 ======================");
                        }


                    } else if (KsnetUtils.byteToString(_responseTelegram, 40, 1).equals("X")) {
                        //SweetDialog( KsnetUtils.byteToMString(_responseTelegram, 62, 16) );
                        //rejectKsnetApprove(KsnetUtils.byteToMString(_responseTelegram, 62, 16) );
                        sendHandlerMessage(MESSAGE_COMPLETE, new String(KsnetUtils.byteToMString(_responseTelegram, 62, 16)), delaytime);
                    }
                } else {//망취소 포함 예외 상황처리
                    sendHandlerMessage(MESSAGE_COMPLETE, new String(KsnetUtils.byteToMString(_responseTelegram, 62, 16)), delaytime);
                }
            }
        }).start();
    }

    private void approveFinish() {
        if (mService != null) {
            try {
                i(TAG, "--- ON DESTROY ---");
//                if (mBound) {
                w("@@@@@@@ unbind Service @@@@@@@@");
                unbindService(mServiceConnection);
//                }

            } catch (Exception e) {
            }

            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(BlueToothStatusChangeReceiver);
            } catch (Exception ignore) {
                e(TAG, ignore.toString());
            }

            try {
                if (!isSettingKeepConnecting && mService != null) {
                    mService.close();
                    mService.stopSelf();
                    mService = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void rejectKsnetApprove(String msg) {
        if (pDialog.isShowing()) pDialog.dismiss();

        responseobj.setResultCd("2000");
        responseobj.setResultMsg("에러 : " + KsnetUtils.byteToMString(_responseTelegram, 62, 16));
        responseobj.setDataMap(GsonUtil.toJson(KsnetUtils.respGetHashData(_responseTelegram)));
        sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);

//        new SweetAlertDialog(PayResultActivity.this, SweetAlertDialog.WARNING_TYPE)
//                .setContentText(msg)
//                .setConfirmText("확인")
//                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                    @Override
//                    public void onClick(SweetAlertDialog sDialog) {
//                        sDialog.dismissWithAnimation();
//                        responseobj.setResultCd("2000");
//                        responseobj.setResultMsg(KsnetUtils.byteToMString(_responseTelegram, 62, 16));
//                        responseobj.setDataMap(GsonUtil.toJson(KsnetUtils.respGetHashData(_responseTelegram)));
//                        sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
//                    }
//                });
    }

    public void returnActivity(int result_code) {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }

        if (!new String(adminInfo.getReceiptNo()).equals("X")) {
            return_data.putExtra("cashReceipt", "true");
        }
        if (isOtherPayment) {
            return_data.putExtra("isOtherPayment", String.valueOf(isOtherPayment));
        }
        return_data.putExtra("resultCd", responseobj.resultCd);
        return_data.putExtra("resultMsg", responseobj.resultMsg);
        return_data.putExtra("resultData", responseobj.toJsonString());
        return_data.putExtra("paymentMap",paymentMap);

        setResult(result_code, return_data);
        finish();

    }


    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            w("BTJYP", "BT TEST ServiceConnection: Connected ");
            mService = ((UartService.LocalBinder) rawBinder).getService();
            if (!mService.initialize()) {
                e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            mBound = true;

            if(isOnCreateStart) {
                startBTPayment();
                isOnCreateStart = false;
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            w("BTJYP", "BT TEST ServiceConnection: Disconnected ");
//            if (mService != null) {
//                mService.disconnect();
//                mService = null;
//            }
            mBound = false;
        }
    };

    private void startBTPayment() {
        w("BTJYP", "BT TEST @@@@ 연결시작 @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);

        //설정이 안되어있는경우 연결진행
        if (!isSettingKeepConnecting) {
            connectBLE();
        } else {

            //연결유지 설정시 연결되었는지 확인.
            if (!((MainApplication) getApplication()).getIsBlueToothConnect()) {
                responseobj.setResultCd("-2");
                responseobj.setResultMsg(getString(R.string.btreader_not_search));
                sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                Toast.makeText(this, "상태창의 블루투스 연결유지 설정을 눌러서\n다시 연결하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            //연결된 상태.
            isActionConnected = true;

            pDialog.setTitle("기기와 연결되었습니다.\n초기화를 시작합니다.");
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setCancelable(false);
            pDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (isCancelable) {
                        isPressBackButton = true;
                        d("tag", "======================================== 취소 ========================================");
                        responseobj.setResultCd("-1");
                        responseobj.setResultMsg(getString(R.string.reader_iccard_key_backpress));
                        returnActivity(RESULT_CANCELED);
                    } else {
//                                Toast.makeText(PayResultActivity.this, "승인이 끝나고 취소하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            pDialog.show();

            //이미 블루투스 연결설정되었다면 바로 배터리체크 전송
            if (mService != null) {
                w("BTJYP", "BT TEST @@@@ 연결되어있으므로 초기화진행 @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);
                mService.enableTXNotification();    // 리더기에 초기화를 설정합니다
            } else {
                responseobj.setResultCd("-2");
                responseobj.setResultMsg(getString(R.string.btreader_not_search));
                sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                return;
            }
            sendHandlerMessage(MESSAGE_ACTION_GATT_CONNECTED, "", long_delaytime);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        d("tag", "requestCode :" + requestCode + " resultCode : " + resultCode + " data :" + AndroidUtils.getIntentData(data));

        switch (requestCode) {
            case ACTIVITY_KSNET_READER_CONNECT:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {

                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService);
///                    ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName()+ " - connecting");
                    if (mService != null) {
                        mService.connect(deviceAddress);
                    } else {
                        responseobj.setResultCd("-2");
                        responseobj.setResultMsg(getString(R.string.btreader_not_search));
                        sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                    }

                } else {
                    new AlertDialog.Builder(PayResultActivity.this)
                            .setTitle("안내")
                            .setCancelable(false)
                            .setIcon(R.drawable.warning)
                            .setMessage("리더기를 등록한후 사용하시기 바랍니다")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //TODO Auto-generated method stub
                                    finish();
                                }
                            }).show();
                }
                break;
            case ACTIVITY_KSNET_READER_CONNECTED:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case ACTIVITY_MENU_GET_SIGN:  // sign 된 데이타가 오는 곳입니다
            {
                if (resultCode == Activity.RESULT_OK) {
                    d("SIGN", "RESULT  OK :" + AndroidUtils.getIntentData(data));
                    _HashReaderData.put("encrptStrSign", AndroidUtils.getIntentData(data).getBytes());
                    adminInfo.setSignTrans("S".getBytes());
//                  adminInfo.getSignTransBase64();
                    ThreadAdmission(_HashReaderData);   // sign으로 나온 값입니다
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    responseobj.setResultCd("-1");
                    responseobj.setResultMsg(getString(R.string.reader_iccard_signfail));
                    returnActivity(RESULT_CANCELED);
                } else {
                    d("SIGN", "RESULT  NOT OK :" + AndroidUtils.getIntentData(data));
                }
            }
            break;

            default:
                e(TAG, "wrong request code");
                break;
        }
    }


    private final BroadcastReceiver BlueToothStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            w("BTJYP", "BT TEST status change receiver : action= " + action);

            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {

                isActionConnected = true;
            } else if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                //이곳에서 단말기 접속을 시도 합니다

                if (!isReceive) {
                    if (retryCount >= 5) {
                        responseobj.setResultCd("-3");
                        responseobj.setResultMsg(getString(R.string.btreader_not_search));
                        returnActivity(RESULT_CANCELED);
                        return;
                    }

                    //응답이 안와서 끊은경우 재시도

                    if (pDialog != null)
                        pDialog.setContentText("재시도 " + (++retryCount) + "/5회");

                    try {
                        LocalBroadcastManager.getInstance(PayResultActivity.this).unregisterReceiver(BlueToothStatusChangeReceiver);
                    } catch (Exception ignore) {
                        e(TAG, ignore.toString());
                    }
                    if (mService != null) {
                        try {
                            w("@@@@@@@ unbind Service @@@@@@@@");
                            unbindService(mServiceConnection);
                        } catch (Exception e) {
                        }
//
                        try {
                            if (!isSettingKeepConnecting &&mService != null) {
                                mService.close();
                                mService.stopSelf();
                                mService = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//
                        try {
                            if (mBtAdapter != null) {
                                mBtAdapter.cancelDiscovery();
                                mBtAdapter = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            if (mDevice != null) {
                                mDevice = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Intent bindIntent = new Intent(PayResultActivity.this, UartService.class);
                    bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

                    LocalBroadcastManager.getInstance(PayResultActivity.this).registerReceiver(BlueToothStatusChangeReceiver, makeGattUpdateIntentFilter());

                    sendHandlerMessage(MESSAGE_KSNET_READER_SEARCH, "", 4000);
                    return;
                }

            } else if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {  // 이곳서는 밧데리 체크등을 보냅니다
                w("BTJYP", "BT TEST @@@@ 연결됨 초기화 진행 @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);
                if (mService != null) {
                    mService.enableTXNotification();    // 리더기에 초기화를 설정합니다
                } else {
                    responseobj.setResultCd("-2");
                    responseobj.setResultMsg(getString(R.string.btreader_not_search));
                    sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                    return;
                }
                sendHandlerMessage(MESSAGE_ACTION_GATT_CONNECTED, "", long_delaytime);
            } else if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {   // 이곳에서 데이타를 다 모아서 처리할 수 있게끔 해줍니다

                final byte[] txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA);

                if (BuildConfig.IS_DEVEL) {
                    i("BTJYP", "\ntxValue [hex] " + Utils.byteArrayToHexString(txValue) + "\n[idx] " + txValue.length);
                }

                w("BTJYP", "status change receiver : txValue= " + new String(txValue) + " 배터리체크" + isBatChk);

                isReceive = true;
                retryCount = 0;
                if (isBatChk) {
                    w("BTJYP", "BT TEST @@@@ 배터리체크 시작 @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);
                    //배터리체크 응답값 x.x.x.CR.LF
                    if (txValue.length == 5 && txValue[3] == 0x0D && txValue[4] == 0x0A) {
                        w("BTJYP", "BT TEST @@@@ 배터리정보 받음. @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);

                        byte[] bRemainBat = KsnetUtils.byteToSubByte(txValue, 0, 3);
                        int RemainBat = Integer.parseInt(new String(bRemainBat));
                        String msg = "";
                        if (RemainBat > 800)
                            msg = "==== 배터리가 [충분]합니다 ====";
                        if (RemainBat > 700 && RemainBat <= 800)
                            msg = "==== 배터리가 [보통]입니다 ====";
                        if (RemainBat <= 700) {
                            msg = "==== 배터리가 [부족]합니다 ====";
                        }
                        final String fMsg = msg;


                        Toast.makeText(PayResultActivity.this, fMsg, Toast.LENGTH_LONG).show();

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                w("BTJYP", "BT TEST @@@@ 동글정보 요청시작 [C0]->D0 @@@@ time:" + (System.currentTimeMillis() - testTime) / 1000.0);
                                if (encMSRManager != null) {
                                    byte[] requestDongleInfo = encMSRManager.makeDongleInfo();
                                    if (mService != null) {
                                        mService.writeRXCharacteristic(requestDongleInfo);
                                    } else {
                                        responseobj.setResultCd("-2");
                                        responseobj.setResultMsg(getString(R.string.btreader_not_search));
                                        sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                                    }
                                }
                            }
                        }, 500);

                        isBatChk = false;
                        return;
                    }
                }


                if (txValue[0] == (byte) 0x02 && _recvTmpDataLength == 0) {   // 받은 데이터가 STX값을 가지고 있으면 초기화 합니다
                    Arrays.fill(_recvData, (byte) 0x00);   // 바이트 초기화 입니다
                    _recvDataLength = 0;
                    _recvTmpDataLength = 0;
                    d("BTJYP", "receive from reader  first", "처음인가요");
                    if (KsnetParsingByte.GetCommandID(_recvData) == (byte) 0xD2) {
                        SweetDialog(getString(R.string.reader_card_reading));
                    }
                }
                w("BTJYP", "txValue.length: " + txValue.length);
                try {
                    System.arraycopy(txValue, 0, _recvData, _recvDataLength, txValue.length);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();

                    SweetDialog(getString(R.string.reader_iccard_reject_end));

                    responseobj.setResultCd("-3");
                    responseobj.setResultMsg(getString(R.string.reader_iccard_reject_end));

                    sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                    return;
                }
                _recvDataLength += txValue.length;
                _recvTmpDataLength += txValue.length;
                w("BTJYP", "_recvDataLength.length: " + _recvDataLength);

                //test
                w("[이벤트 수신]::::::[전달값]::::::: \n\n::::::::::::[hex]" + Utils.byteArrayToHexString(txValue) + "\n[idx]" + txValue.length);

                if (_recvData[0] == (byte) 0x02) { //stx check
                    int Length = KsnetUtils.byte2Int(_recvData[1]) * 0xff + KsnetUtils.byte2Int(_recvData[2]);
                    d("printHex__BTJYP ", "전체길이받은길이:" + Length + "/" + _recvDataLength);


                    //STX, Length, LRC 바이트길이 4제외
                    if (Length != _recvDataLength - 4) {
                        return;
                    } else {
                        _recvTmpDataLength = 0;
                        d("BTJYP: receive from reader complete", EncMSRManager.byteArrayToHex(_recvData, _recvDataLength));
                    }
                }

                if (KsnetParsingByte.MakeReceiveData(_recvData) == 1) {
                    final byte cmd = KsnetParsingByte.GetCommandID(_recvData);

                    d("BTJYP CMDMSG " + EncMSRManager.byteToString(cmd));

                    switch (cmd) {
                        case (byte) 0xD0: //상태정보 응답전문
                            sendHandlerMessage(MESSAGE_DPT_D0, _recvData, delaytime);
                            break;
                        case (byte) 0xD1: //상태 세팅 결과 응답
                            break;
                        case (byte) 0xD2: //카드번호 전송 응답전문
                            SweetDialog(getString(R.string.reader_card_reading));
                            if (isFirstCardNunResq) {
                                sendHandlerMessage(MESSAGE_DPT_D2, _recvData, "CardNumSendResp", KsnetUtils.byteToString(_recvData, IDX_DATA, 2), delaytime);
                                isFirstCardNunResq = false;
                            }
                            break;
                        case (byte) 0xD3: //2nd Generation 응답전문
                            sendHandlerMessage(MESSAGE_DPT_D2, _recvData, "2ndGenResp", KsnetUtils.byteToString(_recvData, IDX_DATA, 2), delaytime);
                            isFirstCardNunResq = true;
//                            map.put("2ndGenResp", AndroidUtils.byteToString(_recvData, IDX_DATA, 2));
                            break;

                        case (byte) 0xD4: //무결성 검증 요청 응답전문  처음 접속시에 무결성 점검을 해야 합니다
                        {

                            sendHandlerMessage(MESSAGE_DPT_D4, _recvData, delaytime);
                            _HashReaderData.clear();
                            /*
                            d("inter","무결성 점검 코드 : "+KsnetUtils.byteToString(_recvData, IDX_DATA, 2));
                            if ((KsnetUtils.byteToString(_recvData, IDX_DATA, 2)).equals("00")) {
                                sendHandlerMessage(MESSAGE_DPT_D4 , _recvData , delaytime );
                            } else  {
                                // 무결성에서 에러이면 끝 ~~~~
                                responseobj.setResultCd("-1");
                                responseobj.setResultMsg(getString(R.string.reader_iccard_instantment_fail));
                                returnActivity(RESULT_CANCELED );

                            }
                            */

                        }

                        break;

                        case (byte) 0xD5: //Fallback 후 카드번호 전송 응답전문
                            SweetDialog(getString(R.string.reader_card_reading));
                            sendHandlerMessage(MESSAGE_DPT_D5, _recvData, "FallBackCardResp", KsnetUtils.byteToString(_recvData, IDX_DATA, 2), delaytime);
//                            map.put("FallBackCardResp", AndroidUtils.byteToString(_recvData, IDX_DATA, 2));
                            break;
                        case (byte) 0xD6: //카드 삽입 제거
//                            map.put("ICCardInOut", AndroidUtils.byteToString(_recvData, IDX_DATA, 3));
                            sendHandlerMessage(MESSAGE_DPT_D6, _recvData, "ICCardInOut", KsnetUtils.byteToString(_recvData, IDX_DATA, 3), delaytime);
                            //sendHandlerMessage(MESSAGE_DPT_D6 , _recvData , delaytime );
                            break;
                        default:
                            break;
                    } // switch (cmd) {
                }
//                else {
//                    responseobj.setResultCd("-3");
//                    responseobj.setResultMsg(getString(R.string.bterror));
//                    returnActivity(RESULT_CANCELED);
//                }
            } else if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                isReceive = false;
                mService.disconnect();
//                responseobj.setResultCd("-3");
//                responseobj.setResultMsg("UART를 지원하지 않습니다.");
//                returnActivity(RESULT_CANCELED);
            }
        }

    }; // BroadcastReceiver


    public int MakeReceiveData(byte[] bArr) {
        int byteToInt = (Utils.byteToInt(_recvData[1]) * 256) + Utils.byteToInt(_recvData[2]) + 4;

        System.arraycopy(bArr, 0, _recvData, _recvDataLength, bArr.length);
        _recvDataLength += bArr.length;

        w("[이벤트 수신]::::::[_recvData]::::::: \n\n::::::::::::[hex]" + Utils.stringToHex(new String(Utils.byteToString(_recvData, 0, _recvDataLength).getBytes())) + "\n[idx]" + _recvDataLength);
        int i = 0;
        while (true) {
            if (i == _recvDataLength || _recvDataLength <= 3) {
                break;
            }
            if (_recvData[0] != 2 || _recvDataLength < byteToInt) {
                break;
            }
            if (_recvData[byteToInt - 2] != 3 || _recvData[byteToInt - 1] != Utils.LRC(Utils.byteToSubByte(_recvData, 0, byteToInt))) {
                break;
            }
            w("[이벤트 데이터 수신완료]::::::[버퍼수신 길이 = " + _recvDataLength + "]::::::: \n");
            if (CheckCommandError(Utils.byteToSubByte(_recvData, 0, byteToInt))) {
                w("::::::::::::::[리더기 응답오류 발생! 결제 종료]::::::: \n");
                clearTempBuffer();
                break;
            }

            cleaReadBuffer(byteToInt);

//            GetCommandID(Utils.byteToSubByte(_recvData, 0, byteToInt));
//            Thread thread = new Thread(new ThreadSerialWrite(GetCommandID(Utils.byteToSubByte(_recvData, 0, byteToInt))));
//            thread.start();
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


            try {
                Thread.sleep(10);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            i++;

        }
        return 0;
    }

    private void cleaReadBuffer(int i) {
        int i2 = _recvDataLength;
        int i3 = i2 - i;
        if (i2 == 0) {
            i3 = i;
        }
        byte[] bArr = new byte[1024];
        System.arraycopy(_recvData, i, bArr, 0, i3);
        System.arraycopy(bArr, 0, _recvData, 0, 1024);
        _recvDataLength = i3;
        w("::::::[데이터 버퍼 ReadClear]:::::::\n전체수신 버퍼 길이 : " + i2 + "\n남은 버퍼길이 : " + i3);
    }


    public byte GetCommandID(byte[] bArr) {
        if (bArr == null || bArr.length < 6) {
            return 1;
        }
        if (bArr[0] != 2) {
            return 2;
        }
        if (bArr[_recvDataLength - 2] != 3) {
            return 3;
        }
        return bArr[3];
    }


    public boolean CheckCommandError(byte[] bArr) {
        w("::::::::::::::::::::::::::::::Start CheckCommandError:::::::::::::::::::::::::::::::::::::::::::::::");
        if (bArr.length == 6 && bArr[0] == 2 && bArr[bArr.length - 2] == 3 && bArr[3] == 21) {
            w("::::::::::::::::::::::::::::::Start CheckCommandError::::::6byte NAK Command");
        }
        return false;
    }


    public void onBlueToothComplete(HashMap<String, String> map) {
        if (map == null || map.size() == 0) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("블루투스 검색")
                    .setContentText(map.get("name") + "을 찾을 수 없습니다")
                    .show();
        } else {   // 이곳은 블루투스가 연결된 곳입니다

        }
    }

    public void sendHandlerMessage(int msg_menu, String data, int delaytime) {
        Message msg = mHandler.obtainMessage(msg_menu);
        Bundle bundle = new Bundle();
        bundle.putString("type", "string");
        bundle.putString("data", data);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, delaytime);
    }

    public void sendHandlerMessage(int msg_menu, byte[] data, int delaytime) {
        Message msg = mHandler.obtainMessage(msg_menu);
        Bundle bundle = new Bundle();
        bundle.putString("type", "byte");
        bundle.putByteArray("data", data);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, delaytime);
    }


    public void sendHandlerMessage(int msg_menu, byte[] data, String key, String value, int delaytime) {
        Message msg = mHandler.obtainMessage(msg_menu);
        Bundle bundle = new Bundle();
        bundle.putString("type", "byte");
        bundle.putString("key", key);
        bundle.putString(key, value);
        bundle.putByteArray("data", data);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, delaytime);
    }


    private void sendBTMessage(byte[] bMsg) {
        w("sendBTMessage", "isActionConnected: " + isActionConnected);
        if (isActionConnected) {


            AndroidUtils.printHex(bMsg);
            d("send to reader", EncMSRManager.byteArrayToHex(bMsg, bMsg.length));

            for (int i = 0; i < (bMsg.length / 20) + 1; i++) {
                byte[] sub_packet;
                if (i == bMsg.length / 20)
                    sub_packet = KsnetUtils.byteToSubByte(bMsg, i * 20, bMsg.length - (20 * i));
                else
                    sub_packet = KsnetUtils.byteToSubByte(bMsg, i * 20, 20);
                try {
                    if (mService != null) {
                        mService.writeRXCharacteristic(sub_packet);
                    } else {
                        responseobj.setResultCd("-2");
                        responseobj.setResultMsg(getString(R.string.btreader_not_search));
                        sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
                    }
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            responseobj.setResultCd("-2");
            responseobj.setResultMsg(getString(R.string.btreader_not_search));
            sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
        }
    }

    public void addText(String message) {
        try {
            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
            w("test", "message: " + message);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //임시 수신데이터 삭제
    private void clearTempBuffer() {
        clearTerminalBuffer();  // 단말기에서 전송된 데이터 보관
        _recvDataLength = 0;     // 실제 받은 데이터를 초기화 합니다
        //receiveobj = null;
        // receiveobj = new receiveObj();

        responseobj = null;
        responseobj = new responseObj();
        _HashReaderData.clear();

        isNetCancel = false;
        isCardCancel = false;
        isICFirstAct = false;

        isFirstCardNunResq = true;
    }

    public void clearTerminalBuffer() {
        Arrays.fill(_recvData, (byte) 0x00);   // 바이트 초기화 입니다
        Arrays.fill(_recvData, (byte) 0xff);   // 바이트 초기화 입니다
        Arrays.fill(_recvData, (byte) 0x00);   // 바이트 초기화 입니다
        Arrays.fill(_responseTelegram, (byte) 0x00);   // 바이트 초기화 입니다
        Arrays.fill(_responseTelegram, (byte) 0xff);   // 바이트 초기화 입니다
        Arrays.fill(_responseTelegram, (byte) 0x00);   // 바이트 초기화 입니다


        if (EMVData != null) Arrays.fill(EMVData, (byte) 0x00);
        if (reqEMVData != null) Arrays.fill(reqEMVData, (byte) 0x00);
        if (readerModelNum != null) Arrays.fill(readerModelNum, (byte) 0x00);
        if (trackII != null) Arrays.fill(trackII, (byte) 0x00);
        if (EncryptInfo != null) Arrays.fill(EncryptInfo, (byte) 0x00);
        if (Cardbin != null) Arrays.fill(Cardbin, (byte) 0x00);

    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setRationaleTitle(R.string.rationale_title)
                    .setRationaleMessage(R.string.rationale_message)
                    .setDeniedTitle("권한 거절")
                    .setDeniedMessage("권한을 거절하시면 어플을 수행 할 수 없습니다\n\n다음에서 권한을 허락해주세요 [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS)
                    .check();
        }
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(PayResultActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(PayResultActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public void SweetDialog(String msg) {
        if (pDialog != null) {
            pDialog.setTitleText(msg);
            pDialog.setContentText("");
            pDialog.showContentText(false);
        }
    }

    public void SweetDialog(String msg, boolean isShow) {
        if (isShow) {
            if (pDialog.isShowing()) pDialog.dismiss();
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText(msg);
//            pDialog.setContentText()
            pDialog.setCancelable(false);
            pDialog.show();
        } else {
            if (pDialog.isShowing()) pDialog.dismiss();
        }
    }

    // park
    public void TimerCheck(int timeMiliseconds) {
        if (timeMiliseconds == 0) timeMiliseconds = 2000;
        basetime = new BataTime(timeMiliseconds);
        basetime.start(new BataTimeCallback() {
            @Override
            public void onUpdate(int elapsed) {
                d("TAG", "On update called...time elapsed = " + elapsed);
            }

            @Override
            public void onComplete() {
                d("TAG", "On complete called...");
                sendHandlerMessage(MESSAGE_RETURN_NOK_ACTIVITY, "", delaytime);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        d(TAG, "onDestroy()");
        if (pDialog != null && pDialog.isShowing()) pDialog.dismiss();

        if (mService != null) {

            try {
                if (!isSettingKeepConnecting && mService != null) {
                    mService.close();
                    mService.stopSelf();
                    mService = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
//                if (mBound) {
                if (mServiceConnection != null) {
                    w("@@@@@@@ unbind Service @@@@@@@@");
                    unbindService(mServiceConnection);
                }
//                }

            } catch (Exception e) {
            }

            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(BlueToothStatusChangeReceiver);
            } catch (Exception ignore) {
                e(TAG, ignore.toString());
            }


        }
        if (basetime != null) {
            basetime.stop();
        }


//        android.os.Process.killProcess(android.os.Process.myPid());
//        finishAffinity();
//        System.runFinalization();
//        System.exit(0);
//        this.moveTaskToBack(true);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//                android.os.Process.killProcess(android.os.Process.myPid());
//            }
//        },1000);

    }

    @Override
    protected void onStop() {
        d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        d(TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        d(TAG, "onResume");
        if (mBtAdapter != null &&
                !mBtAdapter.isEnabled()) {
            i(TAG, "onResume - BT not enabled yet");
            //   Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //   startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onClick(View v) {
        w("TEST");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        d(TAG, "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back pressed.
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        d(TAG, "onBackPressed");
//        super.onBackPressed();
        if (isCancelable) {
            responseobj.setResultCd("-1");
            responseobj.setResultMsg(getString(R.string.reader_iccard_key_backpress));
            returnActivity(RESULT_CANCELED);
        } else {
//            Toast.makeText(this, "승인이 끝나고 취소하시기 바랍니다.",Toast.LENGTH_SHORT).show();
        }
    }


    class ThreadSerialWrite implements Runnable {
        byte cmd;

        ThreadSerialWrite(byte b) {
            this.cmd = b;
        }

        public void run() {
            Intent intent;
            d(":::::::::::::::::::::::::::::::::::::::::::::::::Start ThreadSerialWrite::::::::::::::::::::::::::::::::::::::::::::::::");
            HashMap hashMap = new HashMap();
            if (this.cmd == 0xD0) { //상태정보 수신
                hashMap.put("RequestDongleInfo", Utils.byteToString(_recvData, 36, 1));

                d("[수신]:::::RequestDongleInfo:::::");
            } else if (Constants.IS_TEST) {
                d("[수신]:::::::::::No Command ");
            }
            if (hashMap.containsKey("ICCardInOut")) {
                if (((String) hashMap.get("ICCardInOut")).equals("INS") && Constants.IS_TEST) {
                    d("[수신]:::::INS:::::");
                }
                ((String) hashMap.get("ICCardInOut")).equals("DEL");
            }
            if (hashMap.containsKey("RequestDongleInfo")) {
                if (((String) hashMap.get("RequestDongleInfo")).equals("O")) {

                    if (((MainApplication) getApplication()).getIsBootFirst().booleanValue()) {
//                        TabMain.this.ReaderModelNum = new String(Utils.byteToSubByte(TabMain._recvData, 4, 16));
//                        TabMain.this.mApp.setReaderModelName(TabMain.this.ReaderModelNum);
//                        TabMain.this.mApp.setSWModelName(TabMain.this.SWModelNum);
//                        d("#####상태정보요청 성공");
//                        new Thread(new Runnable() {
//                            public void run() {
//                                TabMain.this.runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        TextView textView = TabMain.this.tvBranchPhone;
//                                        textView.setText("[SW] " + TabMain.this.SWModelNum + " [Reader] " + TabMain.this.ReaderModelNum);
//                                    }
//                                });
//                            }
//                        }).start();
//                        TabMain.this.CDT.cancel();
//                        TabMain.this.adBuild.cancel();
                        clearTempBuffer();
                        ((MainApplication) getApplication()).setIsBootFirst(false);
                    } else if (!((MainApplication) getApplication()).getIsBootFirst().booleanValue()) {
//                        d("#####상태정보요청 성공22");
//                        if (TabMain.this.CDT != null) {
//                            d("#####Timer 해지22");
//                            TabMain.this.adBuild.cancel();
//                            TabMain.this.CDT.cancel();
//                            TabMain.this.clearTempBuffer();
//                        }
                        clearTempBuffer();
//                        if (TabMain.this.isChkCredit.booleanValue()) {
//                            d("#####isCHkCredit 트루");
//                            if (new Configuration(TabMain.this).getStaffInfo().length > 0) {
//                                intent = new Intent(TabMain.this, SetStaffBranchList.class);
//                                intent.putExtra("BranchID", TabMain.this.SelectedBranchID);
//                                intent.putExtra("BranchDPT", TabMain.this.SelectedBranchDPT);
//                                intent.putExtra("BranchNAME", TabMain.this.SelectedBranchName);
//                                intent.putExtra("Boss", TabMain.this.SelectedBranchBoss);
//                                intent.putExtra("Addr", TabMain.this.SelectedBranchAddr);
//                                intent.putExtra("Phone", TabMain.this.SelectedBranchPhone);
//                                intent.putExtra("BtnSelName", "Credit");
//                            } else {
//                                intent = new Intent(TabMain.this, PayCreditStep1.class);
//                                intent.putExtra("StaffID", "");
//                                intent.putExtra("BranchID", TabMain.this.SelectedBranchID);
//                                intent.putExtra("BranchDPT", TabMain.this.SelectedBranchDPT);
//                                intent.putExtra("BranchNAME", TabMain.this.SelectedBranchName);
//                                intent.putExtra("Boss", TabMain.this.SelectedBranchBoss);
//                                intent.putExtra("Addr", TabMain.this.SelectedBranchAddr);
//                                intent.putExtra("Phone", TabMain.this.SelectedBranchPhone);
//                            }
//                            d("#####PayCreditStep1 클릭 ");
//                            TabMain.this.startActivity(intent);
//                            Boolean unused = TabMain.this.isChkCredit = false;
//                        }
                    }
                } else if (((String) hashMap.get("RequestDongleInfo")).equals("X")) {
                    clearTempBuffer();
                }
            }
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Constants.IS_TEST) {
                d(":::::::::::::::::::::::::::::::::::::::::::::::::::::::End ThreadSerialWrite:::::::::::::::::::::::::::::::::::::::::::::::");
            }
        }
    }


}
