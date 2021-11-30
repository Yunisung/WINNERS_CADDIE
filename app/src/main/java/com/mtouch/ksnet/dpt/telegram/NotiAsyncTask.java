package com.mtouch.ksnet.dpt.telegram;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotiAsyncTask extends AsyncTask {
    public static final int NOTI_CODE_PAY_ERROR = 1000;
    public static final int NOTI_CODE_APP_ERROR = 2000;
    public static final int NOTI_CODE_WALLET_REQUEST = 3000;
    public static final int NOTI_CODE_TEST = 4000;

    public static final String NOTI_TOKEN_1 = "1122224441:AAGkfCbmjAldtsRCO5XTXDrmE3JZvFDLo9E"; //Q2
    public static final String NOTI_TOKEN_2 = "879585948:AAGNIQQFJzb9fmbDERPD6uSy-oGtm9Mverw"; //xpda
    public static final String NOTI_TOKEN_3 = "1349736084:AAEzATmd3Ck_7Gk5YaYRI2CIXA2qtLPkpUo"; //ksr03

    public static final String NOTI_CHAT_1 = "-1001283887537"; //결제누락방
    public static final String NOTI_CHAT_2 = "-469752466"; // 앱에러
    public static final String NOTI_CHAT_3 = "-336483654"; // 월렛 분리정산
    public static final String NOTI_CHAT_4 = "-1001365196696"; // 테스트알림


    Bundle bundle;
    Object[] pdus;
    String Message = "";
    Context context = null;

    private static String recentData = "";

    public NotiAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if ((int) params[0] == NOTI_CODE_PAY_ERROR) {
            SendMessage(NOTI_CHAT_1, (String) params[1]);
        } else if ((int) params[0] == NOTI_CODE_APP_ERROR) {
            SendMessage(NOTI_CHAT_2, (String) params[1]);
        } else if ((int) params[0] == NOTI_CODE_WALLET_REQUEST) {
            SendMessage(NOTI_CHAT_3, (String) params[1]);
        } else if((int) params[0] == NOTI_CODE_TEST){
            SendMessage(NOTI_CHAT_4, (String) params[1]);
        }
        return null;
    }

    private void SendMessage(String chatId, String string) {
        try {
            TelegramBot bot = new TelegramBot(NOTI_TOKEN_3);
            SendMessage request = new SendMessage(chatId, string);
            bot.execute(request);




        //todo test
        if(true) return;

            File mtouchDir = new File(Environment.getExternalStorageDirectory().getPath() + "/mtouch");
            File tempFile = new File(mtouchDir.getPath()+"/log.txt");

            if (!mtouchDir.exists()) {
                mtouchDir.mkdirs();
            }
            if(!tempFile.exists()){
                tempFile.createNewFile();
            }

            File logFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/Kcp/Log");
            String fileName = "main.zip";

            for (File file : logFolder.listFiles()) {
                if (file.getName().contains("main")) {
                    fileName = file.getName();
                    break;
                }
            }

            File mainFile = new File(Environment.getExternalStorageDirectory().getPath() + "/wizarViewAgent/wizarpos/log/" + fileName);
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/wpmonitor/logcat/logcat.txt");
            File logFileZip = new File(Environment.getExternalStorageDirectory().getPath() + "/wpmonitor/logcat/logcat.zip");

            if(mainFile.exists() && logFile.exists()) {

                //둘다 있을경우 날짜체크
                long mainFileDate = mainFile.lastModified();
                long logFileDate = logFile.lastModified();

                if(mainFileDate < logFileDate){
                    uploadTempFile(bot,chatId,logFile,tempFile);
                }else{
                    uploadTempFile(bot,chatId,mainFile,tempFile);
                }


            }else if (mainFile.exists()) {
                uploadTempFile(bot,chatId,mainFile,tempFile);
            } else {
                if (logFile.exists()) {
                    uploadTempFile(bot,chatId,logFile,tempFile);
                }else if(logFileZip.exists()){
                    uploadTempFile(bot,chatId,logFileZip,tempFile);
                }
            }


//                Process du = Runtime.getRuntime().exec("grep -r " + name);
//                Process du = Runtime.getRuntime().exec("getprop " + name);
//                BufferedReader in = new BufferedReader(new InputStreamReader(du.getInputStream()));
//                String value = in.readLine();
//                in.close();
//                return value;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void uploadTempFile(TelegramBot bot, String chatId, File saveFile, File tempFile){
//        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        int dayLogSize = 0;
//        try {
//            dayLogSize = Integer.parseInt(SharedPreferenceUtil.getData(context, date, "0"));
//        } catch (Exception e) {
//        }
//
//        if (dayLogSize > 5000) {
//            LOG.i("log size limit");
//            return;
//        }
//
//        try {
//            FileHelper.copyFile(saveFile,tempFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (tempFile.length() < 1024 * 1024 * 5) { //5MB 미만 로그 업로드.
//            dayLogSize += tempFile.length() / 1024;
//            SendDocument requestFile = new SendDocument(chatId, tempFile);
//            requestFile.caption(Build.SERIAL);
//            bot.execute(requestFile);
//            SharedPreferenceUtil.putData(context, date, String.valueOf(dayLogSize));
//        }
    }
}
