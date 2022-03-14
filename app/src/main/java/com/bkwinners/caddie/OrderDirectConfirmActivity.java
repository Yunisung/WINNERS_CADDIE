package com.bkwinners.caddie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bkwinners.caddie.data.PayData;
import com.bkwinners.caddie.data.WidgetData;
import com.bkwinners.caddie.databinding.ActivityOrderDirectConfirmBinding;
import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.caddie.network.model.OrderDetail;
import com.bkwinners.caddie.network.model.OrderPayResponse;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchInstallmentDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.pswseoul.util.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

import com.bkwinners.caddie.R;
import com.bkwinners.caddie.BuildConfig;

public class OrderDirectConfirmActivity extends DefaultActivity {

    public static final int REQUEST_CODE = 1122;

    public static final String INTENT_KEY_CARD_NAME = "intent_key_name";
    public static final String INTENT_KEY_CARD_NUM = "intent_key_card_num";
    public static final String INTENT_KEY_VALIDATE_TERM = "intent_key_validate_term";
    //220303 카드결제 수기결제 분리작업
    public static final String INTENT_KEY_INPUTDATA = "intent_key_inputdata";

    private ActivityOrderDirectConfirmBinding binding;

    private CaddieAPIService caddieAPIService;

    private String installmentString = "0";

    private String cardNum=null;
    private String amount = null;
    private String validateTerm = null;
    private String smsKey = null;
    private String name = null;
    private String inputData = null;

    private OrderDetail orderDetail;

    private ArrayList<OrderDetail> orderDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDirectConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        bindView();

    }

    private void init() {
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());

        if(getIntent()!=null){
            orderDetailList = (ArrayList<OrderDetail>) getIntent().getSerializableExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST);
            if(orderDetailList!=null && orderDetailList.size()>0) {

                orderDetail = orderDetailList.get(0);

                inputData = getIntent().getStringExtra(INTENT_KEY_INPUTDATA);
                name = orderDetail.getName();
                amount = orderDetail.getTotalAmount();
                cardNum = getIntent().getStringExtra(INTENT_KEY_CARD_NUM);
                validateTerm = getIntent().getStringExtra(INTENT_KEY_VALIDATE_TERM);
                String phoneNumber = orderDetail.getNumber();
                binding.totalAmountTextView.setText(String.format("%,d 원", Integer.parseInt(amount)));

                if(inputData.equals("sugi"))
                    binding.cardInfoTextView.setText("카드번호 뒷자리(" + cardNum.substring(cardNum.length() - 4) + ")");

                if(name!=null)
                    binding.nameEditText.setText(name);
                binding.phoneNumber1EditText.setText(phoneNumber.substring(0, 3));
                if (phoneNumber.length() == 11) {
                    binding.phoneNumber2EditText.setText(phoneNumber.substring(3, 7));
                    binding.phoneNumber3EditText.setText(phoneNumber.substring(7));
                } else if (phoneNumber.length() < 11) {
                    binding.phoneNumber2EditText.setText(phoneNumber.substring(3, 6));
                    binding.phoneNumber3EditText.setText(phoneNumber.substring(6));
                }

            }
        }
    }

    private void bindView() {

        binding.installmentTextView.setOnClickListener(v->{
            if(orderDetail!=null && orderDetail.getTotalAmount()!=null){
                int amount = CommonUtil.parseInt(orderDetail.getTotalAmount());
                if(amount<50000){
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

        binding.payButton.setOnClickListener(v->{
            payStart();
        });
    }

    private void payStart() {
        if(!binding.infoCheckbox.isChecked()){
            new MtouchDialog(this).setTitleText("알림").setContentText("결제내용을 확인 후 체크를 하시기 바랍니다.").show();
            return;
        }

        if(amount==null || amount.length()==0 || amount.equals("0")){
            new MtouchDialog(this,false).setTitleText("알림").setContentText("금액을 입력하세요.").show();
            return;
        }


        String name = binding.nameEditText.getText().toString().trim();
        String number = binding.phoneNumber1EditText.getText().toString().trim()
                + binding.phoneNumber2EditText.getText().toString().trim()
                + binding.phoneNumber3EditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        if(name==null || name.length()==0){
            new MtouchDialog(this,false).setTitleText("알림").setContentText("구매자명을 입력하세요.").show();
            return;
        }
        if(number==null || number.length()==0){
            new MtouchDialog(this,false).setTitleText("알림").setContentText("휴대전화번호를 입력하세요.").show();
            return;
        }
        if(email==null || email.length()==0){
            new MtouchDialog(this,false).setTitleText("알림").setContentText("이메일을 입력하세요.").show();
            return;
        }


        binding.payButton.setEnabled(false);

        HashMap<String, Object> params = new HashMap<>();

        params.put("payKey", SharedPreferenceUtil.getData(this,"payKey"));
        params.put("smsKey", orderDetail.getSmsKey());
        params.put("trackId", orderDetail.getTrackId());
        params.put("amount", orderDetail.getTotalAmount());
        params.put("payerName", name);
        params.put("payerEmail", email);
        params.put("payerTel", number);

        if(inputData.equals("sugi")) {
            params.put("cardNumber", cardNum);
            params.put("expiry", validateTerm.substring(2)+validateTerm.substring(0,2));
            params.put("installment", installmentString);
        }



        Log.d("sms/pay : ", params.toString());

        showLoading();

        if(inputData.equals("sugi")) {
            //수기결제 처리
            caddieAPIService.smsPay(params).enqueue(new Callback<OrderPayResponse>() {
                @Override
                public void onResponse(Call<OrderPayResponse> call, retrofit2.Response<OrderPayResponse> response) {
                    hideLoading();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().isSuccess()) {
                                //220302 pys : 온라인결제때문에 주석처리
                                orderDetailList.remove(orderDetail);

                                PayData payData = response.body().getPay();

                                Intent intent = new Intent(OrderDirectConfirmActivity.this, OrderPaymentCompleteActivity.class);
                                intent.putExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST, orderDetailList);
                                intent.putExtra("reqPayCount", orderDetail.getTrackId().split("_")[3]);
                                intent.putExtra("payerName", name);
                                intent.putExtra("payerTel", number);
                                intent.putExtra("trackId", payData.getTrackId());
                                intent.putExtra("cardNumber", payData.getCard().getBin() + "******" + payData.getCard().getLast4());
                                intent.putExtra("amount", payData.getAmount());
                                String authDate = payData.getTransactionDate().substring(2);
                                intent.putExtra("approvalDay", authDate.substring(0, 6));
                                intent.putExtra("approvalNumber", payData.getAuthCd());
                                intent.putExtra("trxId", payData.getTrxId());
                                intent.putExtra("delngSe", "승인");
                                intent.putExtra("authDate", authDate);
                                intent.putExtra("issuCmpnyNm", payData.getCard().getIssuer());
                                intent.putExtra("instlmtMonth", payData.getCard().getInstallment());
                                intent.putExtra("puchasCmpnyNm", payData.getCard().getAcquirer());
                                intent.putExtra("brand", payData.getCard().getIssuer());

                                startActivity(intent);
                                finish();
                            } else {
                                binding.payButton.setEnabled(true);
                                String resultMsg = response.body().getResultMsg();
                                new MtouchDialog(OrderDirectConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                            }
                        } else {
                            binding.payButton.setEnabled(true);
                            String resultMsg = new String(response.errorBody().bytes());
                            new MtouchDialog(OrderDirectConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<OrderPayResponse> call, Throwable t) {
                    hideLoading();
                    binding.payButton.setEnabled(true);
                    String resultMsg = getString(R.string.network_error_msg);
                    new MtouchDialog(OrderDirectConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            });
        } else {
            //카드결제 처리
            caddieAPIService.cardPay(params).enqueue(new Callback<OrderPayResponse>() {
                @Override
                public void onResponse(Call<OrderPayResponse> call, retrofit2.Response<OrderPayResponse> response) {
                    hideLoading();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().isSuccess()) {
                                //220302 pys : 온라인결제때문에 주석처리
                                orderDetailList.remove(orderDetail);

                                WidgetData widgetData = response.body().getWidget();

                                String url = BuildConfig.BASE_API_URL + widgetData.getRouteUrl();
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                                finish();
                            } else {
                                binding.payButton.setEnabled(true);
                                String resultMsg = response.body().getResultMsg();
                                new MtouchDialog(OrderDirectConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                            }
                        } else {
                            binding.payButton.setEnabled(true);
                            String resultMsg = new String(response.errorBody().bytes());
                            new MtouchDialog(OrderDirectConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                        }

                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailure(Call<OrderPayResponse> call, Throwable t) {
                    hideLoading();
                    binding.payButton.setEnabled(true);
                    String resultMsg = getString(R.string.network_error_msg);
                    new MtouchDialog(OrderDirectConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            });
        }
    }
}