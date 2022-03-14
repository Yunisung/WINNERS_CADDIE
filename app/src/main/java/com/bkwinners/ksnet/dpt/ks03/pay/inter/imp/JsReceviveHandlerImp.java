package com.bkwinners.ksnet.dpt.ks03.pay.inter.imp;

/**
 * Created by parksuwon on 2017-12-15.
 */

public interface JsReceviveHandlerImp {

    public String getData(String str);  // 웹에서 안드로이드에서 app호출    token version  phonenum bluetooth , printer  상황에 맞는 상태 값을 전달 합니다
    public void setData(String str, String value);  // 웹에서 안드로이드에서 app호출
    public void androidFinish() ;  // 어플  종료 합니다
    public void payStart(String str) ;  //  일시불 (숫자 두자리) , 승인금액
    public void sendPrint(String str) ;  //  거래일시 , 카드종류 , 카드번호 , 전표배입사 , 승인금액 승인번호
    public void refund(String str) ;  //  승인번호 , 거래일짜

    public void sendSMS(String str) ;  //  JSON 파일
    public void searchPrint() ;  //  프린터 검색

    public void sendCalcPrint(String str) ;  //  승인번호 , 거래일짜
    public void sendCalcSMS(String str) ;  //  승인번호 , 거래일짜

    public void clearBlueTooth();
    public void readerSetting();

}

