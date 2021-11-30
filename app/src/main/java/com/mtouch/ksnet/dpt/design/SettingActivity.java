package com.mtouch.ksnet.dpt.design;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.caddie.TempActivity;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.ksnet.dpt.MainApplication;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.bluetooth.DeviceRegistActivity;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;
import com.mtouch.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;
import com.nordicsemi.nrfUARTv2.UartService;


public class SettingActivity extends DeviceCheckActivity {

    private ImageView backButton;
    private TextView headerTitleTextView;

    private TextView versionTextView;
    private TextView terminalIdTextView;
    private TextView mchtNameTextView;
    private TextView bizNumberTextView;
    private Button printerSearchButton;
    private Button readerSearchButton;
    private Button resetButton;
    private Button btConnectSettingButton;
    private View connectSettingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if(BuildConfig.DEBUG)test();
        bindViews();
        init();
    }
    private void test(){
        findViewById(R.id.test).setOnClickListener(v->{
            startActivity(new Intent(this, TempActivity.class));
        });
    }

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);

        versionTextView = findViewById(R.id.versionTextView);
        terminalIdTextView = findViewById(R.id.terminalIdTextView);
        mchtNameTextView = findViewById(R.id.mchtNameTextView);
        bizNumberTextView = findViewById(R.id.bizNumberTextView);
        printerSearchButton = findViewById(R.id.printerSearchButton);
        readerSearchButton = findViewById(R.id.readerSearchButton);
        resetButton = findViewById(R.id.resetButton);
        btConnectSettingButton = findViewById(R.id.btConnectSettingButton);
        connectSettingView = findViewById(R.id.connectSettingView);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("설정");

        printerSearchButton.setOnClickListener(v->{
            Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_SEARCH_PRINTER);
            Bundle bundle = new Bundle();
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        });
        readerSearchButton.setOnClickListener(v->{
            SharedPreferenceUtil.putData(this, Constants.KEY_MAC_ADDRESS,"NONE");
            startActivityForResult(new Intent(this, DeviceRegistActivity.class), REQUEST_CODE_REGIST_ACTIVITY);
        });

        resetButton.setOnClickListener(v->{
            new MtouchDialog(this, vv->{
                SharedPreferenceUtil.clearData(this);
                SharedPreferenceUtil.clearData(this);
                if(NetworkManager.cookieJar!=null){
                    NetworkManager.cookieJar.clear();
                }
                setResult(RESULT_OK);
                finish();
            }).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setTitleText("알림").setContentText("정말로 초기화 하시겠습니까?").show();

        });



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
    }

    private void init() {
        setNeedRegist(false);
        initialize();

        versionTextView.setText(BuildConfig.VERSION_NAME);
        terminalIdTextView.setText( SharedPreferenceUtil.getData(this, "tmnId"));
        mchtNameTextView.setText(SharedPreferenceUtil.getData(this, "name"));
        bizNumberTextView.setText(SharedPreferenceUtil.getData(this, "identity"));

        connectSettingView.setSelected(SharedPreferenceUtil.getData(this, Constants.KEY_KEEP_CONNECTION, "false").equals("true"));
    }
}
