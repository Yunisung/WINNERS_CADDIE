package com.mtouch.ksnet.dpt.ks03.obj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

import com.mtouch.ksnet.dpt.action.process.ksnetmodule.EncMSRManager;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.pswseoul.util.AndroidUtils;
import com.pswseoul.util.GsonUtil;
import com.pswseoul.util.SunUtil;
import com.pswseoul.util.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by parksuwon on 2017-12-13.
 */

public class KsnetResponseObj {
   /*
    public byte[] Classification ;
    public byte[] TelegramType ;
    public byte[] Dpt_Id ;
    public byte[] Enterprise_Info ;
    public byte[] Full_Text_Num ;
    public byte[] Status ;
    public byte[] Authdate ;
    public byte[] Message1 ;
    public byte[] Message2 ;
    public byte[] AuthNum ;
    public byte[] FranchiseID ;
    public byte[] IssueCode ;
    public byte[] CardName ;
    public byte[] PurchaseCode ;
    public byte[] PurchaseName ;
    public byte[] Remain ;
    public byte[] point1 ;
    public byte[] point2 ;
    public byte[] point3 ;
    public byte[] notice1 ;
    public byte[] notice2 ;
    public byte[] CardNo ;

    public String prevAuthNum = "";
    public String prevAuthDate = "";
    public String prevClassfication = "";
*/

    public HashMap<String, String> data = new HashMap<String, String>();

    public void put(String key, String value) {
        data.put(key, value);
    }

    public HashMap getMap() {
        return data;
    }

    public HashMap getByteMap() {
        HashMap<String, byte[]> m_byte_hash = new HashMap<String, byte[]>();
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            m_byte_hash.put(key, data.get(key).getBytes());
        }
        return m_byte_hash;
    }

    public void setMap(HashMap m_hash) {
        this.data.clear();

        Iterator<String> iterator = m_hash.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            this.data.put(key, (String) m_hash.get(key));
        }
    }

    public void clearHash() {
        data.clear();
    }

    public String toJsonString() {
        return GsonUtil.toJson(this, true, "");
    }
   /*
        m_hash.put ("Message1" , "우리카드" );
        m_hash.put ("CardNo" , "410120**********");
        m_hash.put ("TotalAmount" , "1004");
        m_hash.put ("PurchaseName" , "비씨카드사");
        m_hash.put ("AuthNum" , "66637564");
        m_hash.put ("FranchiseID" , "753657369");
        m_hash.put ("Authdate" , "171214142801");
       */


//    public Bitmap PrnBmpTempleteData(int approve, String store_name, String name , String sauup, String telNo , String addr ) {
//        int TotalAmount = Integer.parseInt(new String(data.get("TotalAmount")));  //총금액
//        int TaxAmount = (int) ((TotalAmount / 1.1) * 0.1);     // 부가세
//        //if(vat.equals("N")) TaxAmount = 0;
//        int Amount = Math.round(TotalAmount - TaxAmount); // 공급가액
//
//String  installment = "";
//        if(data.get("installment").equals("00"))
//            installment = "일시불";
//        else
//            installment =data.get("installment")+"개월";
//
//
//        if (approve == 1)   //음수
//        {
//            TotalAmount = -TotalAmount;
//            Amount = -Amount;
//            TaxAmount = -TaxAmount;
//        }
//
//
//        ReceiptBuilder receipt = new ReceiptBuilder(1200);
//        receipt.setMargin(30, 20).
//                setAlign(Paint.Align.CENTER).
//                setColor(Color.BLACK).
//                setTextSize(100).
//                addText("［신용카드전표］").
//                addParagraph().
//                setTextSize(60).
//                setAlign(Paint.Align.LEFT). addText("가 맹 점 명: ",false).
//                setAlign(Paint.Align.RIGHT). addText(store_name).
//                setAlign(Paint.Align.LEFT). addText("대 표 자 명: ",false).
//                setAlign(Paint.Align.RIGHT). addText(name).
//                setAlign(Paint.Align.LEFT). addText("사업자 번호: ",false).
//                setAlign(Paint.Align.RIGHT). addText(sauup).
//                setAlign(Paint.Align.LEFT). addText("전 화 번 호: ",false).
//                setAlign(Paint.Align.RIGHT). addText(telNo).
//                setAlign(Paint.Align.LEFT). addText("주       소: "+addr,false).
//                addParagraph().
//
//                addText("*** 신용승인정보 ***").
//                setAlign(Paint.Align.LEFT). addText("거래  일시: ",false).
//                setAlign(Paint.Align.RIGHT). addText(tools.getMysqlDate()).
//                setAlign(Paint.Align.LEFT). addText("카드  종류: ",false).
//                setAlign(Paint.Align.RIGHT). addText(data.get("Message1")).
//                setAlign(Paint.Align.LEFT). addText("카드  번호: ",false).
//                setAlign(Paint.Align.RIGHT). addText(data.get("CardNo")).
//                addParagraph().
//                setAlign(Paint.Align.LEFT). addText("카드  번호: ",false).
//                setAlign(Paint.Align.RIGHT). addText(data.get("CardNo")).
//                setAlign(Paint.Align.LEFT). addText("결제  방법: ",false).
//                setAlign(Paint.Align.RIGHT). addText(data.get("installment")).
//
//
//                setAlign(Paint.Align.LEFT). addText("금      액: ",false).
//                setAlign(Paint.Align.RIGHT). addText(""+Amount).
//                setAlign(Paint.Align.LEFT). addText("부  가  세: ",false).
//                setAlign(Paint.Align.RIGHT). addText(""+TaxAmount).
//                setAlign(Paint.Align.LEFT). addText("합      계: ",false).
//                setAlign(Paint.Align.RIGHT). addText(""+TotalAmount).
//                addParagraph().
//
//                setAlign(Paint.Align.LEFT). addText("승인  번호: ",false).
//                setAlign(Paint.Align.RIGHT). addText( data.get("AuthNum")).
//
//                addParagraph().
//                addParagraph().
//                addParagraph().
//                addParagraph().
//                addLine();
//
//        return receipt.build();
//    }

    public String printCashReceipt(int approve, boolean isCashCard) {

        int TotalAmount = Integer.parseInt(new String(data.get("TotalAmount")));  //총금액
        int TaxAmount = (int) ((TotalAmount / 1.1) * 0.1);     // 부가세
        int Amount = Math.round(TotalAmount - TaxAmount); // 공급가액

        StringBuffer sb = new StringBuffer();
        sb.append(SunUtil.LF);
        sb.append(SunUtil.Line);
        sb.append(SunUtil.LF);

        sb.append("     *** 신용승인정보 ***");
        sb.append(SunUtil.DLF);

        String pattern = "yyMMddhhmmss";
        try {
            Date authDate = new SimpleDateFormat(pattern).parse(data.get("Authdate"));
            sb.append("거래  일시: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(authDate) + "\n", 0, 0);
            sb.append(SunUtil.LF);
        } catch (Exception e) {
        }


        sb.append("카드  종류: " + data.get("Message1"));
        sb.append(SunUtil.LF);
        if (data.get("CardNo") != null) {
            sb.append("카드  번호: " + data.get("CardNo"));
            sb.append(SunUtil.LF);
        }
//        if(data.get("installment").equals("00"))
//            sb.append("결제  방법: 일시불");
//        else
//            sb.append("결제  방법: "+ data.get("installment")+"개월");
//        sb.append(SunUtil.LF);
//        sb.append("전표매입사: " + data.get("PurchaseName"));
//        sb.append(SunUtil.LF);
        if (approve == 0)   //양수
        {
            sb.append("판매  금액: " + numberFormat(Amount));
            sb.append(SunUtil.LF);
            sb.append("부  가  세: " + numberFormat(TaxAmount));
            sb.append(SunUtil.LF);
            sb.append("승인  금액: " + numberFormat(TotalAmount));
        } else if (approve == 1) {  //음수
            sb.append("판매  금액: " + numberFormat(-Amount));
            sb.append(SunUtil.LF);
            sb.append("부  가  세: " + numberFormat(-TaxAmount));
            sb.append(SunUtil.LF);
            sb.append("승인  금액: " + numberFormat(-TotalAmount));
        }
        sb.append(SunUtil.LF);
        sb.append("승인  번호: " + data.get("AuthNum"));
        sb.append(SunUtil.LF);
        sb.append(data.get("notice2"));
        sb.append(SunUtil.LF);
//      sb.append("[유효  기간]: "+ store_name );sb.append(LF);
//      sb.append("[숭인  일시]: "+  new String(m_hash.get("Authdate"))); sb.append(LF);
//      sb.append("[가맹점번호]: "+  new String(m_hash.get("FranchiseID"))); sb.append(LF);

        sb.append(SunUtil.DLF);
        sb.append(SunUtil.LF);

        return sb.toString();
    }

    public String PrnTempleteData (Context context,int approve, String vat) {
        String store_name = SharedPreferenceUtil.getData(context, "name");
        String name = SharedPreferenceUtil.getData(context, "ceoName");
        String sauup = SharedPreferenceUtil.getData(context, "identity");
        String telNo = SharedPreferenceUtil.getData(context, "telNo");
        String addr = SharedPreferenceUtil.getData(context, "addr");

        return PrnTempleteData(approve,store_name,name,sauup,telNo,addr,vat);
    }

    public String PrnTempleteData(int approve, String store_name,
                                  String name,
                                  String sauup,
                                  String telNo,
                                  String addr, String vat) {

            int TotalAmount = Integer.parseInt(new String(data.get("TotalAmount")));  //총금액
            int TaxAmount = (int) ((TotalAmount / 1.1) * 0.1);     // 부가세
            if ("N".equals(vat)) TaxAmount = 0;
            int Amount = Math.round(TotalAmount - TaxAmount); // 공급가액

            StringBuffer sb = new StringBuffer();
            sb.append(SunUtil.LF);
            sb.append(SunUtil.Line);
            sb.append(SunUtil.LF);
//        sb.append("[영수중] "+ tools.getConcorrentTime() );sb.append(LF);
//        sb.append(Line);sb.append(LF);
            sb.append("가 맹 점 명: " + store_name);
            sb.append(SunUtil.LF);
            sb.append("대 표 자 명: " + name);
            sb.append(SunUtil.LF);
            sb.append("사업자 번호: " + sauup);
            sb.append(SunUtil.LF);
            sb.append("전 화 번 호: " + telNo);
            sb.append(SunUtil.LF);
            sb.append("주       소: " + addr);
            sb.append(SunUtil.LF);
            sb.append(SunUtil.Line);
            sb.append(SunUtil.LF);

            sb.append("     *** 신용승인정보 ***");
            sb.append(SunUtil.DLF);

            String pattern = "yyMMddhhmmss";
            try {
                Date authDate = new SimpleDateFormat(pattern).parse(data.get("Authdate"));
                sb.append("거래  일시: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(authDate) + "\n", 0, 0);
                sb.append(SunUtil.LF);
            } catch (Exception e) {
            }
//        sb.append("거래  일시: " + tools.getMysqlDate());
//        sb.append(SunUtil.LF);
            sb.append("카드  종류: " + data.get("Message1"));
            sb.append(SunUtil.LF);
            sb.append("카드  번호: " + data.get("CardNo"));
            sb.append(SunUtil.LF);
            if (data.get("installment").equals("00"))
                sb.append("결제  방법: 일시불");
            else
                sb.append("결제  방법: " + data.get("installment") + "개월");
            sb.append(SunUtil.LF);
            sb.append("전표매입사: " + data.get("PurchaseName"));
            sb.append(SunUtil.LF);
            if (approve == 0)   //양수
            {
                sb.append("판매  금액: " + numberFormat(Amount));
                sb.append(SunUtil.LF);
                sb.append("부  가  세: " + numberFormat(TaxAmount));
                sb.append(SunUtil.LF);
                sb.append("승인  금액: " + numberFormat(TotalAmount));
            } else if (approve == 1) {  //음수
                sb.append("판매  금액: " + numberFormat(-Amount));
                sb.append(SunUtil.LF);
                sb.append("부  가  세: " + numberFormat(-TaxAmount));
                sb.append(SunUtil.LF);
                sb.append("승인  금액: " + numberFormat(-TotalAmount));
            }
            sb.append(SunUtil.LF);
            sb.append("승인  번호: " + data.get("AuthNum"));
            sb.append(SunUtil.LF);
//      sb.append("[유효  기간]: "+ store_name );sb.append(LF);
//      sb.append("[숭인  일시]: "+  new String(m_hash.get("Authdate"))); sb.append(LF);
//      sb.append("[가맹점번호]: "+  new String(m_hash.get("FranchiseID"))); sb.append(LF);

            sb.append(SunUtil.DLF);
            sb.append(SunUtil.LF);

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

        public String PrnData () {
            StringBuffer sb = new StringBuffer();

            Iterator<String> iterator = data.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                sb.append("key=" + key);
                sb.append(" value=" + getString(key));
                sb.append(AndroidUtils.LF);
            }
            sb.append(AndroidUtils.DLF);
        /*
        sb.append(new String(Classification));sb.append(LF);
        sb.append(new String(TelegramType));sb.append(LF);
        sb.append(new String(Dpt_Id));sb.append(LF);
        sb.append(new String(Enterprise_Info));sb.append(LF);
        sb.append(new String(Full_Text_Num));sb.append(LF);
        sb.append(new String(Status));sb.append(LF);
        sb.append(new String(Authdate));sb.append(LF);
        sb.append(new String(Message1));sb.append(LF);
        sb.append(new String(Message2));sb.append(LF);
        sb.append(new String(AuthNum));sb.append(LF);
        sb.append(new String(FranchiseID));sb.append(LF);
        sb.append(new String(IssueCode));sb.append(LF);
        sb.append(new String(CardName));sb.append(LF);
        sb.append(new String(PurchaseCode));sb.append(LF);
        sb.append(new String(PurchaseName));sb.append(LF);
        sb.append(new String(Remain));sb.append(LF);
        sb.append(new String(point1));sb.append(LF);
        sb.append(new String(point2));sb.append(LF);
        sb.append(new String(point3));sb.append(LF);
        sb.append(new String(notice1));sb.append(LF);
        sb.append(new String(notice2));sb.append(LF);
        sb.append(new String(CardNo));sb.append(DLF);
*/
            return sb.toString();

        }

        public String getString (String key){
            return data.get(key);
        }

        private String numberFormat ( int number){
            return (AndroidUtils.SPACE + String.format("%,d", number)).substring(((AndroidUtils.SPACE + String.format("%,d", number)).length() - AndroidUtils.substringlen));
        }

    }
