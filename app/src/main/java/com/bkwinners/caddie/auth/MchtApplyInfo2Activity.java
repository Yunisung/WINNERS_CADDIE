package com.bkwinners.caddie.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bkwinners.caddie.R;
import com.bkwinners.caddie.databinding.ActivityMchtApplyInfo2Binding;
import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.caddie.network.model.Response;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class MchtApplyInfo2Activity extends AppCompatActivity {

    private ActivityMchtApplyInfo2Binding binding;

    private CaddieAPIService caddieAPIService;

    private String token;
    private String email;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mcht_apply_info2);
        binding = ActivityMchtApplyInfo2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        bindView();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
//        finish();
        super.onBackPressed();
    }

    private void init() {
        caddieAPIService = NetworkManager.getAPIService(this);
        caddieAPIService.getEformToken(new HashMap<>()).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                try {
                    if (response.isSuccessful()) {
//                        JSONObject jsonData = new JSONObject(new String(response.body().bytes()));
                        token = (String) response.body().getData().get("token");
                        email = (String) response.body().getData().get("email");
                        phone = (String) response.body().getData().get("phone");

                    } else {
                        String resultMsg = new String(response.errorBody().bytes());
                        try {
                            new MtouchDialog(MchtApplyInfo2Activity.this).setTitleText("알림").setContentText(resultMsg).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    new MtouchDialog(MchtApplyInfo2Activity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                try {
                    new MtouchDialog(MchtApplyInfo2Activity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bindView() {
        ((TextView) binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("결제서비스 신청");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.sendButton.setOnClickListener(v -> new MtouchDialog(this, view->{

        }).setContentText("설정된 전화번호와 이메일로 전송됩니다.\n" + "email: "+email+"\n"+"phone: "+phone).show());


    }


}