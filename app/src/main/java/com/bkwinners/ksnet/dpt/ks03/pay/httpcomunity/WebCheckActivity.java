package com.bkwinners.ksnet.dpt.ks03.pay.httpcomunity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.bkwinners.ksnet.dpt.ErrorActivity;
import com.bkwinners.ksnet.dpt.ReceiptActivity;
import com.bkwinners.ksnet.dpt.Toasty;
import com.bkwinners.ksnet.dpt.action.PayResultActivity;
import com.bkwinners.ksnet.dpt.action.obj.responseObj;
import com.bkwinners.ksnet.dpt.action.process.ksnetmodule.obj.AdminInfo;
import com.bkwinners.ksnet.dpt.common.Utility;
import com.bkwinners.ksnet.dpt.common.Utils;
import com.bkwinners.ksnet.dpt.db.PaymentInfo;
import com.bkwinners.ksnet.dpt.design.IntroActivity;
import com.bkwinners.ksnet.dpt.design.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.design.util.Util;
import com.bkwinners.ksnet.dpt.ks03.obj.KsnetResponseObj;
import com.bkwinners.ksnet.dpt.ks03.obj.Request;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.bkwinners.ksnet.dpt.ks03.pay.DirectPaymentActivity;
import com.bkwinners.ksnet.dpt.ks03.pay.httpcomunity.api.APIService;
import com.bkwinners.ksnet.dpt.ks03.pay.httpcomunity.model.DirectPayment;
import com.bkwinners.ksnet.dpt.ks03.pay.httpcomunity.retrofitclient.ApiUtils;
import com.bkwinners.ksnet.dpt.ks03.pay.httpcomunity.sms.PatternUtil;
import com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus;
import com.bkwinners.ksnet.dpt.ks03.pay.ksnet.KSNETStatus;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
import com.bkwinners.ksnet.dpt.telegram.NotiAsyncTask;
import com.pswseoul.util.AndroidUtils;
import com.pswseoul.util.GsonUtil;
import com.pswseoul.util.SunUtil;
import com.pswseoul.util.tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zj.com.cn.bluetooth.sdk.BluetoothService;
import zj.com.cn.bluetooth.sdk.DeviceListActivity;
import zj.com.command.sdk.Command;
import zj.com.command.sdk.PrintPicture;
import zj.com.command.sdk.PrinterCommand;

import static com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus.PHONE;

public class WebCheckActivity extends Activity {

    private static final int MAX_RETRY_COUNT = 5;
    private static boolean IS_DOING = false;

    private final Realm realm = Realm.getDefaultInstance();

    private TextView mResponseTv;
    private APIService mAPIService;
    private APIService mAPIDirectService;
    private APIService mAPISMSService;

    private static final boolean DEBUG = true;
    private static final String TAG = "tagtag";

    private int menu_state = -1;

    private final int REQUEST_CODE_DIRECT_PAYMENT = 1111;

    private final int TOKEN = 15001;
    private final int APPROVE_BEFORE = TOKEN + 1;
    private final int APPROVE_AFTER = TOKEN + 2;

    private final int APPROVE_PRINTER_SEARCH = TOKEN + 4;
    private final int APPROVE_PRINTER = TOKEN + 5;
    private final int APPROVE_PRINTER_CLOSE = TOKEN + 6;


    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1000;
    private static final int REQUEST_ENABLE_BT = 1200;

    /******************************************************************************************************/
    // Message types sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;

    public static final String DEVICE_NAME = "device_name";

    /******************************************************************************************************/
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    private BluetoothService mService = null;

    private static final String CHINESE = "GBK";
    private static final String THAI = "CP874";
    private static final String KOREAN = "EUC-KR";
    private static final String BIG5 = "BIG5";

    private final KsnetResponseObj ksnetresp = new KsnetResponseObj();

    private final responseObj resp = new responseObj();

    private SweetAlertDialog sweetAlertDialog;

    String tmnId = "";
    String serial = "";
    String appId = "";
    String mchtId = "";
    String version = "";
    String telNo = "";

    private String payerName = "";
    private String payerEmail = "";
    private String payerTel = "";

    private String payerAddr = "";
    private String prodName = "";
    private final String prodQty = "1";

    private String amount;
    private String name;
    private String cardNum;
    private String expiry_year;
    private String expiry_month;
    private String installment;

    private String trackId;
    private String vanTrxId;


    String et_AuthNum = "";
    String et_Authdate = "";

    Intent return_data;

    String print_flag = "false";

    AdminInfo adminInfo;
    HashMap<String, byte[]> m_hash = new HashMap<String, byte[]>();

    boolean isCancel = false;
    private int retryCount = 0;

    private String uniqueID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (BuildConfig.IS_DEVEL)
            Constants.IS_TEST = true;

        //유니크아이디
        uniqueID = AndroidUtils.getAppPreferences(this, "uniqueId");

        if (uniqueID == null || uniqueID.equals("") || uniqueID.length() == 0) {
            uniqueID = UUID.randomUUID().toString();
            AndroidUtils.setAppPreferences(this, "uniqueId", uniqueID);
        }

        init();

//        if (BuildConfig.IS_DEVEL)
//            registerErrorHandlerForDebuging();

        return_data = new Intent();

        if (IS_DOING) {
            try {
                JSONObject logJson = new JSONObject();
                logJson.put("message", "연속호출됨.");
                if(getIntent()!=null && getIntent().getExtras()!=null) {
                    logJson.put("data", getIntent().getExtras().toString());
                }else{
                    logJson.put("data", "null");
                }
                logJson.put("uniqueID", uniqueID);
                logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
                logJson.put("os",Build.VERSION.SDK_INT + "");
                logJson.put("model",Build.MODEL + "");
                new NotiAsyncTask(this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
            } catch (Exception e){}

            return_data.putExtra("resultMsg", "데이터 없음");
            return_data.putExtra("isSuccess", false);
            setResult(RESULT_OK, return_data);
            finish();
            return;
        }
        IS_DOING = true;

        mAPIService = ApiUtils.getAPIService();
        mAPIDirectService = ApiUtils.getAPIDirectService();
        mAPISMSService = ApiUtils.getSMSSendService();

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (getIntent() == null) {
            Log.e("error", "empty data");
            return_data.putExtra("resultMsg", "데이터 없음");
            return_data.putExtra("isSuccess", false);
            setResult(RESULT_OK, return_data);
            finish();
            return;
        }

        /* 출력에 사용 됩니다 */
        if (getIntent().getStringExtra("print_flag") != null) {
            print_flag = getIntent().getStringExtra("print_flag");
            resp.data.put("print_flag", getIntent().getStringExtra("print_flag"));
        }

        if (getIntent().getStringExtra("TotalAmount") != null) {
            ksnetresp.data.put("TotalAmount", getIntent().getStringExtra("TotalAmount"));
        }

        if (getIntent().getStringExtra("name") != null) {
            ksnetresp.data.put("name", getIntent().getStringExtra("name"));
        }
        if (getIntent().getStringExtra("ceoName") != null) {
            ksnetresp.data.put("ceoName", getIntent().getStringExtra("ceoName"));
        }
        if (getIntent().getStringExtra("identity") != null) {
            ksnetresp.data.put("identity", getIntent().getStringExtra("identity"));
        }
        if (getIntent().getStringExtra("telNo") != null) {
            ksnetresp.data.put("telNo", telNo = getIntent().getStringExtra("telNo"));
        }
        if (getIntent().getStringExtra("addr") != null) {
            ksnetresp.data.put("addr", getIntent().getStringExtra("addr"));
        }
        if (getIntent().getStringExtra("vat") != null) {
            ksnetresp.data.put("vat", getIntent().getStringExtra("vat"));
        }
        if (getIntent().getStringExtra("installment") != null) {
            ksnetresp.data.put("installment", getIntent().getStringExtra("installment"));
        }


        //token
        if (getIntent().getStringExtra("key") != null) {
            resp.data.put("key", getIntent().getStringExtra("key"));
        }

        if (getIntent().getStringExtra("tmnId") != null) {
            tmnId = getIntent().getStringExtra("tmnId");
            resp.data.put("tmnId", getIntent().getStringExtra("tmnId"));
        }

        if (getIntent().getStringExtra("serial") != null) {
            serial = getIntent().getStringExtra("serial");
            resp.data.put("serial", getIntent().getStringExtra("serial"));
        }

        if (getIntent().getStringExtra("mchtId") != null) {
            mchtId = getIntent().getStringExtra("mchtId");
            resp.data.put("mchtId", getIntent().getStringExtra("mchtId"));
        }

        if (getIntent().getStringExtra("payerName") != null) {
            payerName = getIntent().getStringExtra("payerName");
            resp.data.put("payerName", getIntent().getStringExtra("payerName"));
        }
        if (getIntent().getStringExtra("payerEmail") != null) {
            payerEmail = getIntent().getStringExtra("payerEmail");
            resp.data.put("payerEmail", getIntent().getStringExtra("payerEmail"));
        }
        if (getIntent().getStringExtra("payerTel") != null) {
            payerTel = getIntent().getStringExtra("payerTel");
            resp.data.put("payerTel", getIntent().getStringExtra("payerTel"));
        }
        if (getIntent().getStringExtra("payerAddr") != null) {
            payerAddr = getIntent().getStringExtra("payerAddr");
            resp.data.put("payerAddr", getIntent().getStringExtra("payerAddr"));
        }
        if (getIntent().getStringExtra("prodName") != null) {
            prodName = getIntent().getStringExtra("prodName");
            resp.data.put("prodName", getIntent().getStringExtra("prodName"));
        }


        if (getIntent().getStringExtra("trackId") != null) {
            resp.data.put("trackId", getIntent().getStringExtra("trackId"));
            trackId = getIntent().getStringExtra("trackId");
        }

        if (getIntent().getStringExtra("TrxId") != null) {
            resp.data.put("trxId", getIntent().getStringExtra("TrxId"));
        }
        if (getIntent().getStringExtra("trxId") != null) {
            resp.data.put("trxId", getIntent().getStringExtra("trxId"));
        }

//        resp.data.put("appId", appId);
//        resp.data.put("version", version);
//        resp.data.put("telNo", telNo);

        //////////////////////////// 승인 정보 ////////////////////////////////
        if (getIntent().getStringExtra("Message1") != null) {
            ksnetresp.data.put("Message1", getIntent().getStringExtra("Message1"));
        }
        if (getIntent().getStringExtra("PurchaseName") != null) {
            ksnetresp.data.put("PurchaseName", getIntent().getStringExtra("PurchaseName"));
        }
        if (getIntent().getStringExtra("CardNo") != null) {
            ksnetresp.data.put("CardNo", getIntent().getStringExtra("CardNo"));
        }
        if (getIntent().getStringExtra("AuthNum") != null) {
            ksnetresp.data.put("AuthNum", getIntent().getStringExtra("AuthNum"));
        }

        if (getIntent().getStringExtra("TotalAmount") != null) {
            amount = getIntent().getStringExtra("amount");
        }

//        if (getIntent().getStringExtra("secondKey") != null) {
//            ksnetresp.data.put("secondKey", getIntent().getStringExtra("secondKey"));
//            resp.data.put("secondKey", getIntent().getStringExtra("secondKey"));
//        }


//        if (getIntent().getStringExtra("van") != null) {
//            ksnetresp.data.put("van", getIntent().getStringExtra("van"));
//            resp.data.put("van", getIntent().getStringExtra("van"));
//        }

//        if (getIntent().getStringExtra("vanId") != null) {
//            ksnetresp.data.put("vanId", getIntent().getStringExtra("vanId"));
//            resp.data.put("vanId", getIntent().getStringExtra("vanId"));
//        }

//        if (getIntent().getStringExtra("vanTrxId") != null) {
//            ksnetresp.data.put("vanTrxId", getIntent().getStringExtra("vanTrxId"));
//            resp.data.put("vanTrxId", getIntent().getStringExtra("vanTrxId"));
//        }


        String action = getIntent().getStringExtra("action");

        if ("getToken".equals(action)) {
//
//            sendToken();
//
        } else if ("paymentReady".equals(action)) {
//
//            String amount = getIntent().getStringExtra("amount");
//            String installment = getIntent().getStringExtra("installment");
//
//            sendCheckApprove(amount, installment);
//
        } else if ("paymentReceipt".equals(action)) {
            if(getIntent()==null
                    || !getIntent().hasExtra("amount") || !(getIntent().getStringExtra("amount").length()>0)
                    || !getIntent().hasExtra("authDate") || !(getIntent().getStringExtra("authDate").length()>0)
                    || !getIntent().hasExtra("authNum") || !(getIntent().getStringExtra("authNum").length()>0)
                    || !Utils.isStringNumber(getIntent().getStringExtra("amount"))
                    || !getIntent().hasExtra("payType")){
                Toast.makeText(this, "결제를 조회할 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }


            LOG.w("amount: "+getIntent().getStringExtra("amount"));
            LOG.w("authDate: "+getIntent().getStringExtra("authDate"));
            LOG.w("authNum: "+getIntent().getStringExtra("authNum"));
            LOG.w("payType: "+getIntent().getStringExtra("payType"));

            Intent intent = new Intent(this, ReceiptActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);

            finish();
        } else if ("sendDirectPayment".equals(action)) {
            getPayKey();

        } else if ("cancelDirectPayment".equals(action)) {

            m_hash = (HashMap<String, byte[]>) getIntent().getSerializableExtra("AdminInfo_Hash");
            if (m_hash.containsKey("trxId")) {
                ksnetresp.data.put("trxId", new String(m_hash.get("trxId")));
                resp.data.put("trxId", new String(m_hash.get("trxId")));
            } else if (m_hash.containsKey("TrxId")) {
                ksnetresp.data.put("trxId", new String(m_hash.get("TrxId")));
                resp.data.put("trxId", new String(m_hash.get("TrxId")));
                m_hash.put("trxId", m_hash.get("TrxId"));
            }
            if (m_hash.containsKey("TotalAmount")) {
                amount = new String(m_hash.get("TotalAmount"));
            }
            refundDirectPayment();

        } else if ("getStatistics".equals(action)) {

            String startDay = getIntent().getStringExtra("startDay");
            String endDay = getIntent().getStringExtra("endDay");

            getPaymentStatistics(startDay, endDay);

        } else if ("getList".equals(action)) {

            String startDay = getIntent().getStringExtra("startDay");
            String endDay = getIntent().getStringExtra("endDay");

            getPaymentList(startDay, endDay);

        } else if ("cashReceipt".equals(action)) {

            m_hash = (HashMap<String, byte[]>) getIntent().getSerializableExtra("AdminInfo_Hash");

            m_hash.put("SignTrans", "N".getBytes());                                        // 서명거래 필드, 무서명(N) 50000원 이상 서명(S)
            m_hash.put("PlayType", "D".getBytes());                                         // 실행구분,  데몬사용시 고정값(D)
            m_hash.put("CardType", "".getBytes());                                          // 은련선택 여부필드 (현재 사용안함)
            m_hash.put("WorkType", "01".getBytes());                                         //01 고정
            m_hash.put("BranchNM", "".getBytes()); // 사용하지않음
            m_hash.put("BIZNO", "".getBytes()); // 사용하지않음

            m_hash.put("ksnet_server_ip", getString(R.string.ksnet_server_ip).getBytes()); // 접속 서버 아이피
            m_hash.put("ksnet_server_port", getString(R.string.ksnet_server_port).getBytes()); // 접속 서버 포트
            m_hash.put("ksnet_telegrametype", getString(R.string.ksnet_telegrametype).getBytes()); // Telegrame Type
            m_hash.put("ksnet_timeout", getString(R.string.ksnet_timeout).getBytes()); // 타임아웃
            adminInfo = new AdminInfo(m_hash);

//            if(adminInfo.getReceiptNo()!=null && !adminInfo.getReceiptNo().equals("X")){
            //신용카드결제
            if (sweetAlertDialog != null && adminInfo.getReceiptNo() != "".getBytes()) {
                sweetAlertDialog.setTitleText("현금영수증 승인중입니다.");
                if (!sweetAlertDialog.isShowing())
                    sweetAlertDialog.show();
//            }
            }

            sendPush(true);


            //van 결제
        } else if ("vanPayment".equals(action)) {

            try {
                m_hash = (HashMap<String, byte[]>) getIntent().getSerializableExtra("AdminInfo_Hash");
            }catch (Exception e){}
            if(m_hash==null){
                return_data.putExtra("action", "vanPayment");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", "AdminInfo_Hash 데이터가 없습니다.");
                Toast.makeText(WebCheckActivity.this, "[mtouch]호출데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, return_data);
                return;
            }

            m_hash.put("ksnet_server_ip", getString(R.string.ksnet_server_ip).getBytes()); // 접속 서버 아이피
            m_hash.put("ksnet_server_port", getString(R.string.ksnet_server_port).getBytes()); // 접속 서버 포트
            m_hash.put("ksnet_telegrametype", getString(R.string.ksnet_telegrametype).getBytes()); // Telegrame Type
            m_hash.put("ksnet_timeout", getString(R.string.ksnet_timeout).getBytes()); // 타임아웃


            if (m_hash.get("PayType") != null && new String(m_hash.get("PayType")).length() <= 1) {
                Log.d("test", "할부개월수 자리수안맞음 " + new String(m_hash.get("PayType")));
                if (new String(m_hash.get("PayType")).length() == 0) {
                    m_hash.put("PayType", "00".getBytes());
                } else {
                    m_hash.put("PayType", ("0" + new String(m_hash.get("PayType"))).getBytes());
                }
                Log.d("test", "할부개월수 자리수안맞음 " + new String(m_hash.get("PayType")));
            }

            adminInfo = new AdminInfo(m_hash);

            if (new String(adminInfo.getTelegramType()).equals("0200")) {   //==================================== 승인
                isCancel = false;

                sendPush(true);


            } else if (new String(adminInfo.getTelegramType()).equals("0420")) {   //==================================== 취소
                isCancel = true;

                sendPush(true);

            }

        } else {
            //결제요청 paymentRequest


            if (print_flag.equals("false")) {
                m_hash = (HashMap<String, byte[]>) getIntent().getSerializableExtra("AdminInfo_Hash");

                m_hash.put("ksnet_server_ip", getString(R.string.ksnet_server_ip).getBytes()); // 접속 서버 아이피
                m_hash.put("ksnet_server_port", getString(R.string.ksnet_server_port).getBytes()); // 접속 서버 포트
                m_hash.put("ksnet_telegrametype", getString(R.string.ksnet_telegrametype).getBytes()); // Telegrame Type
                m_hash.put("ksnet_timeout", getString(R.string.ksnet_timeout).getBytes()); // 타임아웃


                if (m_hash.get("PayType") != null && new String(m_hash.get("PayType")).length() <= 1) {
                    Log.d("test", "할부개월수 자리수안맞음 " + new String(m_hash.get("PayType")));
                    if (new String(m_hash.get("PayType")).length() == 0) {
                        m_hash.put("PayType", "00".getBytes());
                    } else {
                        m_hash.put("PayType", ("0" + new String(m_hash.get("PayType"))).getBytes());
                    }
                    Log.d("test", "할부개월수 자리수안맞음 " + new String(m_hash.get("PayType")));
                }

                adminInfo = new AdminInfo(m_hash);
                // trackId 가맹점거래번호
//            if (!m_hash.containsKey("TrackId") || m_hash.get("TrackId") == null) {
//                Toasty.error(getApplicationContext(), "TrackId 값이 존재하지 않습니다.", Toast.LENGTH_SHORT, true).show();
//                setResult(RESULT_CANCELED, new Intent());
//                finish();
//                return;
//            }
//            if (!m_hash.containsKey("Van") || m_hash.get("Van") == null) {
//                Toasty.error(getApplicationContext(), "Van 값이 존재하지 않습니다.", Toast.LENGTH_SHORT, true).show();
//                setResult(RESULT_CANCELED, new Intent());
//                finish();
//                return;
//            }
//            if (!m_hash.containsKey("VanId") || m_hash.get("VanId") == null) {
//                Toasty.error(getApplicationContext(), "VanId 값이 존재하지 않습니다.", Toast.LENGTH_SHORT, true).show();
//                setResult(RESULT_CANCELED, new Intent());
//                finish();
//                return;
//            }

//                ksnetresp.data.put("TrackId", new String(m_hash.get("TrackId")));
//                resp.data.put("TrackId", new String(m_hash.get("TrackId")));
//                ksnetresp.data.put("Van", new String(m_hash.get("Van")));
//                resp.data.put("Van", new String(m_hash.get("Van")));
//                ksnetresp.data.put("VanId", new String(m_hash.get("VanId")));
//                resp.data.put("VanId", new String(m_hash.get("VanId")));

                if (new String(adminInfo.getTelegramType()).equals("0200")) {   //==================================== 승인
                    isCancel = false;

                    resp.data.put("installment", new String(adminInfo.getPayType()).trim());
                    sendHandlerMessage(TOKEN);

//                    sendPush();
//                sendCheckApprove(new String(adminInfo.getTotalAmount()), new String(adminInfo.getPayType()));


                    if (m_hash.containsKey("payerAddr")) {
                        payerAddr = new String(m_hash.get("payerAddr"));
                        resp.data.put("payerAddr", payerAddr);
                    }
                    if (m_hash.containsKey("prodName")) {
                        prodName = new String(m_hash.get("prodName"));
                        resp.data.put("prodName", prodName);
                    }
                    if (m_hash.containsKey("payerName")) {
                        payerName = new String(m_hash.get("payerName"));
                        resp.data.put("payerName", payerName);
                    }
                    if (m_hash.containsKey("payerEmail")) {
                        payerEmail = new String(m_hash.get("payerEmail"));
                        resp.data.put("payerEmail", payerEmail);
                    }
                    if (m_hash.containsKey("payerTel")) {
                        payerTel = new String(m_hash.get("payerTel"));
                        resp.data.put("payerTel", payerTel);
                    }


                } else if (new String(adminInfo.getTelegramType()).equals("0420")) {   //==================================== 취소
                    isCancel = true;


                    // trackId 가맹점거래번호
                    if ((!m_hash.containsKey("trxId") || m_hash.get("trxId") == null || m_hash.get("trxId").length==0) && (!m_hash.containsKey("TrxId") || m_hash.get("TrxId") == null || m_hash.get("TrxId").length==0)) {
                        Toasty.error(getApplicationContext(), "TrxId 값이 존재하지 않습니다.", Toast.LENGTH_SHORT, true).show();
                        return_data.putExtra("action", action);
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", "TrxId 값이 존재하지 않습니다");
                        setResult(RESULT_OK, return_data);
                        finish();
                        return;
                    }

                    if (m_hash.containsKey("trxId")) {
                        ksnetresp.data.put("trxId", new String(m_hash.get("trxId")));
                        resp.data.put("trxId", new String(m_hash.get("trxId")));
                    } else if (m_hash.containsKey("TrxId")) {
                        ksnetresp.data.put("trxId", new String(m_hash.get("TrxId")));
                        resp.data.put("trxId", new String(m_hash.get("TrxId")));
                        m_hash.put("trxId", m_hash.get("TrxId"));
                    }
                    if (m_hash.containsKey("TotalAmount")) {
                        amount = new String(m_hash.get("TotalAmount"));
                    }

                    checkTrxId();
//                    sendHandlerMessage(TOKEN);
//                    sendPush();

                }
            } else {
                BTPrinter();
            /*
            Intent serverIntent = new Intent(WebCheckActivity.this, DeviceListDialogtActivity.class);

            serverIntent.putExtra("name", "BTP111");
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            */
            }
//        KSNETStatus.BT_PRN_ADDRESS = SunUtil.getAppPreferences(WebCheckActivity.this, "bt_address");
//          BTPrinter();

        }
    }

    private void init(){
        //fcm구독
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        LOG.w( "Fetching FCM registration token failed" + task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    LOG.d("token: "+token);

                    SharedPreferenceUtil.putData(WebCheckActivity.this, Constants.KEY_TOKEN,token);
                    Constants.TOKEN = token;

                });

        String lastVersion = SharedPreferenceUtil.getVersion(this);
        LOG.w("lastVersion: "+lastVersion);
        if(!BuildConfig.VERSION_NAME.equals(lastVersion)) {
//            Set<String> topicList = SharedPreferenceUtil.getSetStringData(this, Constants.KEY_TOPIC_LIST);
//            if (topicList != null) {
//                for (String topic : topicList)
//                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
//                SharedPreferenceUtil.removeData(this, Constants.KEY_TOPIC_LIST);
//            }
            if(lastVersion !=null && !lastVersion.equals("0")){
                LOG.w("lastVersion unsubscribe ");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(lastVersion);
            }

            FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.VERSION_NAME)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            LOG.w("subscribe "+BuildConfig.VERSION_NAME);
                            SharedPreferenceUtil.putVersion(this);
//                            Set<String> topicSet = new HashSet<>();
//                            topicSet.add(BuildConfig.VERSION_NAME);
//                            SharedPreferenceUtil.putSetStringData(this,Constants.KEY_TOPIC_LIST,topicSet);
                        }
                    });
        }
    }


    private void showDialog() {
        if (sweetAlertDialog != null && !isFinishing()) {
            sweetAlertDialog.show();
        }
    }

    /**
     * 개발중 앱이 죽을때 리포팅을 위하여 화면에 보여준다.
     */
    private void registerErrorHandlerForDebuging() {

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Intent intent = new Intent(WebCheckActivity.this, ErrorActivity.class);
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


    public boolean directPaymentParameterCheck(String parameter, String key) {
        if (parameter == null || parameter.length() == 0) {
            return_data.putExtra("action", "sendDirectPayment");
            return_data.putExtra("isSuccess", false);
            return_data.putExtra("resultMsg", key + "값이 누락되었습니다");
            setResult(RESULT_OK, return_data);
            finish();
            return false;
        }
        return true;
    }


    public void sendHandlerMessage(int menu_state) {
        menu_state = menu_state;
        Message m = mHandler.obtainMessage(menu_state);
        mHandler.sendMessageDelayed(m, 100 * 1);
    }


    public void sendHandlerMessage(int menu_state, int delay) {
        menu_state = menu_state;
        Message m = mHandler.obtainMessage(menu_state);
        mHandler.sendMessageDelayed(m, delay);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        //Handler events that received from UART service
        public void handleMessage(Message msg) {
            Log.d("tag", "Handler " + msg.what + "/" + msg.getData());
            switch (msg.what) {
                case APPROVE_PRINTER_SEARCH: {
                    if (BluetoothAdapter.checkBluetoothAddress(KSNETStatus.BT_PRN_ADDRESS)) {
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(KSNETStatus.BT_PRN_ADDRESS);
                        // Attempt to connect to the device
                        mService.connect(device);
                    }

                    Message m = mHandler.obtainMessage(APPROVE_PRINTER);
                    mHandler.sendMessageDelayed(m, 500 * 1);

                    /*
                    BluetoothDevice device = mBluetoothAdapter
                            .getRemoteDevice("00:19:5D:25:EB:A1");
                    // Attempt to connect to the device
                    try {
                        mService.connect(device);
                    } catch ( Exception e) {e.printStackTrace();}
                    Toasty.info(WebCheckActivity.this, "프린터를 연결하고 있습니다", Toast.LENGTH_LONG, true).show();
                    // custom_toast("프린터 연결중입니다......");
                    */
                }
                break;
                case APPROVE_PRINTER: {
                    if (print_flag.equals("yes")) {
                        print();
                    }
                    sendHandlerMessage(APPROVE_PRINTER_CLOSE, 1000 * 2);
                }
                break;
                case APPROVE_PRINTER_CLOSE: {
                    return_data.putExtra("resultCd", resp.resultCd);
                    return_data.putExtra("resultMsg", resp.resultMsg);
                    return_data.putExtra("resultData", resp.toJsonString());
                    setResult(RESULT_OK, return_data);
                    finish();
                }
                break;

                case TOKEN: {
                    sendToken();
                }
                break;
                case APPROVE_BEFORE: {
                    if(adminInfo==null || adminInfo.getTotalAmount() == null || adminInfo.getPayType() == null){
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", "금액이나 할부개월수가 존재하지 않습니다.");
                        setResult(RESULT_OK, return_data);
                        finish();
                        return;
                    }


                    sendCheckApprove(new String(adminInfo.getTotalAmount()), new String(adminInfo.getPayType()));
                }
                break;
                case APPROVE_AFTER: {

                    sendApproveComplete();
                    if (isCancel)
                        resp.data.put("type", "취소");
                    sendSMS();
                }
                break;

                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   // BT Print Connect
                            KSNETStatus.BT_PRN_FLAG = true;
                            SunUtil.setAppPreferences(WebCheckActivity.this, "bt_address", KSNETStatus.BT_PRN_ADDRESS);
                            System.out.println("print_flag : " + print_flag);
                            if (print_flag.equals("yes")) {
                                print();
                            }

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
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toasty.info(getApplicationContext(), mConnectedDeviceName + "접속 중입니다", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    // 이곳에 프린터가 끊어진 곳입니다 .
                    break;
                case MESSAGE_CONNECTION_LOST:
                    KSNETStatus.BT_PRN_FLAG = false;
                    Toasty.error(getApplicationContext(), "프린터기와 연결이 끊어졌습니다", Toast.LENGTH_SHORT, true).show();
                    break;
                case MESSAGE_UNABLE_CONNECT:     //
                    KSNETStatus.BT_PRN_FLAG = false;
                    KSNETStatus.BT_PRN_ADDRESS = null;
                    Toasty.error(getApplicationContext(), "프린터기와 연결을 할 수 없습니다", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };


    //token조회이후
    public void cancelDirectPayment() {

        HashMap<String, String> data = new HashMap<>();
        data.put("trackId", resp.data.get("trackId"));
        data.put("trxType", "ONTR");
        data.put("Amount", amount);
        data.put("rootTrxId", resp.data.get("trxId"));

        HashMap<String, Object> refundMap = new HashMap<>();
        refundMap.put("refund", data);


        mAPIDirectService.cancelDirectPayment(resp.data.get("payKey"), refundMap).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {


                        /**
                         *
                         * {
                         *       "result": {
                         *         "resultCd": "0000",
                         *         "resultMsg": "정상",
                         *         "advanceMsg": "정상취소",
                         *         "create": "20190910161756"
                         *       },
                         *       "refund": {
                         *         "rootTrxId": "T190910002257",
                         *         "rootTrackId": "AXD_1568099851515",
                         *         "rootTrxDay": "20190910",
                         *         "authCd": "30022661",
                         *         "trxId": "T190910002258",
                         *         "trxType": "ONTR",
                         *         "tmnId": "test0005",
                         *         "trackId": "AXD_1568099851515",
                         *         "amount": 1004,
                         *         "udf1": "",
                         *         "udf2": ""
                         *       }
                         *     }
                         *
                         */

                        String responseData = new String(response.body().bytes());
//                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
//                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

                        JSONObject json = new JSONObject(responseData);
                        JSONObject refund = json.getJSONObject("refund");

                        if ("0000".equals(json.getJSONObject("result").getString("resultCd"))) {

                            resp.data.put("rootTrxId", refund.getString("rootTrxId"));
                            resp.data.put("trxId", refund.getString("trxId"));
                            resp.data.put("rootTrackId", refund.getString("rootTrackId"));
                            resp.data.put("trackId", refund.getString("trackId"));
                            resp.data.put("tmnID", refund.getString("tmnId"));
                            resp.data.put("type", "취소");
                            resp.data.put("authCd", refund.getString("authCd"));
                            resp.data.put("TotalAmount", refund.getString("amount"));
                            resp.data.put("regDate", refund.getString("transactionDate").substring(2));
                            resp.data.put("Classification", "");
                            resp.data.put("Status", "O");
                            resp.data.put("Authdate", refund.getString("transactionDate").substring(2));
                            resp.data.put("AuthNum", refund.getString("authCd"));
                            resp.data.put("TelegramType", "0430");
                            resp.data.put("processingCd", "1004");
                            resp.data.put("Message2", "OK: " + refund.getString("authCd"));

                            mAPIDirectService.getDirectPayment(resp.data.get("payKey"), refund.getString("rootTrxId")).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        if (response.isSuccessful()) {

                                            /**
                                             * 조회..
                                             * {
                                             *   "result": {
                                             *     "resultCd": "0000",
                                             *     "resultMsg": "정상",
                                             *     "advanceMsg": "정상승인",
                                             *     "create": "20190910161747"
                                             *   },
                                             *   "pay": {
                                             *     "authCd": "30022661",
                                             *     "card": {
                                             *       "cardId": "card_cb3c-cf9284-1ef-baf18",
                                             *       "installment": 0,
                                             *       "bin": "540926",
                                             *       "last4": "0012",
                                             *       "issuer": "국민",
                                             *       "cardType": "신용",
                                             *       "acquirer": "국민",
                                             *       "issuerCode": "02",
                                             *       "acquirerCode": "02"
                                             *     },
                                             *     "products": [
                                             *       {
                                             *         "prodId": "",
                                             *         "name": "test",
                                             *         "qty": 0,
                                             *         "price": 0,
                                             *         "desc": ""
                                             *       }
                                             *     ],
                                             *     "trxId": "T190910002257",
                                             *     "trxType": "ONTR",
                                             *     "tmnId": "test0005",
                                             *     "trackId": "AXD_1568099851515",
                                             *     "amount": 1004,
                                             *     "udf1": "",
                                             *     "udf2": ""
                                             *   }
                                             * }
                                             */
                                            String responseData = new String(response.body().bytes());
//                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
//                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

                                            JSONObject json = new JSONObject(responseData);
                                            JSONObject cardJSON = json.getJSONObject("pay").getJSONObject("card");
                                            resp.data.put("PurchaseName", cardJSON.getString("acquirer"));
                                            resp.data.put("number", cardJSON.getString("bin") + "******" + cardJSON.getString("last4"));
//                                        resp.data.put("authCd", json.getJSONObject("pay").getString("authCd"));
                                            resp.data.put("TotalAmount", json.getJSONObject("pay").getString("amount"));
                                            resp.data.put("installment", cardJSON.getString("installment").length() == 1 ? "0" + cardJSON.getString("installment") : cardJSON.getString("installment"));
                                            resp.data.put("CardNo", cardJSON.getString("bin") + "******" + cardJSON.getString("last4"));
                                            resp.data.put("Classification", "");
                                            resp.data.put("Status", "O");
                                            resp.data.put("CardName", cardJSON.getString("issuer"));
                                            resp.data.put("processingCd", "1004");
                                            resp.data.put("PurchaseCode", cardJSON.getString("acquirerCode"));
                                            resp.data.put("issuer", cardJSON.getString("issuer"));
                                            resp.data.put("IssueCode", cardJSON.getString("issuerCode"));

                                            sendSMS();

                                            realm.executeTransaction(realm -> {
                                                PaymentInfo paymentInfo = realm.createObject(PaymentInfo.class);

                                                paymentInfo.delngSe = "승인취소";
                                                paymentInfo.cardCashSe = "CARD";
                                                try {
                                                    paymentInfo.cardNo = cardJSON.getString("bin") + "******" + cardJSON.getString("last4");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.issuCmpnyNm =cardJSON.getString("issuer");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.puchasCmpnyNm = cardJSON.getString("acquirer");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.instlmtMonth = cardJSON.getString("installment").length() == 1 ? "0" + cardJSON.getString("installment") : cardJSON.getString("installment");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.splpc =json.getJSONObject("pay").getString("amount");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.confmNo =  refund.getString("authCd");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.regDate = refund.getString("transactionDate").substring(2);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.issuCmpnyCode = cardJSON.getString("issuerCode");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.puchasCmpnyCode = cardJSON.getString("acquirerCode");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    paymentInfo.setTrxId(refund.getString("trxId"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    paymentInfo.trackId = refund.getString("trackId");
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                paymentInfo.mchtName = resp.geKeyValye("name");
                                                paymentInfo.mchtAddr = resp.geKeyValye("addr");
                                                paymentInfo.mchtBizNum = resp.geKeyValye("identity");

                                            });


//                        JSONObject responseData = response.body();
                                            Log.d("tag", "test responseData: " + responseData);

                                            resp.resultMsg = json.getJSONObject("result").getString("advanceMsg");

                                            return_data.putExtra("action", "cancelDirectPayment");
                                            return_data.putExtra("isSuccess", true);
                                            return_data.putExtra("resultData", resp.data);
                                            setResult(RESULT_OK, return_data);
//                                            finish();

                                            if (sweetAlertDialog != null) {
//                                                sweetAlertDialog.setTitle(R.string.error_msg);
                                                sweetAlertDialog.setTitleText(resp.resultMsg);
                                                if (!sweetAlertDialog.isShowing())
                                                    showDialog();
                                            }

                                            new Handler().postDelayed(() -> {
                                                try {
                                                    StringBuilder sb = new StringBuilder();
                                                    sb.append("winpay://refund_check?action=refund");
                                                    sb.append("&resultCode=S0001");
                                                    sb.append("&payType=CARD");
                                                    sb.append("&authNum="+refund.getString("authCd"));
                                                    sb.append("&authDate="+refund.getString("transactionDate").substring(2,8));
                                                    sb.append("&authTime="+refund.getString("transactionDate").substring(8));
                                                    sb.append("&isuuerName="+URLEncoder.encode(cardJSON.getString("issuer"), "UTF-8"));
                                                    sb.append("&issuerCode="+cardJSON.getString("issuerCode"));
                                                    sb.append("&trxId="+refund.getString("trxId"));
                                                    sb.append("&purchaseCode="+cardJSON.getString("acquirerCode"));
                                                    sb.append("&purchaseName="+URLEncoder.encode(cardJSON.getString("acquirer"), "UTF-8"));
                                                    sb.append("&installment="+ (cardJSON.getString("installment").length() == 1 ? "0" + cardJSON.getString("installment") : cardJSON.getString("installment")));
                                                    sb.append("&cardNo="+ cardJSON.getString("bin") + "******" + cardJSON.getString("last4"));
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString())));
                                                    LOG.w("url: "+sb.toString());
                                                } catch (ActivityNotFoundException e) {
                                                    LOG.w("액티비티 없음.");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    try {
                                                        JSONObject logJson = new JSONObject();
                                                        logJson.put("message", "수기결제 에러.");
                                                        logJson.put("error",e.getMessage());
                                                        logJson.put("data", getIntent().getExtras().toString());
                                                        logJson.put("response data", resp.data.toString());
                                                        logJson.put("uniqueID", uniqueID);
                                                        logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
                                                        logJson.put("winpay_version", Util.getVersion(WebCheckActivity.this,"co.shilla.winpay"));
                                                        logJson.put("os",Build.VERSION.SDK_INT + "");
                                                        logJson.put("model",Build.MODEL + "");
                                                        new NotiAsyncTask(WebCheckActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
                                                    } catch (Exception ee){}
                                                }
                                            }, 500);

                                            finish();

                                        } else {
                                            resp.resultMsg = new String(response.errorBody().bytes());

                                            return_data.putExtra("action", "cancelDirectPayment");
                                            return_data.putExtra("isSuccess", false);
                                            return_data.putExtra("resultMsg", resp.resultMsg);
//                                            Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK, return_data);
//                                            finish();

                                            if (sweetAlertDialog != null) {
//                                                sweetAlertDialog.setTitle(R.string.error_msg);
                                                sweetAlertDialog.setTitleText(resp.resultMsg);
                                                if (!sweetAlertDialog.isShowing())
                                                    showDialog();
                                            }
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 1000);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                        return_data.putExtra("action", "cancelDirectPayment");
                                        return_data.putExtra("isSuccess", false);
                                        return_data.putExtra("resultMsg", e.getMessage());
//                                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK, return_data);
//                                        finish();

                                        if (sweetAlertDialog != null) {
                                            sweetAlertDialog.setTitle(R.string.error_msg);
//                                            sweetAlertDialog.setTitleText(resp.resultMsg);
                                            if (!sweetAlertDialog.isShowing())
                                                showDialog();
                                        }
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1000);
                                    }

                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                    Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                                    t.getMessage();
                                    return_data.putExtra("action", "cancelDirectPayment");
                                    return_data.putExtra("isSuccess", false);
                                    return_data.putExtra("resultMsg", t.getMessage());
                                    setResult(RESULT_OK, return_data);

                                    if (sweetAlertDialog != null) {
                                        sweetAlertDialog.setTitle(R.string.error_msg);
//                                            sweetAlertDialog.setTitleText(resp.resultMsg);
                                        if (!sweetAlertDialog.isShowing())
                                            showDialog();
                                    }
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                }
                            });

//                        } else if ("9999".equals(json.getJSONObject("result").getString("resultCd"))) {
                        } else {
                            resp.resultMsg = json.getJSONObject("result").getString("advanceMsg");
                            return_data.putExtra("action", "cancelDirectPayment");
                            return_data.putExtra("isSuccess", false);
                            return_data.putExtra("resultMsg", resp.resultMsg);
//                            Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, return_data);
//                            finish();

                            if (sweetAlertDialog != null) {
//                                sweetAlertDialog.setTitle(R.string.error_msg);
                                sweetAlertDialog.setTitleText(resp.resultMsg);
                                if (!sweetAlertDialog.isShowing())
                                    showDialog();
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);

                        }

                    } else {
                        resp.resultMsg = new String(response.errorBody().bytes());

                        return_data.putExtra("action", "cancelDirectPayment");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", resp.resultMsg);
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, return_data);
//                        finish();
                        if (sweetAlertDialog != null) {
//                                sweetAlertDialog.setTitle(R.string.error_msg);
                            sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    resp.resultMsg = e.getMessage();
                    return_data.putExtra("action", "cancelDirectPayment");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", resp.resultMsg);
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, return_data);
//                    finish();

                    if (sweetAlertDialog != null) {
//                                sweetAlertDialog.setTitle(R.string.error_msg);
                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();

                resp.resultMsg = t.getMessage();
                return_data.putExtra("action", "paymentRequest");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", resp.resultMsg);
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, return_data);

                if (sweetAlertDialog != null) {
//                                sweetAlertDialog.setTitle(R.string.error_msg);
                    sweetAlertDialog.setTitleText(resp.resultMsg);
                    if (!sweetAlertDialog.isShowing())
                        showDialog();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }

        });
    }

    public void getPayKey() {
        if (sweetAlertDialog != null) {
            sweetAlertDialog.setTitleText("수기결제 승인중입니다.");
            if (!sweetAlertDialog.isShowing())
                showDialog();
        }

        Request req = new Request();
        req.data.put("log_uniqueId", uniqueID);
        req.data.put("payment", "수기결제");
        req.data.put("tmnId", resp.data.get("tmnId"));
        req.data.put("serial", resp.data.get("serial"));
        req.data.put("mchtId", resp.data.get("mchtId"));
        req.data.put("appId", "");
        req.data.put("version", BuildConfig.VERSION_NAME);
        req.data.put("telNo", "");

//        req.data.put("appId", resp.data.get("appId"));
//        req.data.put("version", resp.data.get("version"));
//        req.data.put("telNo", resp.data.get("telNo"));

        mAPIService.getStringToken("", req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

                        if (resp.data.get("apiMaxInstall") != null) {
                            //할부개월수 제한
                            try {
                                int reqInstallment = Integer.parseInt(new String(adminInfo.getPayType()));

                                int apiMaxInstall = Integer.parseInt(resp.data.get("apiMaxInstall"));

                                if (reqInstallment > apiMaxInstall) {
                                    return_data.putExtra("action", "getToken");
                                    return_data.putExtra("isSuccess", false);
                                    return_data.putExtra("resultMsg", "최대 할부개월수는 " + apiMaxInstall + "개월 입니다.");
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK, return_data);
//                        finish();

                                    try {
                                        if (sweetAlertDialog != null) {
//                            sweetAlertDialog.setTitle(R.string.error_msg);
                                            sweetAlertDialog.setTitleText("최대 할부개월수는 " + apiMaxInstall + "개월 입니다.");
                                            if (!sweetAlertDialog.isShowing())
                                                showDialog();
                                        }
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1000);
                                    } catch (Exception e) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1000);
                                    }

                                    return;
                                }

                            } catch (Exception e) {
                            }


                            Intent intent = new Intent(WebCheckActivity.this, DirectPaymentActivity.class);
                            intent.putExtras(getIntent());
                            intent.putExtra("apiMaxInstall",resp.data.get("apiMaxInstall"));
                            startActivityForResult(intent, REQUEST_CODE_DIRECT_PAYMENT);

                            return;
                        }    //할부개월수 제한

                        Intent intent = new Intent(WebCheckActivity.this, DirectPaymentActivity.class);
                        intent.putExtras(getIntent());
                        intent.putExtra("apiMaxInstall","0");
                        startActivityForResult(intent, REQUEST_CODE_DIRECT_PAYMENT);

                    } else {
                        return_data.putExtra("action", "getToken");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", new String(response.errorBody().bytes()));
                        setResult(RESULT_OK, return_data);
//                                        finish();

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.setTitle(new String(response.errorBody().bytes()));
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    return_data.putExtra("action", "getToken");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", e.getMessage());
                    setResult(RESULT_OK, return_data);
//                                    finish();

                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(e.getMessage());
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                return_data.putExtra("action", "getToken");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", t.getMessage());
                setResult(RESULT_OK, return_data);
//                                finish();

                if (sweetAlertDialog != null) {
                    sweetAlertDialog.setTitle(t.getMessage());
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                    if (!sweetAlertDialog.isShowing())
                        showDialog();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });
    }

    //token조회이후
    public void sendDirectPayment() {
        DirectPayment pay = new DirectPayment();
        pay.pay.put("trxType", "ONTR");
//        pay.pay.put("tmnId", resp.data.get("tmnId"));
        pay.pay.put("trackId", resp.data.get("trackId"));
        pay.pay.put("amount", amount);
        pay.pay.put("payerName", payerName);
        pay.pay.put("payerEmail", payerEmail);
        pay.pay.put("payerTel", payerTel);
        pay.pay.put("udf1", "");
        pay.pay.put("udf2", "");
        ArrayList products = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("qty", 1);
        map.put("price", amount);
        map.put("desc", payerAddr);

        products.add(map);
        pay.pay.put("products", products);

        final HashMap<String, Object> card = new HashMap<>();
        card.put("number", cardNum);
        card.put("expiry", expiry_year + "" + expiry_month);
        card.put("installment", installment);
        pay.pay.put("card", card);

        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("cardAuth", "false");//
        metadata.put("authPw", ""); //카드비밀번호 두자리
        metadata.put("authDob", "");
        pay.pay.put("metadata", metadata);


        mAPIDirectService.sendDirectPayment(resp.data.get("payKey"), pay).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent return_data = new Intent();
                try {
                    if (response.isSuccessful()) {
                        /**
                         *{
                         *       "result": {
                         *         "resultCd": "0000",
                         *         "resultMsg": "정상",
                         *         "advanceMsg": "정상승인",
                         *         "create": "20190910114932"
                         *       },
                         *       "pay": {
                         *         "authCd": "30022560",
                         *         "card": {
                         *           "cardId": "card_7117-aab3ed-956-ac8bd",
                         *           "installment": 0,
                         *           "bin": "540926",
                         *           "last4": "0012",
                         *           "issuer": "국민",
                         *           "cardType": "신용",
                         *           "acquirer": "국민",
                         *           "issuerCode": "02",
                         *           "acquirerCode": "02"
                         *         },
                         *         "products": [
                         *           {
                         *             "prodId": "",
                         *             "name": "test",
                         *             "qty": 0,
                         *             "price": 0,
                         *             "desc": ""
                         *           }
                         *         ],
                         *         "trxId": "T190910002233",
                         *         "trxType": "ONTR",
                         *         "tmnId": "test0005",
                         *         "trackId": "AXD_1568083757709",
                         *         "amount": 1004,
                         *         "udf1": "",
                         *         "udf2": ""
                         *       }
                         *     }
                         *
                         *   String name = resp.data.get("name");
                         *         String issuer = resp.data.get("PurchaseName");
                         *         String cardNumber = resp.data.get("number");
                         *         if(cardNumber==null || cardNumber.length()==0){
                         *             cardNumber = resp.data.get("bin");
                         *         }
                         *
                         *         String trxResult = resp.data.get("type");
                         *         String authCd = resp.data.get("authCd");
                         *         String amount = resp.data.get("processingCd");
                         *         String installment = resp.data.get("installment");
                         *         String regDate = resp.data.get("regDate");
                         */



                        String responseData = new String(response.body().bytes());
//                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
//                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

                        JSONObject json = new JSONObject(responseData);
                        if ("0000".equals(json.getJSONObject("result").getString("resultCd"))) {
                            JSONObject cardJSON = json.getJSONObject("pay").getJSONObject("card");
                            resp.data.put("PurchaseName", cardJSON.getString("acquirer"));
                            resp.data.put("number", cardJSON.getString("bin") + "******" + cardJSON.getString("last4"));
                            resp.data.put("type", "승인");
                            resp.data.put("authCd", json.getJSONObject("pay").getString("authCd"));
                            resp.data.put("AuthNum", json.getJSONObject("pay").getString("authCd"));
                            resp.data.put("TotalAmount", json.getJSONObject("pay").getString("amount"));
                            resp.data.put("installment", cardJSON.getString("installment").length() == 1 ? "0" + cardJSON.getString("installment") : cardJSON.getString("installment"));
                            resp.data.put("regDate", json.getJSONObject("pay").getString("transactionDate").substring(2));
                            resp.data.put("CardNo", cardJSON.getString("bin") + "******" + cardJSON.getString("last4"));
                            resp.data.put("Message2", "OK: " + json.getJSONObject("pay").getString("authCd"));
                            resp.data.put("Classification", "");
                            resp.data.put("Status", "O");
                            resp.data.put("Authdate", json.getJSONObject("pay").getString("transactionDate").substring(2));
                            resp.data.put("trxId", json.getJSONObject("pay").getString("trxId"));
                            resp.data.put("TelegramType", "0210");
                            resp.data.put("CardName", cardJSON.getString("issuer"));
                            resp.data.put("processingCd", "1004");
                            resp.data.put("PurchaseCode", cardJSON.getString("acquirerCode"));
                            resp.data.put("issuer", cardJSON.getString("issuer"));
                            resp.data.put("IssueCode", cardJSON.getString("issuerCode"));


                            realm.executeTransaction(realm -> {
                                PaymentInfo paymentInfo = realm.createObject(PaymentInfo.class);
//                        tmnId;
//                        serial;
//                        appId;
//                        mchtId;
//                        version;
//                        telNo;

                                paymentInfo.delngSe = "승인";
                                paymentInfo.cardCashSe = "CARD";
                                try {
                                    paymentInfo.cardNo = cardJSON.getString("bin") + "******" + cardJSON.getString("last4");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.issuCmpnyNm =cardJSON.getString("issuer");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.puchasCmpnyNm = cardJSON.getString("acquirer");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.instlmtMonth = cardJSON.getString("installment").length() == 1 ? "0" + cardJSON.getString("installment") : cardJSON.getString("installment");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.splpc =json.getJSONObject("pay").getString("amount");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.confmNo =  json.getJSONObject("pay").getString("authCd");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.regDate = json.getJSONObject("pay").getString("transactionDate").substring(2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.issuCmpnyCode = cardJSON.getString("issuerCode");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.puchasCmpnyCode = cardJSON.getString("acquirerCode");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    paymentInfo.setTrxId(json.getJSONObject("pay").getString("trxId"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    paymentInfo.trackId = json.getJSONObject("pay").getString("trackId");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                paymentInfo.mchtName = resp.geKeyValye("name");
                                paymentInfo.mchtAddr = resp.geKeyValye("addr");
                                paymentInfo.mchtBizNum = resp.geKeyValye("identity");

                            });

//                        sendHandlerMessage(APPROVE_BEFORE);
//                        JSONObject responseData = response.body();
                            Log.d("tag", "test responseData: " + responseData);

                            if (sweetAlertDialog != null) {
                                sweetAlertDialog.setTitleText("수기결제 승인완료 되었습니다.");
                                if (!sweetAlertDialog.isShowing())
                                    showDialog();
                            }

                            try {
                                sendSMS();
                                resp.resultMsg = json.getJSONObject("result").getString("advanceMsg");
                            }catch (Exception e){}


                            return_data.putExtra("action", "sendDirectPayment");
                            return_data.putExtra("isSuccess", true);
                            return_data.putExtra("resultData", resp.data);
                            return_data.putExtra("resultMsg", resp.resultMsg);
                            setResult(RESULT_OK, return_data);

                            try {
                                JSONObject logJson = new JSONObject();
                                logJson.put("message", "수기결제완료.");
                                logJson.put("data", getIntent().getExtras().toString());
                                logJson.put("response data", resp.data.toString());
                                logJson.put("uniqueID", uniqueID);
                                logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
                                logJson.put("winpay_version", Util.getVersion(WebCheckActivity.this,"co.shilla.winpay"));
                                logJson.put("os",Build.VERSION.SDK_INT + "");
                                logJson.put("model",Build.MODEL + "");
                                new NotiAsyncTask(WebCheckActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
                            } catch (Exception e){}



                            new Handler().postDelayed(() -> {
                                try {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("winpay://payment_check?action=payment");
                                    sb.append("&resultCode=S0001");
                                    sb.append("&payType=CARD");
                                    sb.append("&authNum="+json.getJSONObject("pay").getString("authCd"));
                                    sb.append("&authDate="+json.getJSONObject("pay").getString("transactionDate").substring(2,8));
                                    sb.append("&authTime="+json.getJSONObject("pay").getString("transactionDate").substring(8));
                                    sb.append("&isuuerName="+URLEncoder.encode(cardJSON.getString("issuer"), "UTF-8"));
                                    sb.append("&issuerCode="+cardJSON.getString("issuerCode"));
                                    sb.append("&trxId="+json.getJSONObject("pay").getString("trxId"));
                                    sb.append("&purchaseCode="+cardJSON.getString("acquirerCode"));
                                    sb.append("&purchaseName="+URLEncoder.encode(cardJSON.getString("acquirer"), "UTF-8"));
                                    sb.append("&installment="+ (cardJSON.getString("installment").length() == 1 ? "0" + cardJSON.getString("installment") : cardJSON.getString("installment")));
                                    sb.append("&cardNo="+ cardJSON.getString("bin") + "******" + cardJSON.getString("last4"));
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString())));
                                    LOG.w("url: "+sb.toString());
                                } catch (ActivityNotFoundException e) {
                                    LOG.w("액티비티 없음.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    try {
                                        JSONObject logJson = new JSONObject();
                                        logJson.put("message", "수기결제 에러.");
                                        logJson.put("error",e.getMessage());
                                        logJson.put("data", getIntent().getExtras().toString());
                                        logJson.put("response data", resp.data.toString());
                                        logJson.put("uniqueID", uniqueID);
                                        logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
                                        logJson.put("winpay_version", Util.getVersion(WebCheckActivity.this,"co.shilla.winpay"));
                                        logJson.put("os",Build.VERSION.SDK_INT + "");
                                        logJson.put("model",Build.MODEL + "");
                                        new NotiAsyncTask(WebCheckActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
                                    } catch (Exception ee){}
                                }

                            },500);

                            finish();

//                        } else if ("9999".equals(json.getJSONObject("result").getString("resultCd"))) {
                        } else {
                            return_data.putExtra("action", "sendDirectPayment");
                            return_data.putExtra("isSuccess", false);
                            return_data.putExtra("resultMsg", json.getJSONObject("result").getString("advanceMsg"));
//                            Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, return_data);

                            if (sweetAlertDialog != null) {
                                sweetAlertDialog.setTitleText(json.getJSONObject("result").getString("advanceMsg"));
                                if (!sweetAlertDialog.isShowing())
                                    showDialog();
                            }

                            finish();

                        }

                    } else {
                        resp.resultMsg = new String(response.errorBody().bytes());

                        return_data.putExtra("action", "sendDirectPayment");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", resp.resultMsg);

                        setResult(RESULT_OK, return_data);

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.setTitle(R.string.error_msg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    try {
                        JSONObject logJson = new JSONObject();
                        logJson.put("message", "수기결제 에러.");
                        logJson.put("error",e.getMessage());
                        logJson.put("data", getIntent().getExtras().toString());
                        logJson.put("response data", resp.data.toString());
                        logJson.put("uniqueID", uniqueID);
                        logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
                        logJson.put("os",Build.VERSION.SDK_INT + "");
                        logJson.put("model",Build.MODEL + "");
                        new NotiAsyncTask(WebCheckActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
                    } catch (Exception ee){}

                    return_data.putExtra("action", "sendDirectPayment");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", e.getMessage());
                    setResult(RESULT_OK, return_data);

                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(R.string.error_msg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
                return_data.putExtra("action", "sendDirectPayment");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", t.getMessage());
                setResult(RESULT_OK, return_data);

                if (sweetAlertDialog != null) {
                    sweetAlertDialog.setTitle(t.getMessage());
                    if (!sweetAlertDialog.isShowing())
                        showDialog();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }

        });
    }

    public void checkTrxId() {
        Request req = new Request();
        req.data.put("log_uniqueId", uniqueID);
        req.data.put("payment", "거래종류체크");
        req.data.put("tmnId", resp.data.get("tmnId"));
        req.data.put("serial", resp.data.get("serial"));
        req.data.put("mchtId", resp.data.get("mchtId"));
        req.data.put("appId", "");
        req.data.put("version", BuildConfig.VERSION_NAME);
        req.data.put("telNo", "");

//        req.data.put("appId", resp.data.get("appId"));
//        req.data.put("version", resp.data.get("version"));
//        req.data.put("telNo", resp.data.get("telNo"));

        mAPIService.getStringToken("", req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        resp.setDataMap(GsonUtil.toJson(responseobj.data));


                        Request requestBody = new Request();
                        requestBody.data.put("trxId", resp.data.get("trxId"));


                        //수기인지 오프라인결제인지 체크
                        mAPIService.checkTrxId(resp.data.get("key"), requestBody).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        String responseData = new String(response.body().bytes());
                                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

                                        if ("ONTR".equals(resp.data.get("trxType"))) {
                                            if (sweetAlertDialog != null) {
//                                            sweetAlertDialog.setTitle(R.string.error_msg);
                                                sweetAlertDialog.setTitleText("수기결제 취소승인중입니다.");
                                                if (!sweetAlertDialog.isShowing())
                                                    showDialog();
                                            }

                                            refundDirectPayment();

                                        } else if ("MATR".equals(resp.data.get("trxType"))) {
                                            sendHandlerMessage(APPROVE_BEFORE);

                                        }

                                    } else {
                                        resp.resultMsg = new String(response.errorBody().bytes());

                                        return_data.putExtra("action", "getToken");
                                        return_data.putExtra("isSuccess", false);
                                        return_data.putExtra("resultMsg", resp.resultMsg);
//                                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK, return_data);
//                                        finish();

                                        if (sweetAlertDialog != null) {
//                                            sweetAlertDialog.setTitle(R.string.error_msg);
                                            sweetAlertDialog.setTitleText(resp.resultMsg);
                                            if (!sweetAlertDialog.isShowing())
                                                showDialog();
                                        }
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1000);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    return_data.putExtra("action", "getToken");
                                    return_data.putExtra("isSuccess", false);
                                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
//                                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK, return_data);
//                                    finish();

                                    if (sweetAlertDialog != null) {
                                        sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                                        if (!sweetAlertDialog.isShowing())
                                            showDialog();
                                    }
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                                t.getMessage();
                                return_data.putExtra("action", "getToken");
                                return_data.putExtra("isSuccess", false);
                                return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
//                                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, return_data);

                                if (sweetAlertDialog != null) {
                                    sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                                    if (!sweetAlertDialog.isShowing())
                                        showDialog();
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1000);
                            }
                        });


                    } else {
                        resp.resultMsg = new String(response.errorBody().bytes());

                        return_data.putExtra("action", "getToken");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", resp.resultMsg);
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, return_data);
//                        finish();

                        if (sweetAlertDialog != null) {
//                            sweetAlertDialog.setTitle(R.string.error_msg);
                            sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    return_data.putExtra("action", "getToken");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, return_data);
//                    finish();

                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
                return_data.putExtra("action", "getToken");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, return_data);

                if (sweetAlertDialog != null) {
                    sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                    if (!sweetAlertDialog.isShowing())
                        showDialog();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }

        });
    }

    public void refundDirectPayment() {
        Request req = new Request();
        req.data.put("payment", "수기취소");
        req.data.put("log_uniqueId", uniqueID);
        req.data.put("tmnId", resp.data.get("tmnId"));
        req.data.put("serial", resp.data.get("serial"));
        req.data.put("mchtId", resp.data.get("mchtId"));
        req.data.put("appId", "");
        req.data.put("version", BuildConfig.VERSION_NAME);
        req.data.put("telNo", "");

//        req.data.put("appId", resp.data.get("appId"));
//        req.data.put("version", resp.data.get("version"));
//        req.data.put("telNo", resp.data.get("telNo"));

        mAPIService.getStringToken("", req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        resp.setDataMap(GsonUtil.toJson(responseobj.data));


                        cancelDirectPayment();
                    } else {
                        resp.resultMsg = new String(response.errorBody().bytes());

                        return_data.putExtra("action", "getToken");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", resp.resultMsg);
                        setResult(RESULT_OK, return_data);
//                                        finish();

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    return_data.putExtra("action", "getToken");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                    setResult(RESULT_OK, return_data);
//                                    finish();
                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                return_data.putExtra("action", "getToken");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                setResult(RESULT_OK, return_data);
//                                finish();

                if (sweetAlertDialog != null) {
                    sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                    if (!sweetAlertDialog.isShowing())
                        showDialog();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });



    }

    public void sendToken() {
        Request req = new Request();
        req.data.put("payment", "일반결제");
        req.data.put("log_uniqueId", uniqueID);
        req.data.put("tmnId", resp.data.get("tmnId"));
        req.data.put("serial", resp.data.get("serial"));
        req.data.put("mchtId", resp.data.get("mchtId"));
        req.data.put("appId", "");
        req.data.put("version", BuildConfig.VERSION_NAME);
        req.data.put("telNo", "");

//        req.data.put("appId", resp.data.get("appId"));
//        req.data.put("version", resp.data.get("version"));
//        req.data.put("telNo", resp.data.get("telNo"));

        mAPIService.getStringToken("", req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

                        if (resp.data.get("apiMaxInstall") != null) {
                            //할부개월수 제한
                            try {
                                int reqInstallment = Integer.parseInt(new String(adminInfo.getPayType()));

                                int apiMaxInstall = Integer.parseInt(resp.data.get("apiMaxInstall"));

                                if (reqInstallment > apiMaxInstall) {
                                    return_data.putExtra("action", "getToken");
                                    return_data.putExtra("isSuccess", false);
                                    return_data.putExtra("resultMsg", "최대 할부개월수는 " + apiMaxInstall + "개월 입니다.");
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK, return_data);
//                        finish();

                                    try {
                                        if (sweetAlertDialog != null) {
//                            sweetAlertDialog.setTitle(R.string.error_msg);
                                            sweetAlertDialog.setTitleText("최대 할부개월수는 " + apiMaxInstall + "개월 입니다.");
                                            if (!sweetAlertDialog.isShowing())
                                                showDialog();
                                        }
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1000);
                                    } catch (Exception e) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1000);
                                    }

                                    return;
                                }

                            } catch (Exception e) {
                            }
                        }    //할부개월수 제한

                        sendHandlerMessage(APPROVE_BEFORE);
                    } else {
                        resp.resultMsg = new String(response.errorBody().bytes());

                        return_data.putExtra("action", "getToken");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", resp.resultMsg);
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, return_data);
//                        finish();

                        if (sweetAlertDialog != null) {
//                            sweetAlertDialog.setTitle(R.string.error_msg);
                            sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    return_data.putExtra("action", "getToken");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, return_data);
//                    finish();

                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
                return_data.putExtra("action", "getToken");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, return_data);

                if (sweetAlertDialog != null) {
                    sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                    if (!sweetAlertDialog.isShowing())
                        showDialog();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }

        });
    }

    public void sendCheckApprove(String amount, String installment) {
        Request req = new Request();
        req.data.put("amount", amount);
        req.data.put("installment", installment);


        if (isCancel) {
            req.data.put("trxId", resp.data.get("trxId"));
            mAPIService.getCheckCancelApprove(resp.data.get("key"), req).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            String responseData = new String(response.body().bytes());
                            responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                            resp.setDataMap(GsonUtil.toJson(responseobj.data));

                            /**
                             * {
                             *   "data": {
                             *     "result": "결제승인",
                             *     "van": "KSPAY1",
                             *     "vanId": "2006400005",
                             *     "trackId": "TX190829000193",
                             *     "secondKey": "DPT0A24555"
                             *   }
                             * }
                             */
                            String trmnlNo = responseobj.data.get("secondKey");
                            String van = responseobj.data.get("van");
                            String vanId = responseobj.data.get("vanId");
                            String trackId = responseobj.data.get("trackId");
                            if (resp.data.get("secondKey") != null) {
                                resp.data.put("DPTID", resp.data.get("secondKey"));
                                adminInfo.setDPTID(resp.data.get("secondKey").getBytes());
                                m_hash.put("DPTID", adminInfo.getDPTID());
                            }
                            if (BuildConfig.IS_DEVEL) {
                                if (resp.data.get("secondKey") != null) {
                                    resp.data.put("DPTID", "DPT0TEST03");
                                    adminInfo.setDPTID(resp.data.get("DPTID").getBytes());
                                    m_hash.put("DPTID", adminInfo.getDPTID());
                                }
                            }

                            if (van == null || van.length() == 0 || !van.contains("KSPAY") || trmnlNo == null || trmnlNo.length() == 0) {
                                return_data.putExtra("action", "paymentReady");
                                return_data.putExtra("isSuccess", false);
                                return_data.putExtra("resultMsg", getString(R.string.tmn_setting_error));
                                setResult(RESULT_OK, return_data);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1000);
                                return;
                            }

                            sendPush();

                        } else {
                            resp.resultMsg = new String(response.errorBody().bytes());

                            return_data.putExtra("action", "paymentReady");
                            return_data.putExtra("isSuccess", false);
                            return_data.putExtra("resultMsg", resp.resultMsg);
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, return_data);
//                        finish();

                            if (sweetAlertDialog != null) {
//                                sweetAlertDialog.setTitle(R.string.error_msg);
                        sweetAlertDialog.setTitleText(resp.resultMsg);
                                if (!sweetAlertDialog.isShowing())
                                    showDialog();
                            }
                            new Handler().postDelayed(() -> finish(), 1000);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return_data.putExtra("action", "paymentReady");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                        setResult(RESULT_OK, return_data);
//                    finish();

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
                    return_data.putExtra("action", "paymentReady");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                    setResult(RESULT_OK, return_data);
//                finish();

                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }

            });
        } else {
            mAPIService.getCheckApprove(resp.data.get("key"), req).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            String responseData = new String(response.body().bytes());
                            responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                            resp.setDataMap(GsonUtil.toJson(responseobj.data));

                            /**
                             * {
                             *   "data": {
                             *     "result": "결제승인",
                             *     "van": "KSPAY1",
                             *     "vanId": "2006400005",
                             *     "trackId": "TX190829000193",
                             *     "secondKey": "DPT0A24555"
                             *   }
                             * }
                             */
                            String dptId = responseobj.geKeyValye("secondKey");
                            String van = responseobj.geKeyValye("van");
                            String vanId = responseobj.geKeyValye("vanId");
                            String trackId = responseobj.geKeyValye("trackId");

                            if (van == null || van.length() == 0 || !van.contains("KSPAY") || dptId == null || dptId.length() == 0) {
                                return_data.putExtra("action", "paymentReady");
                                return_data.putExtra("isSuccess", false);
                                return_data.putExtra("resultMsg", getString(R.string.tmn_setting_error));
                                setResult(RESULT_OK, return_data);
                                finish();
                                return;
                            }

                            if (resp.data.get("secondKey") != null) {
                                resp.data.put("DPTID", resp.data.get("secondKey"));
                                adminInfo.setDPTID(resp.data.get("secondKey").getBytes());
                                m_hash.put("DPTID", adminInfo.getDPTID());
                            }
                            if (BuildConfig.IS_DEVEL) {
                                if (resp.data.get("secondKey") != null) {
                                    resp.data.put("DPTID", "DPT0TEST03");
                                    adminInfo.setDPTID(resp.data.get("DPTID").getBytes());
                                    m_hash.put("DPTID", adminInfo.getDPTID());
                                }
                            }


                            sendPush();

                        } else {
                            resp.resultMsg = new String(response.errorBody().bytes());

                            return_data.putExtra("action", "paymentReady");
                            return_data.putExtra("isSuccess", false);
                            return_data.putExtra("resultMsg", resp.resultMsg);
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK, return_data);
//                        finish();

                            if (sweetAlertDialog != null) {
                                sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                                if (!sweetAlertDialog.isShowing())
                                    showDialog();
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 1000);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return_data.putExtra("action", "paymentReady");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                        setResult(RESULT_OK, return_data);
//                    finish();

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
                    return_data.putExtra("action", "paymentReady");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                    setResult(RESULT_OK, return_data);
//                finish();

                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }

            });
        }
    }

    public void getPaymentStatistics(final String startDay, final String endDay) {
        Request req = new Request();
        req.data.put("log_uniqueId", uniqueID);
        req.data.put("payment", "집계정보");

        req.data.put("tmnId", resp.data.get("tmnId"));
        req.data.put("serial", resp.data.get("serial"));
        req.data.put("mchtId", resp.data.get("mchtId"));
        req.data.put("appId", "");
        req.data.put("version", BuildConfig.VERSION_NAME);
        req.data.put("telNo", "");

//        req.data.put("appId", resp.data.get("appId"));
//        req.data.put("version", resp.data.get("version"));
//        req.data.put("telNo", resp.data.get("telNo"));

        mAPIService.getStringToken("", req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        resp.setDataMap(GsonUtil.toJson(responseobj.data));


                        //---------------------
                        Request req = new Request();
                        req.data.put("startDay", startDay);
                        req.data.put("endDay", endDay);

                        mAPIService.getPaymentStatistics(resp.data.get("key"), req).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                return_data.putExtra("action", "getStatistics");
                                try {
                                    if (response.isSuccessful()) {
                                        String responseData = new String(response.body().bytes());
                                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

//                        if(resp.data.get("secondKey") != null &&  !(new String(adminInfo.getDPTID()).equals("DPT0TEST03"))) {
//                            resp.data.put("DPTID", resp.data.get("secondKey"));
//                            adminInfo.setDPTID(resp.data.get("secondKey").getBytes());
//                            m_hash.put("DPTID" , adminInfo.getDPTID() );
//                        }

//                        sendPush();

                                        resp.resultMsg = "조회가 완료되었습니다.";
                                        return_data.putExtra("resultData", resp.data);
                                        return_data.putExtra("isSuccess", true);
                                        return_data.putExtra("resultMsg", resp.resultMsg);
                                        setResult(RESULT_OK, return_data);
                                        finish();

                                    } else {
                                        resp.resultMsg = new String(response.errorBody().bytes());

                                        return_data.putExtra("isSuccess", false);
                                        return_data.putExtra("resultMsg", resp.resultMsg);
//                                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK, return_data);
                                        finish();

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return_data.putExtra("isSuccess", false);
                                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                                    setResult(RESULT_OK, return_data);
                                    finish();
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                                t.getMessage();
                                return_data.putExtra("action", "getStatistics");
                                return_data.putExtra("isSuccess", false);
                                return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                                setResult(RESULT_OK, return_data);
                                finish();
                            }

                        });

                        //----------------------


                    } else {
                        resp.resultMsg = new String(response.errorBody().bytes());

                        return_data.putExtra("action", "getToken");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", resp.resultMsg);
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, return_data);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    return_data.putExtra("action", "getToken");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", resp.resultMsg);
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, return_data);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
                return_data.putExtra("action", "getStatistics");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                setResult(RESULT_OK, return_data);
                finish();
            }

        });


    }

    public void getPaymentList(final String startDay, final String endDay) {
        Request req = new Request();
        req.data.put("log_uniqueId", uniqueID);
        req.data.put("payment", "거래내역");

        req.data.put("tmnId", resp.data.get("tmnId"));
        req.data.put("serial", resp.data.get("serial"));
        req.data.put("mchtId", resp.data.get("mchtId"));
        req.data.put("appId", "");
        req.data.put("version", BuildConfig.VERSION_NAME);
        req.data.put("telNo", "");

//        req.data.put("appId", resp.data.get("appId"));
//        req.data.put("version", resp.data.get("version"));
//        req.data.put("telNo", resp.data.get("telNo"));

        mAPIService.getStringToken("", req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        resp.setDataMap(GsonUtil.toJson(responseobj.data));


                        //---------------------
                        Request req = new Request();
                        req.data.put("startDay", startDay);
                        req.data.put("endDay", endDay);

                        mAPIService.getPaymentList(resp.data.get("key"), req).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                return_data.putExtra("action", "getList");
                                Log.d("test", "");
                                try {
                                    if (response.isSuccessful()) {
                                        String responseData = new String(response.body().bytes());
//                        responseObj responseobj =   (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                                        JSONObject json = new JSONObject(responseData);
                                        resp.setDataMap(GsonUtil.toJson(json));

//                        if(resp.data.get("secondKey") != null &&  !(new String(adminInfo.getDPTID()).equals("DPT0TEST03"))) {
//                            resp.data.put("DPTID", resp.data.get("secondKey"));
//                            adminInfo.setDPTID(resp.data.get("secondKey").getBytes());
//                            m_hash.put("DPTID" , adminInfo.getDPTID() );
//                        }

//                        sendPush();

                                        resp.resultMsg = "조회가 완료되었습니다.";
                                        return_data.putExtra("resultData", json.getJSONObject("data").getJSONArray("list").toString());
                                        return_data.putExtra("isSuccess", true);
                                        return_data.putExtra("resultMsg", resp.resultMsg);
                                        setResult(RESULT_OK, return_data);
                                        finish();
                                    } else {
                                        resp.resultMsg = new String(response.errorBody().bytes());

                                        return_data.putExtra("isSuccess", false);
                                        return_data.putExtra("resultMsg", resp.resultMsg);
//                                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                                        setResult(RESULT_OK, return_data);
                                        finish();

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return_data.putExtra("isSuccess", false);
                                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                                    setResult(RESULT_OK, return_data);
                                    finish();
                                }

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                                t.getMessage();
                                return_data.putExtra("action", "getList");
                                return_data.putExtra("isSuccess", false);
                                return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                                setResult(RESULT_OK, return_data);
                                finish();
                            }

                        });

                        //----------------------


                    } else {
                        resp.resultMsg = new String(response.errorBody().bytes());

                        return_data.putExtra("action", "getToken");
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", resp.resultMsg);
//                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, return_data);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    return_data.putExtra("action", "getToken");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, return_data);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();
                return_data.putExtra("action", "getToken");
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
//                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, return_data);
                finish();
            }

        });


    }

/*
    Request rs = new Request();   //
        rs.data.put("type", "승인실패");
        rs.data.put("van", resp.data.get("van").trim());
        rs.data.put("vanId", resp.data.get("vanId").trim());
        rs.data.put("amount", resp.data.get("amount").trim());
        rs.data.put("amount", resp.data.get("TotalAmount").trim());
        rs.data.put("vanTrxId", resp.data.get("trackId").trim());
        rs.data.put("trackId", resp.data.get("trackId").trim());
        rs.data.put("installment", resp.data.get("installment").trim());
*/

    public void sendApproveComplete() {
        Request req = new Request();
        //서버로그용 정보
        req.data.put("log_uniqueId", uniqueID);

        req.data.put("issuerCode", resp.data.get("IssueCode"));
        req.data.put("acquirerCode", resp.data.get("PurchaseCode"));

        req.data.put("type", resp.data.get("type").trim());
        if (isCancel) {
            req.data.put("type", "승인취소".trim());
        }
//        req.data.put("Number", resp.data.get("number").trim());

        if (resp.data.get("van") != null) req.data.put("van", resp.data.get("van").trim());
        if (resp.data.get("vanId") != null) req.data.put("vanId", resp.data.get("vanId").trim());

        if (new String(adminInfo.getTotalAmount()) != null) {
            req.data.put("amount", new String(adminInfo.getTotalAmount()));
            try {
                resp.data.put("TotalAmount", String.valueOf(Integer.parseInt(new String(adminInfo.getTotalAmount()))));
            } catch (Exception e) {
                e.printStackTrace();
                resp.data.put("TotalAmount", new String(adminInfo.getTotalAmount()));
            }
        }

        if (resp.data.get("authCd") != null) req.data.put("authCd", resp.data.get("authCd").trim());
        if (new String(adminInfo.getPayType()) != null)
            req.data.put("installment", new String(adminInfo.getPayType()).trim());
        if (resp.data.get("regDate") != null)
            req.data.put("regDate", resp.data.get("regDate").trim());

        if (resp.data.get("CardName") != null)
            req.data.put("brand", resp.data.get("CardName").trim());
        if (resp.data.get("trxId") != null) req.data.put("trxId", resp.data.get("trxId").trim());
//        if (resp.data.get("trackId") != null) {
//            req.data.put("trackId", resp.data.get("trackId").trim());
//            req.data.put("vanTrxId", resp.data.get("trackId").trim());
//        }
        if (trackId != null && trackId.length() > 0) {
            req.data.put("trackId", trackId.trim());
            req.data.put("vanTrxId", resp.data.get("trackId").trim());
        }
        if (resp.data.get("number") != null) req.data.put("number", resp.data.get("number").trim());

        req.data.put("prodQty", prodQty);
        req.data.put("prodDesc", payerAddr);
        req.data.put("prodName", prodName);
        try {
            req.data.put("prodPrice", String.valueOf(Long.parseLong(req.data.getString("amount"))));
        } catch (Exception e) {
            req.data.put("prodPrice", req.data.get("amount"));
        }
        req.data.put("payerName", payerName);
        req.data.put("payerEmail", payerEmail);
        req.data.put("payerTel", payerTel);

//        ksnetresp.setMap(resp.getMap());   // 요청의 데이터를 응답으로 넣습니다
//        print();

        Log.d("CompleteTest", "====================== 결제서버전송 ======================");
        mAPIService.getApproveComplete(resp.data.get("key"), req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                return_data.putExtra("action", "paymentRequest");
                try {
                    if (response.isSuccessful()) {
//                        Toast.makeText(WebCheckActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        String responseData = new String(response.body().bytes());
                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

                        String trxId = responseobj.data.get("trxId");

//                        System.out.println(resp.toJsonString());

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }

                        RealmResults<PaymentInfo> results = realm.where(PaymentInfo.class)
                                .equalTo("confmNo",req.data.getString("authCd"))
                                .equalTo("trackId",req.data.getString("trackId"))
                                .equalTo("delngSe",req.data.getString("type"))
                                .findAll();

                        if(results!=null){
                            Log.w("test","resutls: "+results.toString());
                            Log.w("test","resutls: "+results.size());
                        }

                        if(results!=null && results.size()==1){
                            realm.executeTransaction(realm -> {
                                PaymentInfo companyInfoModel = results.first();
                                companyInfoModel.isRegisted = true;
                                companyInfoModel.trxId = trxId;

                            });
                        }else if(results == null){
                            Log.e(" log","data가 존재하지 않음.");

                        }else {
                            Log.e("log","data 중복.");

                        }

                        return_data.putExtra("isSuccess", true);
                        return_data.putExtra("resultData", resp.data);
                        return_data.putExtra("resultMsg", resp.resultMsg);

                        setResult(RESULT_OK, return_data);

                        //winpay용
                        new Handler().postDelayed(() -> {
                            try {
                                StringBuilder sb = new StringBuilder();
                                if(req.data.getString("type").equals("승인취소")) {
                                    sb.append("winpay://refund_check?action=refund");
                                }else{
                                    sb.append("winpay://payment_check?action=payment");
                                }
                                sb.append("&resultCode=S0001");
                                sb.append("&resultMsg="+resp.data.get("notice1").trim());
                                sb.append("&payType=CARD");
                                sb.append("&authNum="+req.data.getString("authCd"));
                                sb.append("&authDate="+req.data.getString("regDate").substring(0,6));
                                sb.append("&authTime="+req.data.getString("regDate").substring(6));
                                sb.append("&isuuerName="+URLEncoder.encode(resp.data.get("CardName"), "UTF-8"));
                                sb.append("&purchaseName="+URLEncoder.encode(resp.data.get("PurchaseName").trim(), "UTF-8"));
                                sb.append("&issuerCode="+resp.data.get("IssueCode"));
                                sb.append("&purchaseCode="+resp.data.get("PurchaseCode"));
                                sb.append("&trxId="+responseobj.data.get("trxId"));
                                sb.append("&installment="+ req.data.getString("installment"));
                                sb.append("&cardNo="+ req.data.getString("number"));
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString())));
                                LOG.w("url: "+sb.toString());
                            } catch (ActivityNotFoundException e) {
                                LOG.w("액티비티 없음.");
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    JSONObject logJson = new JSONObject();
                                    logJson.put("message", "일반결제 에러.");
                                    logJson.put("error",e.getMessage());
                                    logJson.put("data", getIntent().getExtras().toString());
                                    logJson.put("response data", resp.data.toString());
                                    logJson.put("uniqueID", uniqueID);
                                    logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
                                    logJson.put("winpay_version", Util.getVersion(WebCheckActivity.this,"co.shilla.winpay"));
                                    logJson.put("os",Build.VERSION.SDK_INT + "");
                                    logJson.put("model",Build.MODEL + "");
                                    new NotiAsyncTask(WebCheckActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
                                } catch (Exception ee){}
                            }
                        }, 500);

                        finish();



                    } else {
                        resp.resultMsg = new String(response.errorBody().bytes());
                        return_data.putExtra("isSuccess", false);
                        return_data.putExtra("resultMsg", resp.resultMsg);
                        Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, return_data);

                        if (sweetAlertDialog != null) {
//                            sweetAlertDialog.setTitle(R.string.error_msg);
                            sweetAlertDialog.setTitleText(resp.resultMsg);
                            if (!sweetAlertDialog.isShowing())
                                showDialog();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }


                } catch (Exception e) {

                    e.printStackTrace();
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                    setResult(RESULT_OK, return_data);
                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//                t.getMessage();

                if (retryCount < MAX_RETRY_COUNT) {
                    new Handler().postDelayed(()->{
                        sendApproveComplete();
                        retryCount++;
                    },1000);
                } else {
                    return_data.putExtra("action", "paymentRequest");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultMsg", getString(R.string.network_error_msg));
                    setResult(RESULT_OK, return_data);
                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitle(R.string.error_msg);
//                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                }
            }

        });
    }

    private void sendPush() {
        Intent intent = new Intent(WebCheckActivity.this, PayResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("AdminInfo_Hash", m_hash);
        intent.putExtra("payment", resp.data);
        intent.putExtra("trackId", trackId);
        intent.putExtra("isOtherPayment", false);
        startActivityForResult(intent, 0);
    }

    //가맹점 전용 DPTID로 결제시 광원서버와 별개로 결제만 진행함.
    private void sendPush(boolean isOtherPayment) {
        Intent intent = new Intent(WebCheckActivity.this, PayResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("AdminInfo_Hash", m_hash);
        intent.putExtra("payment", resp.data);
        intent.putExtra("trackId", trackId);
        intent.putExtra("isOtherPayment", isOtherPayment);
        startActivityForResult(intent, 0);
    }

    private void sendSMS() {
        if (resp.data.get("phoneNo") == null || !PatternUtil.isCellphoneNo(resp.data.get("phoneNo"))) {
            Log.d("test", "번호없음");
            return;
        }
        if (resp.data.get("isReceiveSMS") != null && "false".equals(resp.data.get("isReceiveSMS"))) {
            return;
        }

        String name = resp.data.get("name");
        String issuer = resp.data.get("issuer");
        String cardNumber = resp.data.get("number");
        if (cardNumber == null || cardNumber.length() == 0) {
            cardNumber = resp.data.get("bin");
        }

        String trxResult = resp.data.get("type");
        String authCd = resp.data.get("authCd");
        String amount = resp.data.get("TotalAmount");
        String installment = resp.data.get("installment");
        String regDate = resp.data.get("regDate");

        HashMap<String, Object> data = new HashMap<>();
        data.put("title", "(주)광원");
        data.put("from", "18551838");
        data.put("ttl", "0");

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("to", "82" + resp.data.get("phoneNo").substring(1).replaceAll("-", ""));
        ArrayList<Object> list = new ArrayList<>();
        list.add(map);


        data.put("destinations", list);


        StringBuffer sb = new StringBuffer();

        sb.append("가맹점:");
        sb.append(name);
        sb.append("\n");
        sb.append("카드명:");
        sb.append(issuer);
        sb.append("\n");
        sb.append("카드번호:");
        sb.append(cardNumber);
        sb.append("\n");
        sb.append("승인결과:");
        sb.append(trxResult);
        sb.append("\n");
        sb.append("승인번호:");
        sb.append(authCd);
        sb.append("\n");
        sb.append("승인금액:");
        sb.append(amount);
        sb.append("\n");
        if (installment != null && installment.length() > 0) {
            sb.append("할부기간:");
            sb.append(installment);
            sb.append("\n");
        }
        sb.append("승인일자:");
        sb.append(regDate);

        data.put("text", sb.toString());


        mAPISMSService.sendSMS(resp.data.get("Authorization"), data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
//                        Toast.makeText(WebCheckActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        String responseData = new String(response.body().bytes());

                        Log.d("test", responseData);

//                        responseObj responseobj = (responseObj) GsonUtil.fromJson(responseData, responseObj.class);
//                        resp.setDataMap(GsonUtil.toJson(responseobj.data));

//                        System.out.println(resp.toJsonString());


                    } else {

                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(WebCheckActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                t.getMessage();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("================", requestCode + "/" + resultCode + "/" + AndroidUtils.getIntentData(data));

        String resultCd = "";
        String resultMsg = "";
        String jsonData = "";

        HashMap<String, String> rmap = new HashMap<>();

        if (data == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                rmap.put(key, (String) value);
                LOG.i("Result", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                    Log.d("tag", "onActivityResult " + resultCode + " resultCode " + resultCode + " address >>>>>>>>>" + address + "<<<<<<<<<");
                    KSNETStatus.BT_PRN_ADDRESS = address;

                    this.menu_state = APPROVE_PRINTER_SEARCH;
                    Message m = mHandler.obtainMessage(menu_state);
                    mHandler.sendMessageDelayed(m, 500 * 1);

                } else {
                    return_data.putExtra("resultCd", "-1");
                    return_data.putExtra("resultMsg", "프린터에 연결 할 수 없습니다");
                    setResult(RESULT_OK, return_data);
                    finish();
                }
                break;
            }


            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    KeyListenerInit();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d("TAG", "BT not enabled");
                    Toasty.error(getApplicationContext(), "블루투스가 활성화 되지 않았습니다 프로그램을 종료합니다", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
            case REQUEST_CODE_DIRECT_PAYMENT: {

                amount = data.getStringExtra("amount");
//                payerName = data.getStringExtra("payerName");
//                payerEmail = data.getStringExtra("payerEmail");
                payerTel = data.getStringExtra("payerTel");
                name = data.getStringExtra("name");
                cardNum = data.getStringExtra("cardNum");
                expiry_year = data.getStringExtra("expiry_year");
                expiry_month = data.getStringExtra("expiry_month");
                installment = data.getStringExtra("installment");

                if (!directPaymentParameterCheck(amount, "amount")
                        || !directPaymentParameterCheck(payerTel, "payerTel")
                        || !directPaymentParameterCheck(name, "name")
                        || !directPaymentParameterCheck(cardNum, "cardNum")
                        || !directPaymentParameterCheck(expiry_year, "expiry_year")
                        || !directPaymentParameterCheck(expiry_month, "expiry_month")
                        || !directPaymentParameterCheck(installment, "installment")) {

                    resp.resultMsg = "입력값이 누락되었습니다.";

                    return_data.putExtra("action", "sendDirectPayment");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultCd", resp.resultCd);
                    return_data.putExtra("resultMsg", resp.resultMsg);
                    return_data.putExtra("resultData", resp.data);
                    Toast.makeText(WebCheckActivity.this, resp.resultMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, return_data);
                    finish();
                    return;
                }
                sendDirectPayment();

                return;
            }
        }

        if (data != null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String Message1 = "NOK";
            String Message2 = "NOK";
            Bundle extra = data.getExtras();

            resp.resultCd = extra.getString("resultCd");
            resp.resultMsg = extra.getString("resultMsg");

            if (resp.resultCd.equals("1")) {

                if (extra.get("resultData") != null) {
                    responseObj tmp_responseobj = (responseObj) GsonUtil.fromJson(extra.get("resultData").toString(), new responseObj());

                    resp.setDataMap(GsonUtil.toJson(tmp_responseobj.geMap()));

                    if (resp.resultCd.equals("1")) {
                        Message1 = resp.data.get("Message1").trim();
                        Message2 = resp.data.get("Message2").trim();
                    }
                    resp.data.put("resultMsg", Message1 + ":" + Message2);
                    if (resultCode == Activity.RESULT_OK) {
                        if (Message2.indexOf("OK") >= 0 || "현금취소정상".equals(Message1)) {   // 성공입니다
                            resp.data.put("type", "승인".trim());
                            resp.data.put("number", "****************".trim());
                            if (resp.data.get("CardNo") != null && resp.data.get("CardNo").length() > 5) {
                                if (tools.CheckNumber(resp.data.get("CardNo").substring(0, 5))) {
                                    resp.data.put("number", resp.data.get("CardNo").trim());
                                }
                            }
//                          resp.data.put("vanTrxId", resp.data.get("Full_Text_Num").trim());    //  승인 번호
                            resp.data.put("authCd", resp.data.get("AuthNum").trim());    //  승인 번호
                            resp.data.put("regDate", resp.data.get("Authdate").trim());    //  승인 번호
                            resp.data.put("sendcount", "0".trim());
                            resp.data.put("issuer",resp.data.get("Message1").trim());

                            if (adminInfo != null && new String(adminInfo.getPayType()) != null) {
                                resp.data.put("installment", new String(adminInfo.getPayType()));
                            } else if (installment != null && installment.length() > 0) {
                                resp.data.put("installment", installment);
                            }

                            if (!"true".equals(extra.getString("cashReceipt"))) {
                                //신용카드 결제일경우
//                                sendHandlerMessage(APPROVE_AFTER);

                                if ("true".equals(extra.getString("isOtherPayment"))) {

                                    //van 직접결제 리턴.--------------

                                    if (sweetAlertDialog != null) {
                                        sweetAlertDialog.setTitleText(resp.resultMsg);
                                        if (!sweetAlertDialog.isShowing())
                                            showDialog();
                                    }

                                    return_data.putExtra("action", "vanPayment");
                                    return_data.putExtra("isSuccess", true);
                                    return_data.putExtra("resultData", resp.data);
                                    return_data.putExtra("resultMsg", resp.resultMsg);

                                    setResult(RESULT_OK, return_data);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1000);

                                    //---------------------

                                } else {
                                    sendApproveComplete();
                                    if (isCancel)
                                        resp.data.put("type", "취소");
                                    sendSMS();
                                }

                            } else {
                                // 현금영수증의 경우
                                return_data.putExtra("action", "cashReceipt");
                                return_data.putExtra("isSuccess", true);
                                return_data.putExtra("resultCd", resp.resultCd);
                                return_data.putExtra("resultMsg", resp.resultMsg);
                                return_data.putExtra("resultData", resp.data);
                                setResult(RESULT_OK, return_data);

                                if (sweetAlertDialog != null && adminInfo.getReceiptNo() != "".getBytes()) {
                                    sweetAlertDialog.setTitleText(resp.resultMsg);
                                    if (!sweetAlertDialog.isShowing())
                                        showDialog();
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1000);
                            }
                        }
                    }
                    return;
                } else {
                    return_data.putExtra("action", "cashReceipt");
                    return_data.putExtra("isSuccess", false);
                    return_data.putExtra("resultCd", resp.resultCd);
                    return_data.putExtra("resultMsg", resp.resultMsg);
                    return_data.putExtra("resultData", resp.data);
                    setResult(RESULT_OK, return_data);
                    if (sweetAlertDialog != null) {
                        sweetAlertDialog.setTitleText(resp.resultMsg);
                        if (!sweetAlertDialog.isShowing())
                            showDialog();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                    return;
                }
            } else {
                return_data.putExtra("isSuccess", false);
                return_data.putExtra("resultCd", resp.resultCd);
                return_data.putExtra("resultMsg", resp.resultMsg);
                return_data.putExtra("resultData", resp.data);
                setResult(RESULT_OK, return_data);
                if (sweetAlertDialog != null) {
                    sweetAlertDialog.setTitleText(resp.resultMsg);
                    if (!isFinishing() && !sweetAlertDialog.isShowing()) {
                        try {
                            showDialog();
                        } catch (Exception e) {
                        }
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                return;
            }

        } else {
            return_data.putExtra("isSuccess", false);
            return_data.putExtra("resultCd", resp.resultCd);
            return_data.putExtra("resultMsg", resp.resultMsg);
            return_data.putExtra("resultData", resp.data);
            setResult(RESULT_OK, return_data);
            if (sweetAlertDialog != null) {
                sweetAlertDialog.setTitleText(resp.resultMsg);
                if (!sweetAlertDialog.isShowing())
                    showDialog();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
            return;
        }

    }

    public void print() {

        prnSendData(" 신용카드전표",
                ksnetresp.PrnTempleteData(0,
                        ksnetresp.getString("name"),
                        ksnetresp.getString("ceoName"),
                        ksnetresp.getString("identity"),
                        ksnetresp.getString("telNo"),
                        ksnetresp.getString("addr"),
                        ksnetresp.getString("vat")));

        /*
        SendDataBmp(ksnetresp.PrnBmpTempleteData(0,
                    ksnetresp.getString("name"),
                    ksnetresp.getString("ceoName"),
                    ksnetresp.getString("identity"),
                    ksnetresp.getString("telNo"),
                    ksnetresp.getString("addr"))
        );
        */

        ListView l = null;
        l.invalidateViews();
        ListAdapter la;

    }


//PrnBmpTempleteData(int approve, String store_name, String name , String sauup, String telNo , String addr )

    /*
    프리터가 연결 되어 있지 않으면 2초간 한던 돌린 후 작업 합니다
    */
    private void SendDataBmp(Bitmap bmp) {
        int nMode = 0;
        int nPaperWidth = 384;
        if (bmp != null) {

            byte[] bdata = PrintPicture.POS_PrintBMP(bmp, nPaperWidth, nMode);
            //	SendDataByte(buffer);
            SendDataByte(Command.ESC_Init);
            SendDataByte(Command.LF);
            SendDataByte(bdata);
            SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(20));
            SendDataByte(PrinterCommand.POS_Set_Cut(1));
            SendDataByte(PrinterCommand.POS_Set_PrtInit());
        }
    }

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

    private void SendDataByte(byte[] data) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toasty.error(this, "프린터가 연결되지 않았습니다", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }

    public void BTPrinter() {
//        if(KSNETStatus.BT_PRN_FLAG) return;
        Log.d("tag", "KSNETStatus BT_PRN_ADDRESS : " + KSNETStatus.BT_PRN_ADDRESS);
        KSNETStatus.BT_PRN_ADDRESS = "";
        if ((KSNETStatus.BT_PRN_ADDRESS != null) && (KSNETStatus.BT_PRN_ADDRESS.length() > 0)) {  // 바로 접솝합니다
            if (BluetoothAdapter.checkBluetoothAddress(KSNETStatus.BT_PRN_ADDRESS)) {
                BluetoothDevice device = mBluetoothAdapter
                        .getRemoteDevice(KSNETStatus.BT_PRN_ADDRESS);
                // Attempt to connect to the device
                try {
                    mService.connect(device);
                } catch (Exception e) {
                }
                Toasty.info(WebCheckActivity.this, "프린터를 연결하고 있습니다", Toast.LENGTH_LONG, true).show();
                // custom_toast("프린터 연결중입니다......");
            }
        } else {
//            Intent serverIntent = new Intent(WebCheckActivity.this, DeviceListDialogtActivity.class);
            Intent serverIntent = new Intent(WebCheckActivity.this, DeviceListActivity.class);
            serverIntent.putExtra("name", "BTP111");
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

    private void KeyListenerInit() {
        Log.d("debug", "start bt printer ==================");
        mService = new BluetoothService(this, mHandler);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (PHONE) {
            if (mBluetoothAdapter!=null && !mBluetoothAdapter.isEnabled()) {
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

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (PHONE) {
            try {
                if (mService != null) {
                    if (mService.getState() == BluetoothService.STATE_NONE) {
                        // Start the Bluetooth services
                        mService.start();
                    }
                }
            }catch (Exception e){e.printStackTrace();}
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
        IS_DOING = false;

        // Stop the Bluetooth services
        if (PHONE) {
            if (mService != null)
                mService.stop();
            if (DEBUG)
                Log.e(TAG, "--- ON DESTROY ---");
        }

        if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
            sweetAlertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
