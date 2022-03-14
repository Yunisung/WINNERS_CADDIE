package com.bkwinners.caddie.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bkwinners.caddie.MainActivity;
import com.bkwinners.caddie.R;
import com.bkwinners.caddie.databinding.ActivityMchtApplyBinding;
import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.caddie.network.model.Response;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchListDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class MchtApplyActivity extends AppCompatActivity {

    private ActivityMchtApplyBinding binding;

    private CaddieAPIService caddieAPIService;
    private String bankCode;
    private String bankName;
    private String idType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mcht_apply);
        binding = ActivityMchtApplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        bindView();
    }

    private void init() {
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());
    }

    private void bindView() {
        ((TextView)binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("사업자 정보 설정");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v->{
            finish();
        });


        binding.bizInfoButton.setOnClickListener(v->{
            binding.popupLayout.setVisibility(View.VISIBLE);
        });
        binding.popupLayout.setOnClickListener(v->{
            binding.popupLayout.setVisibility(View.GONE);
        });
        binding.popupButton.setOnClickListener(v->{
            binding.popupLayout.setVisibility(View.GONE);
        });




        binding.personalRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) idType = "주민등록번호";
        });
        binding.bizRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) idType = "사업자번호";
        });

        binding.bankButton.setOnClickListener(v->{
            MtouchListDialog mtouchListDialog = new MtouchListDialog(this, (index, otherData) -> {
                bankCode = otherData;
                bankName = index;
                binding.bankTextView.setText(index);
            }).setTitleText("은행 선택");
            mtouchListDialog.addList("한국산업은행","002");
            mtouchListDialog.addList("중소기업은행","003");
            mtouchListDialog.addList("국민은행","004");
            mtouchListDialog.addList("수협은행","007");
            mtouchListDialog.addList("농협(중앙회)","011");
            mtouchListDialog.addList("농협(지역)","012");
            mtouchListDialog.addList("우리은행","020");
            mtouchListDialog.addList("SC은행","023");
            mtouchListDialog.addList("한국씨티은행","027");
            mtouchListDialog.addList("대구은행","031");
            mtouchListDialog.addList("부산은행","032");
            mtouchListDialog.addList("광주은행","034");
            mtouchListDialog.addList("제주은행","035");
            mtouchListDialog.addList("전북은행","037");
            mtouchListDialog.addList("경남은행","039");
            mtouchListDialog.addList("새마을금고","045");
            mtouchListDialog.addList("신협","048");
            mtouchListDialog.addList("상호저축은행","050");
            mtouchListDialog.addList("기타외국은행","051");
            mtouchListDialog.addList("모간스탠리","052");
            mtouchListDialog.addList("홍콩상하이은행","054");
            mtouchListDialog.addList("도이치은행","055");
            mtouchListDialog.addList("에이비엔암로은행","056");
            mtouchListDialog.addList("미즈호코퍼레이트은행","058");
            mtouchListDialog.addList("도쿄미쓰비시은행","059");
            mtouchListDialog.addList("뱅크오브아메리카","060");
            mtouchListDialog.addList("산림조합","064");
            mtouchListDialog.addList("우체국","071");
            mtouchListDialog.addList("KEB 하나은행","081");
            mtouchListDialog.addList("신한은행","088");
            mtouchListDialog.addList("동양종합금융증권","209");
            mtouchListDialog.addList("현대증권","218");
            mtouchListDialog.addList("미래에셋증권","230");
            mtouchListDialog.addList("대우증권","238");
            mtouchListDialog.addList("삼성증권","240");
            mtouchListDialog.addList("한국투자증권","243");
            mtouchListDialog.addList("우리투자증권","247");
            mtouchListDialog.addList("교보증권","261");
            mtouchListDialog.addList("하이투자증권","262");
            mtouchListDialog.addList("에이치엠씨투자증권","263");
            mtouchListDialog.addList("키움증권","264");
            mtouchListDialog.addList("이트레이드증권","265");
            mtouchListDialog.addList("에스케이증권","266");


            mtouchListDialog.show();
        });

        binding.confirmButton.setOnClickListener(v->{
            String bizNumber = binding.bizNumberEditText.getText().toString().trim();
            String bankName = binding.bankTextView.getText().toString().trim();
            String bankNumber = binding.accountEditText.getText().toString().trim();
            String name = binding.nameEditText.getText().toString().trim();

            if(bizNumber.length()==0){
                Toast.makeText(this, "사업자 등록번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.bizNumberEditText.requestFocus();
                return;
            }
            if(bankName.length()==0){
                Toast.makeText(this, "은행명을 선택해주세요.", Toast.LENGTH_SHORT).show();
                binding.bankButton.performClick();
                return;
            }
            if(bankNumber.length()==0){
                Toast.makeText(this, "계좌번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.bizNumberEditText.requestFocus();
                return;
            }
            if(name.length()==0){
                Toast.makeText(this, "예금주명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                binding.nameEditText.requestFocus();
                return;
            }
            if(idType ==null || idType.length()==0) {
                Toast.makeText(this, "사업자구분을 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> param = new HashMap<>();
            param.put("identity", bizNumber);
            param.put("bankName", bankName);
            param.put("account", bankNumber);
            param.put("accntHolder", name);
            param.put("idType", idType);
            caddieAPIService.mchtApply(param).enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    if (response.isSuccessful()) {
                        if(response.body().isSuccess()) {
                            new MtouchDialog(MchtApplyActivity.this, v->{
                                startActivity(new Intent(MchtApplyActivity.this, MchtApplyInfo2Activity.class));
                                finish();
                            }).setTitleText("사업자 정보설정 완료").setContentText("사업자 정보설정이\n" +"완료되었습니다.").show();


                        }else{

                            String resultMsg = response.body().getResultMsg();
                            new MtouchDialog(MchtApplyActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<com.bkwinners.caddie.network.model.Response> call, Throwable t) {
                    String resultMsg = t.getMessage();
                    new MtouchDialog(MchtApplyActivity.this).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
                }
            });
        });
    }

}