package com.bkwinners.caddie;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bkwinners.caddie.data.PayData;
import com.bkwinners.caddie.databinding.ActivityOrderDirectPaymentLayoutBinding;
import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.caddie.network.model.OrderDetail;
import com.bkwinners.caddie.network.model.OrderPayResponse;
import com.bkwinners.caddie.network.model.Response;
import com.bkwinners.ksnet.dpt.design.appToApp.network.APIService;
import com.bkwinners.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchInstallmentDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchListDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

import com.bkwinners.caddie.R;
import com.bkwinners.caddie.BuildConfig;

public class OrderDirectPaymentActivity extends AppCompatActivity {



    private ActivityOrderDirectPaymentLayoutBinding binding;

    private APIService mAPIService;
    private APIService mAPIDirectService;
    private APIService mAPISMSService;

    private CaddieAPIService caddieAPIService;

    private ImageView backButton;
    private TextView headerTitleTextView;


    private ArrayList<OrderDetail> orderDetailList;
    private OrderDetail orderDetail;

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);


        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("결제하기");

        binding.card1EditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override public void afterTextChanged(Editable s) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.card1EditText.getText().toString().length()==4){
                    new Handler().postDelayed(()->binding.card2EditText.requestFocus(),100);
                }
            }
        });
        binding.card2EditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override public void afterTextChanged(Editable s) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.card2EditText.getText().toString().length()==4){
                    new Handler().postDelayed(()->binding.card3EditText.requestFocus(),100);
                }
            }
        });
        binding.card3EditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override public void afterTextChanged(Editable s) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.card3EditText.getText().toString().length()==4){
                    new Handler().postDelayed(()->binding.card4EditText.requestFocus(),100);
                }
            }
        });
        binding.card4EditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override public void afterTextChanged(Editable s) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.card4EditText.getText().toString().length()==4){
                    new Handler().postDelayed(()->binding.validateTermEditText.requestFocus(),100);
                }
            }
        });

        binding.validateTermEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.payButton.performClick();
                return true;
            }
            return false;
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDirectPaymentLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindViews();
        init();
    }

    private void init() {
        if(getIntent()!=null){
            orderDetailList = (ArrayList<OrderDetail>) getIntent().getSerializableExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST);
            if(orderDetailList!=null && orderDetailList.size()>0) {

                orderDetail = orderDetailList.get(0);

                //setting info
                String phoneNumber = orderDetail.getNumber();
                String name = orderDetail.getName();
                String place = SharedPreferenceUtil.getData(this, "ccName");
                String amount = orderDetail.getTotalAmount();
                String tip = orderDetail.getTip();
                String rounding = orderDetail.getRoundingAmount();

                if(name!=null && name.trim().length()>0)
                    binding.nameTextView.setText(orderDetail.getName().trim());
                if(phoneNumber!=null && phoneNumber.trim().length()>0)
                    binding.phoneNumberTextView.setText(orderDetail.getNumber().trim());
                if(place!=null && place.trim().length()>0)
                    binding.placeTextView.setText(place.trim());
                if(rounding!=null && rounding.length()>0)
                    binding.roundingAmountTextView.setText(String.format("%,d",Integer.parseInt(rounding)));
                if(tip!=null && tip.length()>0)
                    binding.tipTextView.setText(String.format("%,d",Integer.parseInt(tip)));
                if(amount!=null && amount.length()>0)
                    binding.amountTextView.setText(String.format("%,d",Integer.parseInt(amount)));
            }
        }

        mAPIService = ApiUtils.getAPIService();
        mAPIDirectService = ApiUtils.getAPIDirectService();
        mAPISMSService = ApiUtils.getSMSSendService();
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());

        binding.payButton.setOnClickListener(v->{


            String cardNumber = binding.card1EditText.getText().toString().trim()
                    + binding.card2EditText.getText().toString().trim()
                    + binding.card3EditText.getText().toString().trim()
                    + binding.card4EditText.getText().toString().trim();

            String validateTerm = binding.validateTermEditText.getText().toString().trim();


            if(cardNumber==null || cardNumber.length()==0){
                new MtouchDialog(this,false).setTitleText("알림").setContentText("카드번호를 입력하세요.").show();
                return;
            }
            if(cardNumber.length()<14 || cardNumber.length()>16){
                new MtouchDialog(this,false).setTitleText("알림").setContentText("카드번호는 14~16자리만 허용합니다.").show();
                return;
            }
            if(validateTerm==null || validateTerm.length()==0){
                new MtouchDialog(this,false).setTitleText("알림").setContentText("유효기간을 입력하세요.").show();
                return;
            }


            Intent intent = new Intent(this, OrderDirectConfirmActivity.class);
            intent.putExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST, orderDetailList);
            intent.putExtra(OrderDirectConfirmActivity.INTENT_KEY_INPUTDATA, "sugi");
            intent.putExtra(OrderDirectConfirmActivity.INTENT_KEY_CARD_NUM,cardNumber);
            intent.putExtra(OrderDirectConfirmActivity.INTENT_KEY_VALIDATE_TERM,validateTerm);
            startActivityForResult(intent,OrderDirectConfirmActivity.REQUEST_CODE);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == OrderDirectConfirmActivity.REQUEST_CODE){
            if(resultCode==RESULT_OK){
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(0,0);
            }
        }
    }
}
