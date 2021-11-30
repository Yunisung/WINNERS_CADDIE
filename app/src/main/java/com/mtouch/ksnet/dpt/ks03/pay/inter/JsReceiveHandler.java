package com.mtouch.ksnet.dpt.ks03.pay.inter;

/**
 * Created by parksuwon on 2017-12-15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.bluetooth.DeviceRegistActivity;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;
import com.mtouch.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus;
/**
 * Created by parksuwon on 2017-12-15.
 */

import android.widget.Toast;

import com.mtouch.ksnet.dpt.ks03.pay.inter.imp.JsReceviveHandlerImp;
import com.mtouch.ksnet.dpt.ks03.pay.ksnet.KSNETStatus;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;
import com.pswseoul.util.AndroidUtils;

import com.mtouch.ksnet.dpt.Toasty;

/**
 * Class to handle all calls from JS & from Java too
 **/
public class JsReceiveHandler implements JsReceviveHandlerImp

{
    Activity activity;
    String TAG = "JsReceiveHandler";
    WebView webView;

    Handler mHandler;

    public JsReceiveHandler(Activity _contxt,WebView _webView) {
        activity = _contxt;
        webView = _webView;
    }

    public JsReceiveHandler(Activity _contxt,WebView _webView, Handler handler) {
        activity = _contxt;
        webView = _webView;
        mHandler = handler;
    }

    /**
     * This function handles call from JS
     */
    @JavascriptInterface
    public void jsFnCall(String jsString) {
        showDialog(jsString);
    }

    /**
     * This function handles call from Android-Java
     */


    /**
     * function shows Android-Native Alert Dialog
     */
    @JavascriptInterface
    public void showDialog(String msg) {
    }

    @JavascriptInterface
    public String getData(String str) {
        String returnstr = "";

        if (str.indexOf("phonenum") >= 0)    {
            returnstr  = KSNETStatus.telNo;
            //return KSNETStatus.telNo;
        } else if (str.indexOf("token") >= 0) {
            returnstr  =AndroidUtils.getAppPreferences(activity, "token");
            KSNETStatus.token = returnstr;
      //      return AndroidUtils.getAppPreferences(activity, "token");
        } else if (str.indexOf("appId") >= 0) {
            returnstr  =  KSNETStatus.appId ;
      //      return KSNETStatus.appId ;
        } else if (str.indexOf("telNo") >= 0) {
            returnstr  =  KSNETStatus.telNo ;
//            return KSNETStatus.telNo ;
        } else if (str.toLowerCase().indexOf("version") >= 0) {
            returnstr  =  ""+ KSNETStatus.versionName ;
//            return KSNETStatus.telNo ;
        }else if(str.indexOf("serverDomain") >=0) {
            returnstr = BuildConfig.BASE_URL;
        }else if(str.indexOf("serverAPIDomain") >=0) {
            returnstr = BuildConfig.BASE_API_URL;
        }else if(str!=null && str.length()>0){
            returnstr  = AndroidUtils.getAppPreferences(activity, str);
        }

        if(Constants.IS_TEST)
        Log.d("debug","==============getData===============" + str + "==========" + returnstr  + "==========");

        return returnstr;
    }


    @JavascriptInterface
    public void setData(String key  , String value ) {

        if(key.indexOf("token") >=0)  {
            AndroidUtils.setAppPreferences(activity , key , value);
            KSNETStatus.token = value;
            Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_STORE_INFO);
            Bundle bundle = new Bundle();
            bundle.putString("data", value);
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        } else if(key.indexOf("tel") >=0)  {
            Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_TELL_CALL);
            Bundle bundle = new Bundle();
            bundle.putString("data", value);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        } else  {
            AndroidUtils.setAppPreferences(activity , key , value);
        }
        if(Constants.IS_TEST)
        Log.d("debug","setData : " + key  + ":" + value +"=====================================");
    }


    @JavascriptInterface
    public void androidFinish() {
        Toasty.error(activity , "어플을 종료합니다 " , Toast.LENGTH_SHORT);

        Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_FINISH);
        Bundle bundle = new Bundle();
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void cashPayStart(String json){
        if (Constants.IS_TEST){
            Log.d("debug", "cashPayStart=================================================");
            Log.d("debug", json);
        }

        try {
            Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_CASH_PAY);
            Bundle bundle = new Bundle();
            bundle.putString("data", json);
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        } catch ( Exception e) { e.printStackTrace(); }
    }


    @JavascriptInterface
    public void payStart(String str) {
        if (Constants.IS_TEST){
            Log.d("debug", "payStart=================================================");
            Log.d("debug", str);
        }
         try {
            Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_PAYSTART);
            Bundle bundle = new Bundle();
            bundle.putString("data", str);
            msg.setData(bundle);
            mHandler.sendMessage(msg);

        } catch ( Exception e) { e.printStackTrace(); }
    }

    @JavascriptInterface
    public void sendPrint(String str) {
        if(Constants.IS_TEST)
        Log.d("debug","sendPrint : " + str);
        try {
            Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_SENDPRINT);
            Bundle bundle = new Bundle();
            bundle.putString("data", str);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        } catch ( Exception e) { e.printStackTrace(); }
    }

    @JavascriptInterface
    public void refund(String str) {
        if(Constants.IS_TEST)
       Log.d("tag", "str =================refund=============="+ str);
//        data={"trxId":"T171216000014","result":"취소승인","amount":50000,"van":"KSPAY1","vanId":"2823200001","authCd":"005748","trackId":"TX171217000045","regDay":"20171216","secondKey":"DPT0A08808"}}]
        Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_REFUND);
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void sendSMS(String str ) {
        if(Constants.IS_TEST)
        Log.d("tag", "str =================sendSMS=============="+ str);
        Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_SMS);
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void searchPrint( ) {
        Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_SEARCH_PRINTER);
        Bundle bundle = new Bundle();
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void sendCalcPrint( String str ) {
        if(Constants.IS_TEST)
        Log.d("tag", "str =================sendCalcPrint=============="+ str);
        Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_CALC_PRINTER);
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void sendCalcSMS(String str ) {
        if(Constants.IS_TEST)
        Log.d("tag", "str =================sendCalcSMS=============="+ str);
        Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_CALC_SMS);
        Bundle bundle = new Bundle();
        bundle.putString("data", str);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @JavascriptInterface
    public void clearBlueTooth() {
        SharedPreferenceUtil.putData(activity,Constants.KEY_MAC_ADDRESS,"NONE");
    }

    @JavascriptInterface
    public void readerSetting(){
        SharedPreferenceUtil.putData(activity,Constants.KEY_MAC_ADDRESS,"NONE");
        activity.startActivityForResult(new Intent(activity, DeviceRegistActivity.class), 1);
    }
}