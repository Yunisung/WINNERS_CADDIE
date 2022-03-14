package com.bkwinners.ksnet.dpt.design.appToApp;


public interface CommonInfo
{
    String CALLBACK_URL = "mtouch://appmodule"; // callback scheme(Manifest에 적용)
    String MS_DATA = "0100001234"; // 자진발급 시 고정번호 사용

    interface CODE {
        String getCode();
    }

    enum SERVICE implements CODE
    {
        VAN("van"),
        PG("pg");

        private String mode = "";

        SERVICE(String mode)
        {
            this.mode = mode;
        }

        public String getCode()
        {
            return mode;
        }
    }

    /**
     * 거래유형 - 1:신용결제 2:신용취소 3:현금결제 4:현금취소
     */
    enum PAYMENT_METHOD implements CODE
    {
        CREDIT_AUTH("1"),
        CREDIT_AUTH_CANCEL("2"),
        CASH_AUTH("3"),
        CASH_AUTH_CANCEL("4");

        private String code = "";

        PAYMENT_METHOD(String code)
        {
            this.code = code;
        }

        public String getCode()
        {
            return code;
        }
    }

    /**
     * 현금승인 옵션 - 0:소득공제 3:지출증빙 2:자진발급
     */
    enum CASH_OPTION implements CODE
    {
        INCOME_TAX("0"), // 소득공제
        SPEND_PROOF("1"), // 지출증빙
        SELF_ISSUE("2"); // 자진발급

        private String code = "";

        /**
         * @param code : 현금승인 옵션(0 : 소득공제, 1 : 지출증빙, 2 : 자진발급)
         */
        CASH_OPTION(String code)
        {
            this.code = code;
        }

        public String getCode()
        {
            return code;
        }
    }

    /**
     * 취소사유 - 1:거래취소 2:오류발급 3:기타
     */
    enum CASH_CANCEL_REASON implements CODE
    {
        TRADE_CANCEL("1"), // 거래취소
        ERROR_ISSUE("2"), // 오류발급
        ETC_CANCEL("3"); // 기타

        private String code = "";

        CASH_CANCEL_REASON(String code)
        {
            this.code = code;
        }

        public String getCode()
        {
            return code;
        }
    }

    /**
     * 거래유형 - (KEY_IN:"10", SWIPE:"20", IC:"30", FALLBACK:"60")
     */
    enum TRADE_TYPE implements CODE{
        KEY_IN("10"),
        SWIPE("20"),
        IC("30"),
        FALLBACK("60");

        private String code;

        TRADE_TYPE(String code) {this.code = code;}

        public String getCode(){return code;}
    }
    /*
        DUMMY_DATA
     */
    String DUMMY_DEVICE_ID = "1002189855";
    String DUMMY_BUSINESS_NO = "1138521083";
    String DUMMY_PG_DEVICE_ID = "9999990013";
    String DUMMY_PG_BUSINESS_NO = "1138521083";
    String DUMMY_SALE_CODE = "";
    String DUMMY_TOT_AMT = "1000";//총 금액
    String DUMMY_ORG_AMT = "909"; //과세금액
    String DUMMY_TAX_AMT = "91"; //세금
    String DUMMY_DUTY_AMT = "0";//면세금액
    String DUMMY_TAX_RATE = "10";//세율(SAMPLE 계산용으로 사용)
    String DUMMY_MS_DATA = "01012345678";//개인정보
}
