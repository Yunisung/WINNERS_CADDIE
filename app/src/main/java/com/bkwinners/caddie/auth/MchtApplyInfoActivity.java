package com.bkwinners.caddie.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bkwinners.caddie.R;
import com.bkwinners.caddie.databinding.ActivityMchtApplyInfoBinding;

public class MchtApplyInfoActivity extends AppCompatActivity {

    private ActivityMchtApplyInfoBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mcht_apply_info);
        binding = ActivityMchtApplyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        bindView();

    }

    @Override
    public void onBackPressed() {
        if(binding.popupLayout.getVisibility() == View.VISIBLE){
            binding.popupLayout.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

    private void init() {

    }

    private void bindView() {
        ((TextView)binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("사업자 정보 설정");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v->{
            if(binding.popupLayout.getVisibility() == View.VISIBLE){
                binding.popupLayout.setVisibility(View.GONE);
            }else {
                super.onBackPressed();
            }
        });
        binding.bizInfoButton.setOnClickListener(v-> binding.popupLayout.setVisibility(View.VISIBLE));
        binding.closeButton.setOnClickListener(v-> binding.popupLayout.setVisibility(View.GONE));
        binding.applyButton.setOnClickListener(v->{
            startActivity(new Intent(this, MchtApplyActivity.class));
            finish();
        });

    }

}