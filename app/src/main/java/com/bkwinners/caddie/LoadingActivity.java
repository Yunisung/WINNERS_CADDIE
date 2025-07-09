package com.bkwinners.caddie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.bkwinners.caddie.databinding.ActivityLoading2Binding;

public class LoadingActivity extends DefaultActivity {

    private ActivityLoading2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoading2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init(){
        rotateStart(45f);
    }

    private void rotateStart(float degree){
        if(isFinishing()) return;
        binding.loadingImageview.setRotation(degree+binding.loadingImageview.getRotation());
        binding.loadingImageview.invalidate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rotateStart(degree);
            }
        },120);
    }
}