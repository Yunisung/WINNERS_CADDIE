package com.mtouch.ksnet.dpt.ks03.pay.ksnet;

/**
 * Created by parksuwon on 2017-12-13.
 */

public class CyrexNetworkStatus {

    public static int menu_status = -1;

    public static boolean PHONE = true;
    public static boolean TEST = false;

    public static boolean KSNET_APP_CALL = false;

    public static final int KSNET_APP_CALL_EXPIRE_TIME = 1000 * 60 * 2;

    public static final int NETWORK_APP_VERSION = 50;  // NETWORK_APP_VERSION
    public static final int NETWORK_REGISTRY_STORE = 100;   // KEY  등록시 가맹점 이름 조회  REGISTRY_STORE
    public static final int NETWORK_REGISTRY_APP = 200;    // 어플 등록 및 KEY 부여         REGISTRY_APP
    public static final int NETWORK_AVALABLE_KEY = 300;   // KEY 유효성 확인               AVALABLE_KEY
    public static final int NETWORK_BASIC_VALUE = 400;    // 기본 통계                     BASIC_VALUE
    public static final int NETWORK_APPROVE_CEHCK = 500;  // 승인 이전에 확인              APPROVE_CEHCK

    public static final int NETWORK_FAIL_CEHCK = 510;  // 승인 이전에 확인              APPROVE_CEHCK
    public static final int NETWORK_SERVER_SEND = 600;
    public static final int NETWORK_SERVER_SEND_FAIL = 700;

    /*
    Handler Event
     */
    public static final int  HANDLER_GFETDATA_KEY = 50001;
    public static final int  HANDLER_FINISH       = 50002;
    public static final int  HANDLER_PAYSTART     = 50003;
    public static final int  HANDLER_SENDPRINT    = 50004;
    public static final int  HANDLER_REFUND       = 50005;

    public static final int  HANDLER_RETRY_PRINT      = 50006;
    public static final int  HANDLER_SMS      = 50007;
    public static final int  HANDLER_SEARCH_PRINTER      = 50008;
    public static final int  HANDLER_STORE_INFO      = 50009;

    public static final int  HANDLER_APPROVE_RESULT_INFO      = 50010;


    public static final int  HANDLER_CALC_PRINTER      = 50011;  // 웹에서 프린터 처리
    public static final int  HANDLER_CALC_SMS      = 50012;   // 웹에서 문자 처리

    public static final int  HANDLER_TELL_CALL = 50013;  // 전화걸기
    public static final int  HANDLER_CASH_PAY     = 50014;
}
