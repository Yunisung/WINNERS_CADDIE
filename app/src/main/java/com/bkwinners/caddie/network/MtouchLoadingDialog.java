package com.bkwinners.caddie.network;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bkwinners.caddie.R;
import com.bkwinners.ksnet.dpt.design.util.LOG;

public class MtouchLoadingDialog extends Dialog {

    private Activity activity;

    private ImageView loadingImageview;

    public MtouchLoadingDialog(@NonNull Context context) {
        super(context);
        activity = (Activity) context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.dialog_loading);
        loadingImageview = findViewById(R.id.loading_imageview);


        setOnShowListener(dialog -> init());
        setOnDismissListener(dialog -> {});

        setCancelable(false);
        //최대 20초
        new Handler().postDelayed(()->dismiss(),20000);
    }
    private void init(){
        rotateStart(45f);
    }

    private void rotateStart(float degree){
        if(!isShowing()) return;
        loadingImageview.setRotation(degree+loadingImageview.getRotation());
        loadingImageview.invalidate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rotateStart(degree);
            }
        },120);
    }

    @Override
    public void show() {
        if(!activity.isFinishing()) super.show();
    }

    @Override
    public void dismiss() {
        if(!activity.isFinishing()) super.dismiss();
    }
}
