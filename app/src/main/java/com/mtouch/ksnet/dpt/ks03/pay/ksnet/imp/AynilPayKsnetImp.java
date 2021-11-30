package com.mtouch.ksnet.dpt.ks03.pay.ksnet.imp;

/**
 * Created by parksuwon on 2017-12-15.
 */

public interface AynilPayKsnetImp {
    public void initWebSetting();     // webView의 settting 부분을 맡고 있습니다

    public void initWebView();     // webView를 구동하는 부분 입니다

    public void BTPrinter();  // 프린터 체크

    public void sendMessage(int menustatus, String data); // 네트워크 데이터를 송출 하는 부분 입니다

    public void ksNetMessage(int menustatus, String data); //  Ksnet 과 승인 및 취소를 처리하는 부분 입니다

}
