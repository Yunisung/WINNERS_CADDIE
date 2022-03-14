package com.bkwinners.ksnet.dpt.ks03.pay.inter;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.bkwinners.ksnet.dpt.ks03.pay.inter.imp.JsCallHandlerImp;

/**
 * Created by parksuwon on 2017-12-15.
 */


public class JsCallHandler implements  JsCallHandlerImp {

    Activity activity;
    String TAG = "JsCallHandler";
    WebView webView;


    public JsCallHandler(Activity _contxt, WebView _webView) {
        activity = _contxt;
        webView = _webView;
    }
    //sendPrint
    @JavascriptInterface
    public void sendPrint(String i) {

        final String webUrl = "javascript:sendPrint('"+i+"')";
        // Add this to avoid android.view.windowmanager$badtokenexception unable to add window
        if(!activity.isFinishing())
            // loadurl on UI main thread
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    webView.loadUrl(webUrl);
                }
            });
    }
    @JavascriptInterface
    public void sendBlueThooth(String i) {

        final String webUrl = "javascript:sendBlueThooth('"+i+"')";
        // Add this to avoid android.view.windowmanager$badtokenexception unable to add window
        if(!activity.isFinishing())
            // loadurl on UI main thread
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    webView.loadUrl(webUrl);
                }
            });
    }
    @JavascriptInterface
    public void payResult(String i) {
        if(Constants.IS_TEST)
        Log.d("debug","==================payResult==================" + i + "================================================");
        final String webUrl = "javascript:payResult('"+i+"')";
        // Add this to avoid android.view.windowmanager$badtokenexception unable to add window
        if(!activity.isFinishing())
            // loadurl on UI main thread
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    webView.loadUrl(webUrl);
                }
            });
    }
    @JavascriptInterface
    public void diplayJavaMsg(String i) {

        final String webUrl = "javascript:diplayJavaMsg('"+i+"')";
        // Add this to avoid android.view.windowmanager$badtokenexception unable to add window
        if(!activity.isFinishing())
            // loadurl on UI main thread
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    webView.loadUrl(webUrl);
                }
            });
    }

    @JavascriptInterface
    public void forceGoHome(){
        final String url = "javascript:forceGoHome()";
        if(!activity.isFinishing())
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });
    }

    @JavascriptInterface
    public void onBack(){
        final String url = "javascript:onBack()";
        if(!activity.isFinishing())
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });
    }
}
