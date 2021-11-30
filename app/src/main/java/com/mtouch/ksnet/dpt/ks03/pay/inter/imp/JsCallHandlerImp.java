package com.mtouch.ksnet.dpt.ks03.pay.inter.imp;

/**
 * Created by parksuwon on 2017-12-15.
 */

public interface JsCallHandlerImp {

    public void sendPrint(String i);  // Status 0, 1
    public void sendBlueThooth(String i) ;  // Status 0, 1
    public void payResult(String i) ;  //  일시불 (숫자 두자리) , 승인금액
}

