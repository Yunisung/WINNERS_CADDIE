package com.mtouch.caddie;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mtouch.caddie.databinding.ActivityOrderPaymentCompleteBinding;
import com.mtouch.caddie.databinding.ActivityPaymentCompleteBinding;
import com.mtouch.caddie.network.model.OrderDetail;
import com.mtouch.ksnet.dpt.ReceiptPrintActivity;
import com.mtouch.ksnet.dpt.design.DeviceCheckActivity;
import com.mtouch.ksnet.dpt.design.appToApp.ResponseObj;
import com.mtouch.ksnet.dpt.design.appToApp.network.APIService;
import com.mtouch.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.mtouch.ksnet.dpt.design.appToApp.network.model.Request;
import com.mtouch.ksnet.dpt.design.util.GsonUtil;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.MtouchSMSDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderPaymentCompleteActivity extends DeviceCheckActivity {

    private ActivityOrderPaymentCompleteBinding binding;

    private APIService mAPIService;
    private APIService mAPIDirectService;
    private APIService mAPISMSService;

    private ImageView backButton;
    private TextView headerTitleTextView;

    String trackId = "";
    String cardNumber = "";
    String amount = "";
    String approvalDay = "";
    String approvalNumber = "";
    String trxId = "";
    String delngSe = "";
    String authDate = "";
    String authNum = "";
    String issuCmpnyNm = "";
    String cardNo = "";
    String instlmtMonth = "";
    String puchasCmpnyNm = "";
    String setleMssage = "";
    String splpc = "";
    String vatt = "";

    String brand = "";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForParse = new SimpleDateFormat("yyMMddHHmmss");
    private SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ArrayList<OrderDetail> orderDetailArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderPaymentCompleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindViews();
        init();
    }

    @Override
    public void onBackPressed() {
//        if(orderDetailArrayList==null || orderDetailArrayList.size()==0) {
            super.onBackPressed();
//        }else{
//            Toast.makeText(this, "결제가 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
//        }
    }

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("결제 완료 내역");


        binding.printButtn.setOnClickListener(v -> {
            try {
                JSONObject json = new JSONObject();
                json.put("brand", brand);
                json.put("PurchaseName", puchasCmpnyNm);
                json.put("number", cardNo);
                json.put("amount", amount);
                json.put("authCd", authNum);
                json.put("installment", instlmtMonth);

                Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_SENDPRINT);
                Bundle bundle = new Bundle();
                bundle.putString("data", json.toString());
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } catch ( Exception e) { e.printStackTrace(); }
        });
        binding.smsButtn.setOnClickListener(v -> {
            new MtouchSMSDialog(this).setSendData(getSMSJSONString(brand,cardNumber, delngSe,authNum,amount,instlmtMonth,authDate)).show();
        });

        binding.receiptButtn.setOnClickListener(v->{
            Intent intent = new Intent(OrderPaymentCompleteActivity.this, ReceiptPrintActivity.class);
            intent.putExtra("trackId",trackId);
            intent.putExtra("delngSe",delngSe);
            intent.putExtra("trxId",trxId);
            intent.putExtra("cardNumber",cardNumber);
            intent.putExtra("amount",amount);
            intent.putExtra("authDate",binding.approvalDayTextView.getText().toString());
            intent.putExtra("authNum",authNum);
            intent.putExtra("issuCmpnyNm",issuCmpnyNm);
            intent.putExtra("puchasCmpnyNm",puchasCmpnyNm);
            intent.putExtra("instlmtMonth",instlmtMonth);
            intent.putExtra("brand",brand);
            startActivity(intent);
        });

        binding.nextPayButtn.setOnClickListener(v->{
            Intent intent = new Intent(this, OrderDirectPaymentActivity.class);
            intent.putExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST, orderDetailArrayList);
            startActivity(intent);
            finish();
        });

        binding.exitButtn.setOnClickListener(v->{
            finish();
        });
    }



    private void init() {
        mAPIService = ApiUtils.getAPIService();
        mAPIDirectService = ApiUtils.getAPIDirectService();
        mAPISMSService = ApiUtils.getSMSSendService();

        //PYS : 영수증보기 버튼삭제
        binding.receiptButtn.setVisibility(View.GONE);

        if (getIntent() != null) {
            orderDetailArrayList = (ArrayList<OrderDetail>) getIntent().getSerializableExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST);
            String reqPayCount = getIntent().getStringExtra("reqPayCount");

            if(orderDetailArrayList!=null && orderDetailArrayList.size()>0){
                int size = orderDetailArrayList.size();
                binding.amountInfoTextView.setText("결제가 완료 되었습니다.\n"+"("+size+"/"+reqPayCount+")");

                binding.nextPayButtn.setVisibility(View.VISIBLE);
                binding.exitButtn.setVisibility(View.GONE);
            }else{
                binding.amountInfoTextView.setText("모든 결제가 완료되었습니다.\n감사합니다.");

                binding.nextPayButtn.setVisibility(View.GONE);
                binding.exitButtn.setVisibility(View.VISIBLE);
            }

            trackId = getIntent().getStringExtra("trackId");
            cardNumber = getIntent().getStringExtra("cardNumber");
            amount = getIntent().getStringExtra("amount");
            approvalDay = getIntent().getStringExtra("approvalDay");
            approvalNumber = getIntent().getStringExtra("approvalNumber");
            trxId = getIntent().getStringExtra("trxId");

            delngSe = getIntent().getStringExtra("delngSe");
            authDate = getIntent().getStringExtra("authDate");
            authNum = approvalNumber;
            issuCmpnyNm = getIntent().getStringExtra("issuCmpnyNm");
            cardNo = cardNumber;
            instlmtMonth = getIntent().getStringExtra("instlmtMonth");
            puchasCmpnyNm = getIntent().getStringExtra("puchasCmpnyNm");
            setleMssage = getIntent().getStringExtra("setleMssage");
            brand = getIntent().getStringExtra("brand");


            binding.placeTextView.setText(SharedPreferenceUtil.getData(this, "ccName"));
            binding.nameTextView.setText(getIntent().getStringExtra("payerName"));
            binding.phoneNumberTextView.setText(getIntent().getStringExtra("payerTel"));
            binding.amountTextView.setText(String.format("\\ %,d 원", Integer.parseInt(amount)));
            try {
                binding.approvalDayTextView.setText(simpleDateFormatDetail.format(simpleDateFormatForParse.parse(authDate)));
            } catch (ParseException e) {
                e.printStackTrace();
            }



        }
    }

    protected String getSMSJSONString(String brand, String number, String trxResult, String authCd, String amount, String installment, String regDate){
        JSONObject jo = new JSONObject();

        try {
            jo.put("name",SharedPreferenceUtil.getData(this,"name"));
            jo.put("brand",brand);
            jo.put("number",number);
            jo.put("trxResult",trxResult);
            jo.put("authCd",authCd);
            jo.put("amount",amount);
            jo.put("installment",installment);
            jo.put("regDate",regDate);
            jo.put("Authorization", SharedPreferenceUtil.getData(this,"Authorization"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jo.toString();
    }
}
