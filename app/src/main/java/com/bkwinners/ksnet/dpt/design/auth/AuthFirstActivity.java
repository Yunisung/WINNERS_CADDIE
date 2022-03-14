package com.bkwinners.ksnet.dpt.design.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bkwinners.ksnet.dpt.design.DeviceCheckActivity;
import com.bkwinners.ksnet.dpt.design.appToApp.ResponseObj;
import com.bkwinners.ksnet.dpt.design.appToApp.network.APIService;
import com.bkwinners.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.Request;
import com.bkwinners.ksnet.dpt.design.util.GsonUtil;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthFirstActivity extends AppCompatActivity {

    private APIService apiService;

    private Button nextButton;
    private EditText idEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        init();
        initView();
    }

    private void init() {
        apiService = ApiUtils.getAPIService();
    }

    private void initView() {
        nextButton = findViewById(R.id.nextButton);
        idEditText = findViewById(R.id.idEditText);

        idEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    nextButton.performClick();
                    return true;
                }
                return false;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiService.getMchtName(new Request().put("mchtId", idEditText.getText().toString().trim())).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.isSuccessful()) {
                                String responseData = new String(response.body().bytes());
                                ResponseObj responseobj = (ResponseObj) GsonUtil.fromJson(responseData, ResponseObj.class);

                                //save
                                SharedPreferenceUtil.putData(AuthFirstActivity.this,"mchtId",responseobj.getStringValue("mchtId"));

                                new MtouchDialog(AuthFirstActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        startActivity(new Intent(AuthFirstActivity.this, AuthSecondActivity.class));
                                        finish();
                                    }
                                },false).setTitleText("알림").setContentText("가맹점명 : \n"+responseobj.data.get("name")).show();
                            } else {
                                String resultMsg = new String(response.errorBody().bytes());
                                new MtouchDialog(AuthFirstActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        String resultMsg = t.getMessage();
                        new MtouchDialog(AuthFirstActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                    }
                });
            }
        });


    }
}
