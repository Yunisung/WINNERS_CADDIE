package com.mtouch.ksnet.dpt;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;
import com.mtouch.ksnet.dpt.telegram.NotiAsyncTask;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ErrorActivity extends AppCompatActivity {
    public static final String EXTRA_ERROR_MESSAGE = "EXTRA_ERROR_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_error);

        LinearLayout error_layout = (LinearLayout) findViewById(R.id.error_layout);
        String errMsg = intent.getStringExtra(EXTRA_ERROR_MESSAGE);
        TextView tvErrorMsg = (TextView)findViewById(R.id.tv_error_msg);
        tvErrorMsg.setText(errMsg);

        try {
            JSONObject logJson = new JSONObject();
            logJson.put("message", "앱투앱 결제에러.");
            logJson.put("error",errMsg);
            logJson.put("data", getIntent().getExtras().toString());
            logJson.put("ksr03_version", BuildConfig.VERSION_NAME);
            logJson.put("os", Build.VERSION.SDK_INT + "");
            logJson.put("model",Build.MODEL + "");
            new NotiAsyncTask(this).execute(NotiAsyncTask.NOTI_CODE_TEST, logJson.toString());
        } catch (Exception ee){}

//        saveLog(errMsg);

        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("superzer", errMsg);
        clipboardManager.setPrimaryClip(clipData);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                takeScreenshot();
                Toast.makeText(ErrorActivity.this, "로그가 저장되고 복사되었습니다.\n스크린샷이 캡쳐되고 저장되었습니다.\n "+Environment.getExternalStorageDirectory()+"/test",Toast.LENGTH_SHORT).show();

            }
        },1000);

    }

    public void saveLog(String text) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        String filePath = Environment.getExternalStorageDirectory()+"/test/"+now+".txt";

        File superZerDir = new File(Environment.getExternalStorageDirectory()+"/test");

        if(! superZerDir.exists()){
            superZerDir.mkdirs();
        }

        File logFile = new File(filePath);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(filePath.length()>0) {
//                if (Build.VERSION.SDK_INT >= 24) { // Android Nougat ( 7.0 ) and later
//                    Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider",logFile);
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setDataAndType(uri, "application/*");
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    intent.putExtra(Intent.EXTRA_STREAM,uri);
//                    startActivity(Intent.createChooser(intent,"로그전송"));
//                } else {
//                    Uri uri = Uri.fromFile(logFile);
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("application/excel");
//                    intent.putExtra(Intent.EXTRA_STREAM,uri);
//                    startActivity(Intent.createChooser(intent,"로그전송"));
//                }



                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,text),"로그전송"));
            }
        }
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            File superZerDir = new File(Environment.getExternalStorageDirectory()+"/test");

            if(! superZerDir.exists()){
                superZerDir.mkdirs();
            }

            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/test/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

}
