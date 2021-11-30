package com.mtouch.ksnet.dpt.ks03.obj;

import com.pswseoul.util.AndroidUtils;
import com.pswseoul.util.GsonUtil;

/**
 * Created by parksuwon on 2017-12-13.
 */

public class KsnetPrnObj {

    public String startDay;
    public String endDay;
    public String payCnt;
    public String payAmt;
    public String rfdCnt;
    public String rfdAmt;
    public String cnt;
    public String amount;
    public String result;

    public String toJsonString(){
        return  GsonUtil.toJson(this,true,"");
    }

    public String PrnTempleteData() {

        StringBuffer sb = new StringBuffer();
        sb.append(AndroidUtils. LF);
        sb.append(AndroidUtils.Line);
        sb.append(AndroidUtils.LF);
        sb.append("시 작 일 자: " + startDay);
        sb.append(AndroidUtils.LF);
        sb.append("종 료 일 자: " + endDay);
        sb.append(AndroidUtils.LF);
        sb.append("승 인 건 수: " + payCnt);
        sb.append(AndroidUtils.LF);
        sb.append("승 인 금 액: " + payAmt);
        sb.append(AndroidUtils.LF);
        sb.append("취 소 건 수: " + rfdCnt);
        sb.append(AndroidUtils.LF);
        sb.append("취 소 금 액: " + rfdAmt);
        sb.append(AndroidUtils.LF);
        sb.append("합 계 건 수: " + cnt);
        sb.append(AndroidUtils.LF);
        sb.append("합 계 금 액: " + amount);
        sb.append(AndroidUtils. LF);
        sb.append(AndroidUtils.Line);
        sb.append(AndroidUtils. LF);
        sb.append("당일 내역은 실 거래 내역과 다를 수 있습니다");

        sb.append(AndroidUtils.DLF);sb.append(AndroidUtils.LF);

        /*
        sb.append(Line);sb.append(LF);
        sb.append("판 매 금 액: "+ (SPACE+Amount).substring(((SPACE+Amount).length()-substringlen)) );sb.append(LF);
        sb.append("부 가 세 액: "+ (SPACE+TaxAmount).substring(((SPACE+TaxAmount).length()-substringlen))  );sb.append(LF);
        sb.append("봉 사 료 액: "+ (SPACE).substring(((SPACE).length()-substringlen)) );sb.append(LF);
        sb.append("합 계 금 액: "+ (SPACE+TotalAmount).substring(((SPACE+TotalAmount).length()-substringlen)) );sb.append(LF);
        sb.append(Line);sb.append(DLF);sb.append(DLF);

        sb.append("고 객 서 명 란 : 무서명거래");sb.append(DLF);
        sb.append("발행일시 : "+tools.getMysqlDate() );sb.append(DLF);
      */
        return sb.toString();
    }

    private String numberFormat(int number) {
       return  (AndroidUtils.SPACE+String.format("%,d", number)).substring(((AndroidUtils.SPACE+String.format("%,d", number)).length()-AndroidUtils.substringlen));
    }

}
