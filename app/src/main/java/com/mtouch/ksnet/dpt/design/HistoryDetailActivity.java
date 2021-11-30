package com.mtouch.ksnet.dpt.design;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.OrderDirectPaymentActivity;
import com.mtouch.caddie.OrderPaymentActivity;
import com.mtouch.caddie.R;
import com.mtouch.caddie.network.CaddieAPIService;
import com.mtouch.caddie.network.MtouchLoadingDialog;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.caddie.network.model.Order;
import com.mtouch.caddie.network.model.OrderDetail;
import com.mtouch.caddie.network.model.OrderListDetailResponse;
import com.mtouch.ksnet.dpt.design.appToApp.network.APIService;
import com.mtouch.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.MtouchSMSDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class HistoryDetailActivity extends DeviceCheckActivity {

    protected MtouchLoadingDialog loadingDialog;

    private APIService apiService;
    private CaddieAPIService caddieAPIService;
    private ArrayList<OrderDetail> orderDetailArrayList;

    private LinearLayout detailLayout;
    private ImageView backButton;
    private TextView headerTitleTextView;

    private TextView mchtNameTextView;
    private TextView terminalIdTextView;

    private TextView cancelCountTextView;
    private TextView readyCountTextView;
    private TextView completeCountTextView;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForParse = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Order order;

    // End Of Content View Elements
    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);

        mchtNameTextView = (TextView) findViewById(R.id.mchtNameTextView);
        terminalIdTextView = (TextView) findViewById(R.id.terminalIdTextView);

        cancelCountTextView = (TextView) findViewById(R.id.cancelCountTextView);
        readyCountTextView = (TextView) findViewById(R.id.readyCountTextView);
        completeCountTextView = (TextView) findViewById(R.id.completeCountTextView);


        detailLayout = findViewById(R.id.detailLayout);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("분할 결제 내역");


//        cancelButtn.setOnClickListener(v->{
//            cancelButtn.setEnabled(false);
//            int tax = Integer.parseInt(paymentInfo.getAmount()) / 11;
//            int amount = Integer.parseInt(paymentInfo.getAmount()) - tax;
//
//            sendTrxCheck(paymentInfo.getAmount(),paymentInfo.getInstallment(),paymentInfo.getTrxId());
//        });
//        printButtn.setOnClickListener(v->{
//            try {
//                JSONObject json = new JSONObject();
//                json.put("Message1", paymentInfo.getBrand());
//                json.put("PurchaseName", paymentInfo.getIssuer());
//                json.put("CardNo", paymentInfo.getNumber());
//                json.put("TotalAmount", paymentInfo.getAmount());
//                json.put("AuthNum", paymentInfo.getAuthCd());
//                json.put("Authdate",paymentInfo.getRegDay()+paymentInfo.getRegTime());
//                json.put("installment", paymentInfo.getInstallment());
//
//                Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_SENDPRINT);
//                Bundle bundle = new Bundle();
//                bundle.putString("data", json.toString());
//                msg.setData(bundle);
//                mHandler.sendMessage(msg);
//            } catch ( Exception e) { e.printStackTrace(); }
//        });
//        smsButtn.setOnClickListener(v->{
//            new MtouchSMSDialog(this).setSendData(getSMSJSONString(paymentInfo.getBrand(),paymentInfo.getNumber(), paymentInfo.getTrxResult(),paymentInfo.getAuthCd(),paymentInfo.getAmount(),paymentInfo.getInstallment(),paymentInfo.getRegDay()+paymentInfo.getRegTime()
//            )).show();
//        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        loadingDialog = new MtouchLoadingDialog(this);


        bindViews();
        init();

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

    private void init() {
        apiService = ApiUtils.getAPIService();
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());

        if (getIntent() != null) {
            order = (Order) getIntent().getSerializableExtra(Order.INTENT_KEY_ORDER);

            headerTitleTextView.setText("분할 결제 내역 (" + order.getResultCount() + "/" + order.getReqPayCount() + ")");

            //초기화
            detailLayout.removeAllViews();

            HashMap<String, Object> params = new HashMap<>();
            params.put("gcKey", order.getGcKey());
            showLoading();
            caddieAPIService.orderListDetail(params).enqueue(new Callback<OrderListDetailResponse>() {
                @Override
                public void onResponse(Call<OrderListDetailResponse> call, retrofit2.Response<OrderListDetailResponse> response) {
                    hideLoading();
                    try {
                        //status code 확인
                        if (response.isSuccessful()) {

                            //resultCd확인
                            if (response.body().isSuccess()) {

                                orderDetailArrayList = response.body().getList();
                                int completeCount = 0;
                                int requestCount = 0;
                                int cancelCount = 0;
                                for (OrderDetail orderDetail : orderDetailArrayList) {
                                    if(orderDetail.getStatus().equals("요청")){
                                        requestCount++;
                                    }else if(orderDetail.getStatus().equals("완료")){
                                        completeCount++;
                                    }else if(orderDetail.getStatus().equals("취소")){
                                        cancelCount++;
                                    }
                                    detailLayout.addView(inflateDetailLayout(orderDetail));
                                }

                                cancelCountTextView.setText("취소("+cancelCount+"건)");
                                readyCountTextView.setText("요청"+requestCount+"건)");
                                completeCountTextView.setText("완료("+completeCount+"건)");

                            } else {
                                String resultMsg = response.body().getResultMsg();
                                new MtouchDialog(HistoryDetailActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                            }
                        } else {
                            String resultMsg = new String(response.errorBody().bytes());
                            new MtouchDialog(HistoryDetailActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //네트워크 자체에러
                @Override
                public void onFailure(Call<OrderListDetailResponse> call, Throwable t) {
                    hideLoading();
                    String resultMsg = getString(R.string.network_error_msg);
                    new MtouchDialog(HistoryDetailActivity.this, v -> finish()).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            });
        }


//        mchtNameTextView.setText(SharedPreferenceUtil.getData(this, "name"));
//        terminalIdTextView.setText(SharedPreferenceUtil.getData(this, "tmnId"));


    }

    private View inflateDetailLayout(OrderDetail orderDetail) {
        View view = getLayoutInflater().inflate(R.layout.order_detail_layout, null);
        TextView resultTextView;
        TextView roundingAmountTextView;
        TextView tipAmountTextView;
        TextView amountTextView;

        TextView paymentTypeTextView;
        TextView timeTextView;
        TextView nameTextView;
        TextView phoneNumberTextView;
        TextView trackIdTextView;
        TextView trxIdTextView;

        TextView approvalNumberTextView;
        TextView brandTextView;
        TextView installmentTextView;
        TextView cardNumberTextView;
        TextView approvalDayTextView;
        TextView approvalNumberTwoTextView;
        Button payButtn;
        Button printButtn;
        Button smsButtn;
        Button receiptButtn;

        resultTextView = (TextView) view.findViewById(R.id.resultTextView);
        tipAmountTextView = (TextView) view.findViewById(R.id.tipAmountTextView);
        roundingAmountTextView = (TextView) view.findViewById(R.id.roundingAmountTextView);
        amountTextView = (TextView) view.findViewById(R.id.amountTextView);

        paymentTypeTextView = (TextView) view.findViewById(R.id.paymentTypeTextView);
        timeTextView = (TextView) view.findViewById(R.id.timeTextView);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        phoneNumberTextView = (TextView) view.findViewById(R.id.phoneNumberTextView);
        trackIdTextView = (TextView) view.findViewById(R.id.trackIdTextView);
        trxIdTextView = (TextView) view.findViewById(R.id.trxIdTextView);

        approvalNumberTextView = (TextView) view.findViewById(R.id.approvalNumberTextView);
        brandTextView = (TextView) view.findViewById(R.id.brandTextView);
        installmentTextView = (TextView) view.findViewById(R.id.installmentTextView);
        cardNumberTextView = (TextView) view.findViewById(R.id.cardNumberTextView);
        approvalDayTextView = (TextView) view.findViewById(R.id.approvalDayTextView);
        approvalNumberTwoTextView = (TextView) view.findViewById(R.id.approvalNumberTwoTextView);

        payButtn = (Button) view.findViewById(R.id.payButtn);
//        printButtn = (Button) view.findViewById(R.id.printButtn);
        smsButtn = (Button) view.findViewById(R.id.smsButtn);
        receiptButtn = (Button) view.findViewById(R.id.receiptButtn);

        smsButtn.setOnClickListener(v -> {
            if(orderDetail.getSmsKey()!=null && orderDetail.getSmsKey().length()>0){
                String sendMsg = "";
                sendMsg += "--------------------------------\n";
                sendMsg += "SMS 결제 및 영수증 URL :" + "\n";
                sendMsg += BuildConfig.BASE_URL + "/sms/" + orderDetail.getSmsKey() + "/pay" + "\n";
                sendMsg += "--------------------------------";

                new MtouchSMSDialog(this).setContentText("SMS결제 영수증을 보냅니다.").setSendDataAndNumber(sendMsg, orderDetail.getNumber()).setOriginal(true).show();
            }else {
                //현금

            }
        });

        //
        if (orderDetail.getPayType().equals("현금")) {
            smsButtn.setVisibility(View.GONE);
        }

        String resDateString = "";
        if (orderDetail.getTrxId() != null && orderDetail.getTrxId().length() > 0) {
            smsButtn.setText("SMS 영수증");
            payButtn.setVisibility(View.GONE);
            try {
                resDateString = simpleDateFormatDetail.format(simpleDateFormatForParse.parse(orderDetail.getResDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            resultTextView.setBackgroundResource(R.drawable.round_rect_ice_shape_radius_10);
            resultTextView.setTextColor(ContextCompat.getColor(HistoryDetailActivity.this, R.color.greeny_blue));
        } else {

            if (orderDetail.getPayType().equals("SMS")) {
                smsButtn.setText("SMS 재전송");
                smsButtn.setOnClickListener(v -> {
                    if(orderDetail.getSmsKey()!=null && orderDetail.getSmsKey().length()>0){
                        String sendMsg = "";
                        sendMsg += "--------------------------------\n";
                        sendMsg += "SMS 결제 및 영수증 URL :" + "\n";
                        sendMsg += BuildConfig.BASE_URL + "/sms/" + orderDetail.getSmsKey() + "/pay" + "\n";
                        sendMsg += "--------------------------------";

                        new MtouchSMSDialog(this).setContentText("SMS결제 재요청을 보냅니다.").setSendDataAndNumber(sendMsg, orderDetail.getNumber()).setOriginal(true).show();
                    }else {
                        //현금

                    }
                });
            } else if (orderDetail.getPayType().equals("수기")) {
                smsButtn.setVisibility(View.GONE);
                payButtn.setVisibility(View.VISIBLE);
                payButtn.setOnClickListener(v -> {
                    Intent intent = new Intent(this, OrderDirectPaymentActivity.class);
                    ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                    orderDetails.add(orderDetail);
                    intent.putExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST,orderDetails);
                    startActivityForResult(intent,0);
                });
            }else if(orderDetail.getPayType().equals("단말기")){
                smsButtn.setVisibility(View.GONE);
                payButtn.setVisibility(View.VISIBLE);
                payButtn.setOnClickListener(v -> {
                    Intent intent = new Intent(this, OrderPaymentActivity.class);
                    ArrayList<OrderDetail> orderDetails = new ArrayList<>();
                    orderDetails.add(orderDetail);
                    intent.putExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST,orderDetails);
                    startActivityForResult(intent,0);
                });
            }
            resultTextView.setBackgroundResource(R.drawable.round_rect_pink_shape_radius_10);
            resultTextView.setTextColor(ContextCompat.getColor(HistoryDetailActivity.this, R.color.coral));
        }

        tipAmountTextView.setText(String.format("%,d원",Integer.parseInt(orderDetail.getTip())));
        roundingAmountTextView.setText(String.format("%,d원",Integer.parseInt(orderDetail.getRoundingAmount())));
        amountTextView.setText(String.format("%,d원",Integer.parseInt(orderDetail.getTotalAmount())));
        paymentTypeTextView.setText(orderDetail.getPayType());
        timeTextView.setText(orderDetail.getResDate());
        nameTextView.setText(orderDetail.getName());
        phoneNumberTextView.setText(orderDetail.getNumber());
        trackIdTextView.setText(orderDetail.getTrackId());
        trxIdTextView.setText(orderDetail.getTrxId());

        resultTextView.setText(orderDetail.getStatus());

//        timeTextView.setText(orderDetail.getReqDay());
//        approvalNumberTextView.setText(orderDetail.getTrxId());
//        brandTextView.setText(orderDetail.getPayInfo());
//        amountTextView.setText(String.format("%,d", Integer.parseInt(orderDetail.getTotalAmount())) + " 원");
//        installmentTextView.setText(orderDetail.getPayType());
//        cardNumberTextView.setText(orderDetail.getNumber());

//        approvalDayTextView.setText(resDateString);
//        approvalNumberTwoTextView.setText(orderDetail.getTrackId());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new Handler().postDelayed(()->init(),1000);
    }
}
