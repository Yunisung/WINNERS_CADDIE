package com.mtouch.caddie.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.DefaultActivity;
import com.mtouch.caddie.MainActivity;
import com.mtouch.caddie.R;
import com.mtouch.caddie.databinding.ActivitySignupBinding;
import com.mtouch.caddie.network.CaddieAPIService;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.caddie.network.model.Response;
import com.mtouch.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.mtouch.ksnet.dpt.design.appToApp.network.model.Request;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class SignUpActivity extends DefaultActivity {

    private ActivitySignupBinding binding;
    private CaddieAPIService caddieAPIService;
    private boolean isSMSCheck = false;
    private boolean isSMSsend = false;

    private String mchtId;
    private String name;
    private String nick;
    private String bizNumber;
    private String phoneNumber;
    private String ceoName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login2);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initView();


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        super.onBackPressed();
    }

    private void init() {
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());

        if(getIntent()!=null){
            mchtId = getIntent().getStringExtra("mchtId");
            name = getIntent().getStringExtra("name");
            nick = getIntent().getStringExtra("nick");
            bizNumber = getIntent().getStringExtra("bizNumber");
            phoneNumber = getIntent().getStringExtra("phoneNumber");
            ceoName = getIntent().getStringExtra("ceoName");

            binding.nameEditText.setText(ceoName);
            binding.ccNameEditText.setText(nick);
        }
    }


    private void initView() {
        ((TextView)binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("회원가입");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v->{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });














        binding.confirmButton.setOnClickListener(v -> {
            //확인
            if(binding.idEditText.getText().toString().trim().length()==0){
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.idEditText.requestFocus();
                return;
            }
//            if(binding.idEditText.getText().toString().trim().length()<5){
//                Toast.makeText(this, "아이디는 5글자이상 입력해주세요.", Toast.LENGTH_SHORT).show();
//                binding.idEditText.requestFocus();
//                return;
//            }
            if(hasSpecialCharacter(binding.idEditText.getText().toString().trim())){
                Toast.makeText(this, "아이디는 영문숫자로만 구성할 수 있습니다.\n다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.idEditText.requestFocus();
                return;
            }

            if(binding.pwEditText.getText().toString().trim().length()==0){
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.pwEditText.requestFocus();
                return;
            }
//            if(binding.pwEditText.getText().toString().trim().length()<5){
//                Toast.makeText(this, "비밀번호는 5글자이상 입력해주세요.", Toast.LENGTH_SHORT).show();
//                binding.pwEditText.requestFocus();
//                return;
//            }

//            String pw = binding.pwEditText.getText().toString().trim();
//            String pwCheck = binding.pwCheckEditText.getText().toString().trim();
//            if(!pw.equals(pwCheck)){
//                Toast.makeText(this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
//                binding.pwCheckEditText.requestFocus();
//                return;
//            }


            if(binding.nameEditText.getText().toString().trim().length()==0){
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.nameEditText.requestFocus();
                return;
            }
            if(binding.nameEditText.getText().toString().trim().length()<1){
                Toast.makeText(this, "이름은 2글자이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.nameEditText.requestFocus();
                return;
            }
//            if(binding.emailEditText.getText().toString().trim().length()==0){
//                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
//                binding.emailEditText.requestFocus();
//                return;
//            }
//            if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailEditText.getText().toString().trim()).matches()){
//                Toast.makeText(this, "이메일형식에 맞게 입력해주세요.", Toast.LENGTH_SHORT).show();
//                binding.emailEditText.requestFocus();
//                return;
//            }
            if(binding.ccNameEditText.getText().toString().trim().length()==0){
                Toast.makeText(this, "CC 명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.ccNameEditText.requestFocus();
                return;
            }

            if(!binding.personalTermCheckBox.isChecked()){
                Toast.makeText(this, "약관에 동의를 체크해주세요.", Toast.LENGTH_SHORT).show();
                binding.personalTermCheckBox.requestFocus();
                return;
            }
            if(!binding.serviceTermCheckBox.isChecked()){
                Toast.makeText(this, "약관에 동의를 체크해주세요.", Toast.LENGTH_SHORT).show();
                binding.serviceTermCheckBox.requestFocus();
                return;
            }

            String id = binding.idEditText.getText().toString().trim();
            String pw = binding.pwEditText.getText().toString().trim();
            checkTid(id,pw,mchtId,()->{


                HashMap<String, Object> param = new HashMap<>();
                param.put("id",id);
                param.put("pw",pw);
                param.put("name",binding.nameEditText.getText().toString().trim());
//            param.put("email",binding.emailEditText.getText().toString());
//            param.put("phone",binding.phoneNumberEditText.getText().toString());
                param.put("ccName",binding.ccNameEditText.getText().toString().trim());

                param.put("mchtId",mchtId);
                param.put("phone",phoneNumber);

                showLoading();
                caddieAPIService.signup(param).enqueue(new Callback<com.mtouch.caddie.network.model.Response>() {
                    @Override
                    public void onResponse(Call<com.mtouch.caddie.network.model.Response> call, retrofit2.Response<com.mtouch.caddie.network.model.Response> response) {
                        hideLoading();
                        if(response.isSuccessful()){

                            if(response.body().isSuccess()) {
                                new MtouchDialog(SignUpActivity.this, v->{
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    finish();
                                }).setTitleText("회원가입 완료").setContentText("회원가입이 완료되었습니다.").show();
                            }else{
                                Toast.makeText(SignUpActivity.this, response.body().getResultMsg(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<com.mtouch.caddie.network.model.Response> call, Throwable t) {
                        hideLoading();
                        String resultMsg = t.getMessage();
                        new MtouchDialog(SignUpActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                    }
                });


            });


        });
    }


    public static boolean hasSpecialCharacter(String string) {
        if (TextUtils.isEmpty(string)) {
            return false;
        }

        if(!Pattern.matches("^[a-zA-Z0-9]*$",string)){
            return true;
        }
//        for (int i = 0; i < string.length(); i++) {
//            if (!Character.isLetterOrDigit(string.charAt(i))) {
//                return true;
//            }
//        }
        return false;
    }


    private void checkTid(String tmnId, String serial, String mchtId, Runnable callback){
        Request request = new Request()
                .put("tmnId", tmnId)
                .put("serial",serial)
                .put("mchtId", mchtId)
                .put("appId", getPackageName())
                .put("version", BuildConfig.VERSION_NAME)
                .put("telNo", "");

        showLoading();
        ApiUtils.getAPIService().getStringToken("",request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                hideLoading();
                try {
                    if (response.isSuccessful()) {
                        callback.run();
                    } else {
                        String resultMsg = new String(response.errorBody().bytes());
                        new MtouchDialog(SignUpActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    new MtouchDialog(SignUpActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();
                String resultMsg = t.getMessage();
                new MtouchDialog(SignUpActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
            }
        });
    }
}
