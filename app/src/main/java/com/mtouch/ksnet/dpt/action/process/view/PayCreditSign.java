package com.mtouch.ksnet.dpt.action.process.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mtouch.ksnet.dpt.action.process.ksnetmodule.util.SignToString;
import com.mtouch.ksnet.dpt.action.process.sign.SignView;

import com.pswseoul.util.AndroidUtils;
import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;

/**
 * Created by parksuwon on 2018-02-03.
 */

@SuppressLint({"HandlerLeak"})
public class PayCreditSign extends Activity {

    private TextView txtsaleamount;
    private SignView singview;

    private SignToString f1814d;

    private ProgressDialog prog;

    public void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        requestWindowFeature(1);
//        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.signpad);
    //    getWindow().getAttributes().width = (int) (((double) ((WindowManager) getSystemService("window")).getDefaultDisplay().getWidth()) * 0.95d);

        this.txtsaleamount = (TextView) findViewById(R.id.txtSaleAmount);
        this.singview = (SignView) findViewById(R.id.signView);

        Intent i = getIntent();
        txtsaleamount.setText(  "금액 : " + AndroidUtils.toNumFormat(Integer.parseInt(i.getStringExtra("amount") ) )  + "원");
    }

    public void mOnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                if (singview.isSign()) {
                    String signStr= new SignToString().getBitString(singview.getSign());
                    if(signStr == null) {
                        new AlertDialog.Builder(this)
                                .setTitle("전자서명")
                                .setMessage("사인패드에 서명해주시기 바랍니다!")
                                .setCancelable(false)
                                .setIcon(R.drawable.warning)
                                .setPositiveButton("확인", null)
                                .show();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("data", signStr);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    // 이곳에서 사인에서 얻을 값을 전달 하는 곳입니다
                } else {
                    new AlertDialog.Builder(this)
                             .setTitle("전자서명")
                            .setMessage("사인패드에 서명해주시기 바랍니다!")
                            .setCancelable(false)
                            .setIcon(R.drawable.warning)
                            .setPositiveButton("확인", null)
                            .show();
                }
                return;
            default:
                return;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", "NOT");
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

}
