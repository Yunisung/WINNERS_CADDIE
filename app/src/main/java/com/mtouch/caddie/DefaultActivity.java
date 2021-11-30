package com.mtouch.caddie;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.caddie.network.MtouchLoadingDialog;

public class DefaultActivity extends AppCompatActivity {

    protected MtouchLoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new MtouchLoadingDialog(this);
    }

    protected void showLoading(){
        try {
            if (!isFinishing() && !loadingDialog.isShowing() && loadingDialog != null) {
                loadingDialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void hideLoading(){
        try {
            if (!isFinishing() && loadingDialog != null) {
                loadingDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
