package com.bkwinners.ksnet.dpt.ks03.obj;

import com.pswseoul.util.GsonUtil;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by parksuwon on 2017-12-13.
 */

public class KSnetRequestObj {
    /*
    public byte[]  TelegramType;                                // 전문 구분 ,  승인(0200) 취소(0420)
    public byte[]  DPTID;  // 단말기번호 , 테스트단말번호 DPT0TEST03 -> KSCIC앱에 기등록된 단말기번호일 경우에만 정상 승인
    public byte[]  PosEntry;   // Pos Entry Mode , 현금영수증 거래 시 키인거래에만 'K'사용
    public byte[]  PayType;    // [신용]할부개월수(default '00') [현금]거래자구분
    public byte[]  TotalAmount;    // 총금액
    public byte[]  ServicAmount;    // 봉사료
    public byte[]  TaxAmount;  // 부가세
    public byte[]  Amount;   // 공급금액 = 총금액 - 부가세 - 봉사료
    public byte[]  FreeAmount;    // 면세 0처리
    public byte[]  AuthNum;    //원거래 승인번호 , 취소시에만 사용
    public byte[]  Authdate;   //원거래 승인일자 , 취소시에만 사용
    public byte[]  Filler;    // 여유필드 - 판매차 처리
    public byte[]  ReceiptNo;
    public byte[]  SignTrans;   // 서명거래 필드, 무서명(N) 50000원 이상 서명(S)
    public byte[]  PlayType;

    public byte[]  CardType;
    public byte[]  BranchNM;
    public byte[]  BIZNO;
*/
    public HashMap<String, String > data = new HashMap<String, String >();

    public void put(String key , String value) {
        data.put(key,value);
    }
    public HashMap getByteMap() {
        HashMap<String, byte[]> m_byte_hash = new HashMap<String, byte[] >();
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            m_byte_hash.put(key , data.get(key).getBytes());
        }
        return m_byte_hash;
    }

    public HashMap getMap() {
       return data;
    }


    public void clearHash(){
        data.clear();
    }

    public String toJsonString(){
        return  GsonUtil.toJson(this,true,"");
    }


}
