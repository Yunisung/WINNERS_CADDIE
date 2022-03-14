package com.bkwinners.ksnet.dpt.design;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bkwinners.ksnet.dpt.MainApplication;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
import com.nordicsemi.nrfUARTv2.UartService;

public class HiddenOptionActivity extends AppCompatActivity {

    private Button btConnectSettingButton;
    private View connectSettingView;
    private Button paymentLocalHistoryButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_option);

        bindViews();
        init();
    }

    private void bindViews() {
        btConnectSettingButton = findViewById(R.id.btConnectSettingButton);
        paymentLocalHistoryButton = findViewById(R.id.paymentLocalHistoryButton);
        connectSettingView = findViewById(R.id.connectSettingView);
    }

    private void init() {
        connectSettingView.setSelected(SharedPreferenceUtil.getData(this, Constants.KEY_KEEP_CONNECTION, "false").equals("true"));
        btConnectSettingButton.setOnClickListener(v -> {
            String address = SharedPreferenceUtil.getData(this, Constants.KEY_MAC_ADDRESS, "NONE");
            if (address == null || address.equals("NONE")) {
                new MtouchDialog(this).setContentText("리더기를 먼저 등록하시기 바랍니다.").show();
                return;
            }

            boolean isSettingKeepConnecting = SharedPreferenceUtil.getData(this, Constants.KEY_KEEP_CONNECTION, "false").equals("true");


            if (!isSettingKeepConnecting) {
                new MtouchDialog(this, view -> {
                    SharedPreferenceUtil.putData(this, Constants.KEY_KEEP_CONNECTION, true + "");
                    connectSettingView.setSelected(true);

                    Toast.makeText(this, "블루투스 연결유지 설정되었습니다.", Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(this, UartService.class));
                    } else {
                        startService(new Intent(this, UartService.class));
                    }
                }, view -> {

                }).setContentText("블루투스 연결유지 설정을 하시겠습니까?\n리더기와 단말기의 전원이 부족한 경우에는 연결이 불안정할 수 있습니다.").show();


            } else {
                SharedPreferenceUtil.putData(this, Constants.KEY_KEEP_CONNECTION, "false");
                connectSettingView.setSelected(false);
                Intent intent = new Intent(this, UartService.class);
                intent.putExtra(UartService.KEY_ACTION, UartService.ACTION_STOP);
                startService(intent);

                Toast.makeText(this, "블루투스 연결유지 해제되었습니다.", Toast.LENGTH_SHORT).show();

                ((MainApplication) getApplication()).setIsBlueToothConnect(false);
            }
        });


        paymentLocalHistoryButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LocalHistoryActivity.class));
            finish();
        });
    }
}