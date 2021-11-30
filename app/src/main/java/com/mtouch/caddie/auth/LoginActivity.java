package com.mtouch.caddie.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.caddie.DefaultActivity;
import com.mtouch.caddie.MainActivity;
import com.mtouch.caddie.R;
import com.mtouch.caddie.databinding.ActivityLoginBinding;
import com.mtouch.caddie.network.CaddieAPIService;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends DefaultActivity {

    private ActivityLoginBinding binding;

    private CaddieAPIService caddieAPIService;

    private Button nextButton;
    private EditText idEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sms);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initView();
    }

    private void init() {
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());
    }

    private void initView() {
        ((TextView)binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("로그인");
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v->{

        });

        binding.pwEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.loginButton.performClick();
                return true;
            }
            return false;
        });

        binding.forgotTextView.setOnClickListener(v -> {
            new MtouchDialog(this).setTitleText("알림").setContentText("고객센터로 문의주시기 바랍니다.\n051-751-6422").show();
        });

        binding.loginButton.setOnClickListener(v -> {
            if (binding.idEditText.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.pwEditText.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }



            HashMap<String, Object> param = new HashMap<>();
            param.put("id", binding.idEditText.getText().toString().trim());
            param.put("pw", binding.pwEditText.getText().toString().trim());
            param.put("autoLogin", binding.autoCheckBox.isChecked() ? "Y" : "N");

            showLoading();
            caddieAPIService.login(param).enqueue(new Callback<com.mtouch.caddie.network.model.Response>() {
                @Override
                public void onResponse(Call<com.mtouch.caddie.network.model.Response> call, Response<com.mtouch.caddie.network.model.Response> response) {
                    hideLoading();

                    if (response.isSuccessful()) {
                        if(response.body().isSuccess()) {
                            //main 이동
                            if (binding.autoCheckBox.isChecked()) {
                                SharedPreferenceUtil.putData(LoginActivity.this, Constants.KEY_AUTO_LOGIN, "Y");
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else{
                            //사업자등록화면
                            String status;
                            if(response.body().getData()!=null && response.body().getData().containsKey("status")){
                                status = (String) response.body().getData().get("status");
                                if(status.equals("예비")){
                                    startActivity(new Intent(LoginActivity.this, MchtApplyActivity.class));
                                    finish();
                                }else if(status.equals("준비")){
                                    startActivity(new Intent(LoginActivity.this, MchtApplyInfo2Activity.class));
                                    finish();
                                }else if(status.equals("대기")){
                                    new MtouchDialog(LoginActivity.this).setTitleText("안내").setContentText("가맹점 등록대기 중 입니다.").show();
                                }else {
                                    String resultMsg = response.body().getResultMsg();
                                    new MtouchDialog(LoginActivity.this).setContentText(resultMsg).setTitleText("안내").show();
                                    Toast.makeText(LoginActivity.this, "가맹점 등록대기 중 입니다.", Toast.LENGTH_SHORT).show();
                                }

                                return;
                            }

                            String resultMsg = response.body().getResultMsg();
                            new MtouchDialog(LoginActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<com.mtouch.caddie.network.model.Response> call, Throwable t) {
                    hideLoading();
                    String resultMsg = t.getMessage();
                    new MtouchDialog(LoginActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            });
        });


        binding.mchtApplyButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AuthSMSActivity.class));
            finish();
        });


    }
}
