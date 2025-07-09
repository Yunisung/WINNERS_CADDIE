package com.bkwinners.caddie.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bkwinners.caddie.DefaultActivity;
import com.bkwinners.caddie.R;
import com.bkwinners.caddie.databinding.ActivitySmsBinding;
import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class AuthSMSActivity extends DefaultActivity {

    private ActivitySmsBinding binding;

    private CaddieAPIService caddieAPIService;
    private boolean isSMSCheck = false;
    private boolean isSMSsend = false;

    private String bizType;
    
    private Button nextButton;
    private EditText idEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sms);
        binding = ActivitySmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initView();
    }

    private void init() {
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());
    }

    private void initView() {
        ((TextView)binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("회원가입");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v->{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });


        binding.phoneNumberEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId== EditorInfo.IME_ACTION_DONE){
                binding.sendSMSButton.performClick();
                return true;
            }
            return false;
        });

        binding.authNumberEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId== EditorInfo.IME_ACTION_DONE){
                binding.authSMSButton.performClick();
                return true;
            }
            return false;
        });

        binding.personalRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) bizType = "주민번호";
        });
        binding.bizRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) bizType = "사업자번호";
        });

        binding.sendSMSButton.setOnClickListener(v->{
            if(binding.phoneNumberEditText.getText().toString().trim().length()==0){
                Toast.makeText(this, "휴대폰번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.phoneNumberEditText.requestFocus();
                return;
            }
            if(isSMSsend){
                Toast.makeText(this, "인증번호가 이미 발송되었습니다. 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            new MtouchDialog(this, positiveView -> {
                HashMap<String, Object> param = new HashMap<>();
                param.put("phone",binding.phoneNumberEditText.getText().toString());
                showLoading();
                caddieAPIService.sendSMS(param).enqueue(new Callback<com.bkwinners.caddie.network.model.Response>() {
                    @Override
                    public void onResponse(Call<com.bkwinners.caddie.network.model.Response> call, retrofit2.Response<com.bkwinners.caddie.network.model.Response> response) {
                        hideLoading();
                        if(response.isSuccessful() && response.body() != null){
                            if(response.body().isSuccess()) {
                                Toast.makeText(AuthSMSActivity.this, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                binding.authNumberEditText.requestFocus();
                                isSMSsend = true;
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(binding.authNumberEditText, 0);
                            }else{
                                Toast.makeText(AuthSMSActivity.this, response.body().getResultMsg(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(AuthSMSActivity.this, "통신오류", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<com.bkwinners.caddie.network.model.Response> call, Throwable t) {
                        hideLoading();
                        String resultMsg = t.getMessage();
                        new MtouchDialog(AuthSMSActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                    }
                });
            }, negativeView -> {

            }).setTitleText("알림").setContentText(binding.phoneNumberEditText.getText().toString().trim()+"\n위 번호로 인증번호를 발송하시겠습니까?")
                    .setPositiveButtonText("발송").setNegativeButtonText("취소")
                    .show();


        });


        binding.authSMSButton.setOnClickListener(v->{
            if(binding.authNumberEditText.getText().toString().trim().length()==0){
                Toast.makeText(this, "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.authNumberEditText.requestFocus();
                return;
            }


            HashMap<String, Object> param = new HashMap<>();
            param.put("phone",binding.phoneNumberEditText.getText().toString());
            param.put("smsAuthKey",binding.authNumberEditText.getText().toString());
            showLoading();
            caddieAPIService.sendAuthSMS(param).enqueue(new Callback<com.bkwinners.caddie.network.model.Response>() {
                @Override
                public void onResponse(Call<com.bkwinners.caddie.network.model.Response> call, retrofit2.Response<com.bkwinners.caddie.network.model.Response> response) {
                    hideLoading();
                    if(response.isSuccessful()){

                        if(response.body().isSuccess()) {
                            isSMSCheck=true;
                            Toast.makeText(AuthSMSActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
                            binding.phoneNumberEditText.setEnabled(false);
                            binding.authNumberEditText.setEnabled(false);
                        }else{
                            Toast.makeText(AuthSMSActivity.this, response.body().getResultMsg(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<com.bkwinners.caddie.network.model.Response> call, Throwable t) {
                    hideLoading();
                    String resultMsg = t.getMessage();
                    new MtouchDialog(AuthSMSActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            });
        });
        


        binding.nextButton.setOnClickListener(v->{
            if(binding.bizNumberEditText.getText().toString().trim().length()==0){
                new MtouchDialog(AuthSMSActivity.this, vv->{
                    binding.bizNumberEditText.requestFocus();

                }).setTitleText("알림").setContentText("사업자번호를 입력해주세요.").show();
                return;
            }


            if(!isSMSCheck){
                new MtouchDialog(AuthSMSActivity.this).setTitleText("알림").setContentText("핸드폰번호 인증을 해주세요.").show();
                return;
            }
            if(bizType ==null || bizType.length()==0) {
                Toast.makeText(this, "사업자구분을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }


           signUpCheck();
        });
    }



    private void signUpCheck(){
        HashMap<String, Object> param = new HashMap<>();
        param.put("bizType",bizType);
        param.put("bizId",binding.bizNumberEditText.getText().toString());
        param.put("phoneNumber",binding.phoneNumberEditText.getText().toString());
        showLoading();
        caddieAPIService.signUpCheck(param).enqueue(new Callback<com.bkwinners.caddie.network.model.Response>() {
            @Override
            public void onResponse(Call<com.bkwinners.caddie.network.model.Response> call, retrofit2.Response<com.bkwinners.caddie.network.model.Response> response) {
                hideLoading();
                if(response.isSuccessful()){

                    if(response.body().isSuccess()) {
                        String mchtId = (String) response.body().getData().get("mchtId");
                        String name = (String) response.body().getData().get("name");
                        String nick = (String) response.body().getData().get("nick");
                        String ceoName = (String) response.body().getData().get("ceoName");
                        new MtouchDialog(AuthSMSActivity.this,v->{

                            Intent intent = new Intent(AuthSMSActivity.this, SignUpActivity.class);
                            intent.putExtra("mchtId", mchtId);
                            intent.putExtra("ceoName", ceoName);
                            intent.putExtra("name", name);
                            intent.putExtra("nick", nick);
                            intent.putExtra("bizType", bizType);
                            intent.putExtra("phoneNumber", binding.phoneNumberEditText.getText().toString());
                            intent.putExtra("bizNumber", binding.bizNumberEditText.getText().toString());

                            startActivity(intent);
                            finish();
                        },v -> {

                        }).setTitleText("알림").setContentText("가맹점아이디: "+mchtId+"\n 이름: "+ceoName).setImageResource(R.drawable.ic_check_circle).show();
                    }else{
                        Toast.makeText(AuthSMSActivity.this, response.body().getResultMsg(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<com.bkwinners.caddie.network.model.Response> call, Throwable t) {
                hideLoading();
                String resultMsg = t.getMessage();
                new MtouchDialog(AuthSMSActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
            }
        });

    }

}
