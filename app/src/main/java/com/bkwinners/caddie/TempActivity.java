package com.bkwinners.caddie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bkwinners.caddie.databinding.ActivityTempBinding;
import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.MtouchLoadingDialog;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.caddie.network.model.Response;
import com.bkwinners.ksnet.dpt.design.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

import com.bkwinners.caddie.R;
import com.bkwinners.caddie.BuildConfig;

public class TempActivity extends AppCompatActivity {

    private ActivityTempBinding binding;
    private CaddieAPIService caddieAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_temp);
        binding = ActivityTempBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());

    }

    public void goLoading(View v){
       MtouchLoadingDialog mtouchLoadingDialog = new MtouchLoadingDialog(this);
       mtouchLoadingDialog.show();

       new Handler().postDelayed(()->{
           mtouchLoadingDialog.dismiss();
       },3000);
    }

    public void smsSend(View v){
        HashMap<String, Object> param = new HashMap<>();
        param.put("phone",binding.phoneNumberEditText.getText().toString());
        caddieAPIService.sendSMS(param).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                String resultMsg = t.getMessage();
                new MtouchDialog(TempActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
            }
        });
    }
    public void smsAuth(View v){
        HashMap<String, Object> param = new HashMap<>();
        param.put("phone",binding.phoneNumberEditText.getText().toString());
        param.put("smsAuthKey",binding.smsAuthEditText.getText().toString());
        caddieAPIService.sendAuthSMS(param).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });
    }
    public void signup(View v){

    }
    public void login(View v){

    }
    public void apply(View v){

    }
    public void order(View v){

    }
    public void orderList(View v){

    }
}