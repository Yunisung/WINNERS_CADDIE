package com.mtouch.ksnet.dpt.design.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;


public class MtouchSMSDialog extends Dialog {

    private Activity activity;

    private TextView titleTextView;
    private TextView contentTextView;

    private Button mPositiveButton;
    private FrameLayout mNegativeButton;

    private EditText smsEditText;

    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;

    private String titleText = null;
    private String contentText = null;
    private String positiveText = null;
    private String negativeText = null;

    private String sendNumber;
    private String sendData;
    private boolean isOriginal= false;

    public MtouchSMSDialog(@NonNull Context context) {
        super(context);
        this.activity = (Activity) context;
    }

    public MtouchSMSDialog(@NonNull Context context, View.OnClickListener positiveListener) {
        super(context);
        this.activity = (Activity) context;
        this.mPositiveListener = positiveListener;
        this.mNegativeListener = null;
        setCancelable(true);
    }

    public MtouchSMSDialog(@NonNull Context context, View.OnClickListener positiveListener, boolean isCancelable) {
        super(context);
        this.activity = (Activity) context;
        this.mPositiveListener = positiveListener;
        this.mNegativeListener = null;
        setCancelable(isCancelable);
    }

    public MtouchSMSDialog(@NonNull Context context, View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        super(context);
        this.activity = (Activity) context;
        this.mPositiveListener = positiveListener;
        this.mNegativeListener = negativeListener;
        setCancelable(true);
    }

    public MtouchSMSDialog(@NonNull Context context, View.OnClickListener positiveListener, View.OnClickListener negativeListener, boolean isCancelable) {
        super(context);
        this.activity = (Activity) context;
        this.mPositiveListener = positiveListener;
        this.mNegativeListener = negativeListener;
        setCancelable(isCancelable);
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

        setContentView(R.layout.mtouch_dialog_sms_layout);

        //셋팅
        mPositiveButton = (Button) findViewById(R.id.confirmButton);
        mNegativeButton = findViewById(R.id.cancelButton);
        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        smsEditText = findViewById(R.id.smsEditText);

        if (positiveText != null && positiveText.length() > 0)
            mPositiveButton.setText(positiveText);
//        if (negativeText != null && negativeText.length() > 0)
//            mNegativeButton.setText(negativeText);
        if (titleText != null && titleText.length() > 0)
            titleTextView.setText(titleText);
        if (contentText != null && contentText.length() > 0)
            contentTextView.setText(contentText);
        if(sendNumber!=null && sendNumber.length()>0)
            smsEditText.setText(sendNumber);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton.setOnClickListener(v -> {
            dismiss();
            if(isOriginal){
                Util.sendOriginalTextSMS(activity,smsEditText.getText().toString(),sendData);
            }else {
                Util.sendSMS(activity, smsEditText.getText().toString(), sendData);
            }
            if (mPositiveListener != null) mPositiveListener.onClick(v);
        });
        if (mNegativeListener != null) {
            mNegativeButton.setVisibility(View.VISIBLE);
            mNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (mNegativeListener != null) mNegativeListener.onClick(v);
                }
            });
        } else {
            mNegativeButton.setVisibility(View.GONE);
        }

    }

    public MtouchSMSDialog setSendDataAndNumber(String data, String sendNumber) {
        this.sendData = data;
        this.sendNumber = sendNumber;
        return this;
    }

    public boolean isOriginal() {
        return isOriginal;
    }


    public MtouchSMSDialog setOriginal(boolean original) {
        isOriginal = original;
        return this;
    }

    public MtouchSMSDialog setSendData(String sendData) {
        this.sendData = sendData;
        return this;
    }

    public MtouchSMSDialog setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public MtouchSMSDialog setContentText(String contentText) {
        this.contentText = contentText;
        return this;
    }

    public MtouchSMSDialog setPositiveButtonText(String text) {
        positiveText = text;
        return this;
    }

    public MtouchSMSDialog setNegativeButtonText(String text) {
        negativeText = text;
        return this;
    }

}
