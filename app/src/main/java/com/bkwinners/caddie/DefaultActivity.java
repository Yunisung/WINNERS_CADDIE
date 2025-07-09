package com.bkwinners.caddie;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bkwinners.caddie.network.MtouchLoadingDialog;

public class DefaultActivity extends AppCompatActivity {

    protected MtouchLoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new MtouchLoadingDialog(this);

        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
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
