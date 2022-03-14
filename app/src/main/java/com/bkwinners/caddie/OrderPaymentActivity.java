package com.bkwinners.caddie;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bkwinners.caddie.databinding.ActivityOrderPaymentBinding;
import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.MtouchLoadingDialog;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.caddie.network.model.OrderDetail;
import com.bkwinners.caddie.network.model.OrderPayResponse;
import com.bkwinners.ksnet.dpt.action.PayResultActivity;
import com.bkwinners.ksnet.dpt.action.obj.responseObj;
import com.bkwinners.ksnet.dpt.design.DeviceCheckActivity;
import com.bkwinners.ksnet.dpt.design.appToApp.ResponseObj;
import com.bkwinners.ksnet.dpt.design.appToApp.network.APIService;
import com.bkwinners.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.Request;
import com.bkwinners.ksnet.dpt.design.util.GsonUtil;
import com.bkwinners.ksnet.dpt.design.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchInstallmentDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.bluetooth.DeviceRegistActivity;
import com.bkwinners.ksnet.dpt.ks03.obj.KSnetRequestObj;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.bkwinners.ksnet.dpt.telegram.NotiAsyncTask;
import com.pswseoul.util.CommonUtil;
import com.pswseoul.util.tools;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bkwinners.caddie.R;
import com.bkwinners.caddie.BuildConfig;

public class OrderPaymentActivity extends DeviceCheckActivity {

    private static final int MAX_RETRY_COUNT = 5;


    protected static final int REQUEST_APPROVE_KSNET = 10000;

    private int retryCount = 0;

    private ActivityOrderPaymentBinding binding;

    protected MtouchLoadingDialog loadingDialog;

    private APIService mAPIService;
    private APIService mAPIDirectService;
    private APIService mAPISMSService;

    private CaddieAPIService caddieAPIService;

    private ImageView backButton;
    private TextView headerTitleTextView;

    private String installmentString = "00";

    private ArrayList<OrderDetail> orderDetailList;
    private OrderDetail orderDetail;

    private String trackId = null;
    private String smsKey = null;

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);


        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("결제하기");

        binding.installmentTextView.setOnClickListener(v -> {
            if (orderDetail != null && orderDetail.getTotalAmount() != null) {
                int amount = CommonUtil.parseInt(orderDetail.getTotalAmount());
                if (amount < 50000) {
                    new MtouchDialog(this).setTitleText("알림").setContentText("5만원 미만은 할부선택이 불가능 합니다.").show();
                    return;
                }
            }
            new MtouchInstallmentDialog(this, index -> {
                installmentString = index;
                if (installmentString.equals("0")) {
                    binding.installmentTextView.setText("일시불");
                } else {
                    binding.installmentTextView.setText(installmentString + "개월");
                }

            }).setTitleText("할부 기간")
                    .setPositiveButtonText("확인")
                    .setMaxInstallment(Integer.parseInt(SharedPreferenceUtil.getData(this, "apiMaxInstall", "0"))).show();
        });
    }

    protected void showLoading() {
        try {
            if (!isFinishing() && !loadingDialog.isShowing() && loadingDialog != null) {
                loadingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void hideLoading() {
        try {
            if (!isFinishing() && loadingDialog != null) {
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order_payment);
        binding = ActivityOrderPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindViews();
        init();
    }

    private void init() {
        loadingDialog = new MtouchLoadingDialog(this);

        if (getIntent() != null) {
            orderDetailList = (ArrayList<OrderDetail>) getIntent().getSerializableExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST);
            if (orderDetailList != null && orderDetailList.size() > 0) {

                orderDetail = orderDetailList.get(0);

                //setting info
                String phoneNumber = orderDetail.getNumber();
                String name = orderDetail.getName();
                String place = SharedPreferenceUtil.getData(this, "ccName");
                String amount = orderDetail.getTotalAmount();
                String tip = orderDetail.getTip();
                String rounding = orderDetail.getRoundingAmount();
                smsKey = orderDetail.getSmsKey();
                trackId = orderDetail.getTrackId();

                binding.totalAmountTextView.setText(String.format("%,d 원", Integer.parseInt(amount)));

                if (name != null && name.trim().length() > 0)
                    binding.nameTextView.setText(orderDetail.getName().trim());
                if (phoneNumber != null && phoneNumber.trim().length() > 0)
                    binding.phoneNumberTextView.setText(orderDetail.getNumber().trim());
                if (place != null && place.trim().length() > 0)
                    binding.placeTextView.setText(place.trim());
                if (rounding != null && rounding.length() > 0)
                    binding.roundingAmountTextView.setText(String.format("%,d", Integer.parseInt(rounding)));
                if (tip != null && tip.length() > 0)
                    binding.tipTextView.setText(String.format("%,d", Integer.parseInt(tip)));
                if (amount != null && amount.length() > 0)
                    binding.amountTextView.setText(String.format("%,d", Integer.parseInt(amount)));
            }
        }

        mAPIService = ApiUtils.getAPIService();
        mAPIDirectService = ApiUtils.getAPIDirectService();
        mAPISMSService = ApiUtils.getSMSSendService();
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());

        binding.payButton.setOnClickListener(v -> {

            String address = SharedPreferenceUtil.getData(OrderPaymentActivity.this, Constants.KEY_MAC_ADDRESS, "NONE");
            if(address.equals("NONE")){
                new MtouchDialog(this,vv->{
                    SharedPreferenceUtil.putData(this, Constants.KEY_MAC_ADDRESS,"NONE");
                    startActivity(new Intent(this, DeviceRegistActivity.class));
                },vv->{

                }).setPositiveButtonText("등록하기").setTitleText("알림").setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setContentText("블루투스 리더기가 등록되어 있지 않습니다.\n등록후 시도해주세요.").show();
                return;
            }


            //결제내역 존재하는지 체크.
            sendSmsPayCheck(trackId, ()-> sendCheckApprove(orderDetail.getTotalAmount(), installmentString));

        });
    }


    public void sendCheckApprove(final String _amount, String _installment) {
        binding.payButton.setEnabled(false);
        Request req = new Request();
        req.data.put("amount", _amount);
        req.data.put("installment", _installment);

        showLoading();
        mAPIService.getCheckApprove(SharedPreferenceUtil.getData(this, "key"), req).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideLoading();
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        ResponseObj responseobj = (ResponseObj) GsonUtil.fromJson(responseData, ResponseObj.class);

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
                        String dptId = responseobj.getStringValue("secondKey");
                        String van = responseobj.getStringValue("van");
                        String vanId = responseobj.getStringValue("vanId");
//                        String trackId = responseobj.getStringValue("trackId");

                        if (van == null || van.length() == 0 || !van.contains("KSPAY") || dptId == null || dptId.length() == 0) {
                            new MtouchDialog(OrderPaymentActivity.this, true).setTitleText("알림").setContentText(getString(R.string.tmn_setting_error)).show();
                            return;
                        }

                        //승인
                        LOG.w("결제시작 @@@");
                        KSnetRequestObj ksnetreq = new KSnetRequestObj();

                        ksnetreq.put("van", van);
                        ksnetreq.put("vanId", vanId);
                        ksnetreq.put("trackId", trackId);
                        ksnetreq.put("secondKey", dptId);
                        ksnetreq.put("TelegramType", "0200");                                    // 전문 구분 ,  승인(0200) 취소(0420)   승인번호없는망취소 (0440) / (0450)
                        ksnetreq.put("DPTID", dptId);             // 단말기번호 , 테스트단말번호 DPT0TEST03 -> KSCIC앱에 기등록된 단말기번호일 경우에만 정상 승인
                        ksnetreq.put("PosEntry", "S");                                            // Pos Entry Mode , 현금영수증 거래 시 키인거래에만 'K'사용
                        ksnetreq.put("PayType", String.format("%02d", Integer.parseInt(installmentString)));                                            // [신용]할부개월수(default '00') [현금]거래자구분
                        ksnetreq.put("installment", String.format("%02d", Integer.parseInt(installmentString)));                                            // [신용]할부개월수(default '00') [현금]거래자구분
                        ksnetreq.put("TotalAmount", new String(tools.getStrMoneytoTgAmount(_amount))); // 총금액

                        if (BuildConfig.IS_DEVEL) {
                            ksnetreq.put("DPTID", "DPT0TEST03");
                        }

                        int TotalAmount = Integer.parseInt(_amount);  //총금액
                        int TaxAmount = (int) ((TotalAmount / 1.1) * 0.1);     // 부가세

                        if (SharedPreferenceUtil.getData(OrderPaymentActivity.this, "vat", "Y").equals("N")) {
                            TaxAmount = 0;
                        }
                        int Amount = Math.round(TotalAmount - TaxAmount); // 공급가액

                        ksnetreq.put("amount", new String(tools.getStrMoneytoTgAmount("" + TotalAmount))); // 총금액
                        ksnetreq.put("TaxAmount", new String(tools.getStrMoneytoTgAmount("" + TaxAmount)));     // 부가세
                        ksnetreq.put("ServicAmount", new String(tools.getStrMoneytoTgAmount("0")));        // 봉사료

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

                        Intent intent = new Intent(OrderPaymentActivity.this, PayResultActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);

                        intent.putExtra("AdminInfo_Hash", ksnetreq.getByteMap());
                        ksnetreq.put("key", SharedPreferenceUtil.getData(OrderPaymentActivity.this, "key"));
                        intent.putExtra("payment", ksnetreq.data);
                        intent.putExtra("trackId", ksnetreq.data.get("trackId"));
                        startActivityForResult(intent, REQUEST_APPROVE_KSNET);


                    } else {
                        String resultMsg = new String(response.errorBody().bytes());
                        try {
                            new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        binding.payButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();
                try {
                    new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                binding.payButton.setEnabled(true);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_APPROVE_KSNET) {

            if (data != null) {

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String Message1 = "NOT";
                String Message2 = "NOT";

                Bundle extra = data.getExtras();
                responseObj responseobj = (responseObj) com.pswseoul.util.GsonUtil.fromJson(extra.get("resultData").toString(), responseObj.class);
                HashMap<String, String> mhashApprove = responseobj.geMap();

                Request request = new Request();

                if (responseobj.resultCd.equals("1")) {
                    Message1 = mhashApprove.get("Message1").trim();
                    Message2 = mhashApprove.get("Message2").trim();
                }


                if (resultCode == Activity.RESULT_OK) {

                    if (Message2.indexOf("OK") >= 0) {
//                        request.data.put("type", "승인".trim());
//
//                        Toasty.success(this, "정상 승인 되었습니다", Toast.LENGTH_LONG, true).show();
//
//                        request.data.put("number", "****************".trim());
//                        if (mhashApprove.get("CardNo") != null && mhashApprove.get("CardNo").length() > 5) {
//                            if (tools.CheckNumber(mhashApprove.get("CardNo").substring(0, 5))) {
//                                request.data.put("number", mhashApprove.get("CardNo").trim());
//                            }
//                        }
//
//                        request.data.put("authCd", ksnetresp.getString("AuthNum").trim());
//                        request.data.put("regDate", ksnetresp.getString("Authdate").trim());
//
//                        request.data.put("issuerCode", mhashApprove.get("IssueCode"));
//                        request.data.put("acquirerCode", mhashApprove.get("PurchaseCode"));
//
//
//                        prnSendData("신용카드전표", ksnetresp.PrnTempleteData(OrderPaymentActivity.this, 0, ksnetresp.getString("vat")));

                        HashMap<String, String> paymentMap = (HashMap<String, String>) data.getSerializableExtra("paymentMap");

                        int amount = Integer.parseInt(responseobj.geKeyValye("TotalAmount"));
                        String van = paymentMap.get("van");
                        String vanTrxId = paymentMap.get("trackId");
                        String authCd = responseobj.geKeyValye("AuthNum");
                        String trackId = paymentMap.get("trackId");
                        String regDate = responseobj.geKeyValye("Authdate");
                        String type = "승인";
                        String resultMsg = responseobj.geKeyValye("Message2");
                        String number = responseobj.geKeyValye("CardNo").trim();
                        String vanId = paymentMap.get("vanId");
                        String installment = paymentMap.get("installment");
                        String issuerCode = responseobj.geKeyValye("IssueCode");
                        String acquirerCode = responseobj.geKeyValye("PurchaseCode");
                        String prodQty = "1";
                        String prodName = "라운딩";
                        String prodPrice = responseobj.geKeyValye("TotalAmount");

                        request.put("amount",amount);
                        request.put("van",van);
                        request.put("vanTrxId",vanTrxId);
                        request.put("authCd",authCd);
                        request.put("trackId",trackId);
                        request.put("regDate",regDate);
                        request.put("type",type);
                        request.put("resultMsg",resultMsg);
                        request.put("number",number);
                        request.put("vanId",vanId);
                        request.put("installment",installment);
                        request.put("issuerCode",issuerCode);
                        request.put("acquirerCode",acquirerCode);
                        request.put("prodQty",prodQty);
                        request.put("prodName",prodName);
                        request.put("prodPrice",prodPrice);
                        request.put("payerTel",orderDetail.getNumber());
                        request.put("payerName",orderDetail.getName());


                        sendPayment(request);


                    } else {
                        //Message2 성공응답이 아님.


                    }


                } else {
                    // resultCode가 성공이 아님.

                }


            } else {
                //data가 없음.


            }


        }


        binding.payButton.setEnabled(true);

    }

    private void sendSmsPayCheck(String trackId,Runnable callback){
        HashMap<String, Object> map = new HashMap<>();
        map.put("trackId",trackId);

        caddieAPIService.smsPayCheck(map).enqueue(new Callback<com.bkwinners.caddie.network.model.Response>() {
            @Override
            public void onResponse(Call<com.bkwinners.caddie.network.model.Response> call, Response<com.bkwinners.caddie.network.model.Response> response) {
                if(response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        callback.run();
                    } else {
                        String msg = response.body().getResultMsg();
                        if (msg != null && msg.length() > 0) {
                            new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(msg).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                        } else {
                            new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                        }
                    }
                }else{
                    new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                }
            }

            @Override
            public void onFailure(Call<com.bkwinners.caddie.network.model.Response> call, Throwable t) {
                new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
            }
        });
    }



    private void sendPayment(Request request) {
        mAPIService.getApproveComplete(SharedPreferenceUtil.getData(this, "key"), request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        ResponseObj responseobj = (ResponseObj) GsonUtil.fromJson(responseData, ResponseObj.class);
                        String trxId = responseobj.getStringValue("trxId");

                        sendSmsPayPush(trxId);
                    } else {
                        new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (retryCount < MAX_RETRY_COUNT) {
                    try {
                        new Handler().postDelayed(() -> sendPayment(request), 2000);
                    } catch (Exception e) {
                    }
                } else {
                    new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();

                    try {
                        JSONObject logJson = new JSONObject();
                        logJson.put("message", "결제등록에러");
                        logJson.put("data", request.toJsonString());
                        logJson.put("caddie_version", BuildConfig.VERSION_NAME);
                        logJson.put("os", Build.VERSION.SDK_INT + "");
                        logJson.put("model", Build.MODEL + "");
                        new NotiAsyncTask(OrderPaymentActivity.this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });
    }

    private void sendSmsPayPush(String trxId){

        HashMap<String, Object> map = new HashMap<>();
        map.put("trxId",trxId);
        map.put("smsKey",smsKey);
        map.put("trackId",trackId);
        caddieAPIService.smsPayPush(map).enqueue(new Callback<com.bkwinners.caddie.network.model.Response>() {
            @Override
            public void onResponse(Call<com.bkwinners.caddie.network.model.Response> call, Response<com.bkwinners.caddie.network.model.Response> response) {
                if (response.isSuccessful()) {

                } else {
                    String msg = response.body().getResultMsg();
                    if(msg!=null&&msg.length()>0){
                        new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(msg).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                    }else {
                        new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<com.bkwinners.caddie.network.model.Response> call, Throwable t) {
                new MtouchDialog(OrderPaymentActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
            }
        });
    }



}