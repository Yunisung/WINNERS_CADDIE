package com.bkwinners.ksnet.dpt.action.process.util;

import android.content.res.Configuration;

import com.pswseoul.comunity.library.Logger;
import com.pswseoul.util.DwStringTokenizer;


import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by parksuwon on 2018-01-22.
 */

public class KsnetUtils {
    static byte[] buf ;
    static ByteBuffer bytebuf = ByteBuffer.allocate(1);

    static String[] reqData = {"4|길이","1|STX","2|거래구분","2|업무구분","4|전문구분","1|거래형태","10|단말기번호","4|업체정보","12|전문일련번호","1|POSEntryMode",
                              "20|거래고유번호","20|암호화하지않은카드번호","1|암호화여부","16|SW모델번호","16|CAT","40|암호화정보","37|TRACEII","1|FS","2|구분","12|총금액",
                              "12|봉사료","12|세금","12|공급금액","12|면세금액","2|working","16|비밀번호","12|원거래승인번호","6|원거래승인일자","13|사용자정보","2|가맹점ID",
                              "30|가맹점사용필드","4|Reserv","20|KSNET Re","1|동글구분","1|매체구분","1|이통사구분","1|신용카드종류","30|filter","60|DCC환율" };
    static String[] respData = {"4|길이|Length","1|STX|STX","2|거래구분|Classification","2|업무구분|Approve","4|전문구분|TelegramType","1|거래형태|N","10|단말기번호|Dpt_Id","4|업체정보|Enterprise_Info",
                                "12|전문일련번호|Full_Text_Num","1|Status|Status","4|KSNET응답코드|KsnetCode","4|카드사응답코드|CardCord","12|거래일시|Authdate","1|카드TYPE|CardType",
                                "16|카드종류/거절이유|Message1","16|OK/거절이유|Message2","12|승인번호|AuthNum","20|거래고유번호|OriNumber","15|가맹점번호|FranchiseID","2|발급자코드|IssueCode",
                               "16|카드종류명|CardName","2|매입사코드|PurchaseCode","16|메입사명|PurchaseName","2|Working|Working","16|Working Key|Working Key" ,"9|잔액|Remain",
                               "9|포인트|point1","9|포인트2|point2","9|포인트3|point3","20|Notice1|notice1","40|Notice2|notice2","5|KSNETR","30|fillter|fillter" };

    public  static HashMap<String,  String> reqmap = new HashMap<String, String>(); //데이터 처리용 임시변수 , 암호화 정보저장
    public  static HashMap<String , String> respmap = new HashMap<String, String>(); //데이터 처리용 임시변수 , 암호화 정보저장

    public static void reqDataPrint (byte[] buf) {
        int start = 0 ;
        int size = 0;
        for(int i = 0; i < reqData.length  ; ++i) {
            DwStringTokenizer dst = new DwStringTokenizer(reqData[i] , "|");
            byte[] _buf = new byte[Integer.parseInt(dst.nextToken())];
            size = _buf.length;
            System.arraycopy(buf , start, _buf,   0 ,  size  );
            start += size;
            try {
                    System.out.println("reqDataPrint : " + (i+1) + "="+ dst.nextToken() + "/" + new String(_buf, "MS949"));
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void respDataPrint (byte[] buf) {
        int start = 0 ;
        int size = 0;
        for(int i = 0; i < respData.length  ; ++i) {
            DwStringTokenizer dst = new DwStringTokenizer(respData[i] , "|");
            byte[] _buf = new byte[Integer.parseInt(dst.nextToken())];
            size = _buf.length;
            System.arraycopy(buf , start, _buf,   0 ,  size  );
            start += size;
            try {
                    System.out.println("respDataPrint : " + (i+1) + "="+  dst.nextToken() + "/" + new String(_buf, "MS949"));
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<String , String> respGetHashData (byte[] buf) {
        HashMap<String , String > map = new HashMap<>();
        int start = 0 ;
        int size = 0;
        map.clear();
        for(int i = 0; i < respData.length  ; ++i) {
            DwStringTokenizer dst = new DwStringTokenizer(respData[i] , "|");
            String _size = dst.nextToken();
            String _notice1 = dst.nextToken();
            String _notice2 = dst.nextToken();
            byte[] _buf = new byte[Integer.parseInt(_size)];
            size = _buf.length;
            System.arraycopy(buf , start, _buf,   0 ,  size  );
            start += size;
            try {
                    System.out.println("respDataPrint : " + (i+1) + "="+  _notice1 + "/" + new String(_buf, "MS949"));
                    map.put(_notice2 ,  new String(_buf, "MS949"));
            } catch ( Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static byte[] make2ByteLengh(int i) {
        byte[] buf = new byte[4];
        buf[0] = (byte)(i >>> 24);
        buf[1] = (byte)(i >>> 16 & 0xff);
        buf[2] = (byte)(i >>> 8 & 0xff);
        buf[3] = (byte)i;
        return buf;
    }

    public static byte[]  byteToSubByte(byte[] buf , int start, int end) {
        Logger.d("printHex" ,  "byteToSubByte : " + start +"/" +end);
        byte[] _buf = new byte[end];
        if(start+end > buf.length){
            return _buf;
        }
        System.arraycopy(buf,start , _buf , 0 , end);
        return _buf ;
     //   return  Arrays.copyOfRange(buf, start, end);
    }

    public static String byteToString (byte[] buf , int start , int size) {
        try {
/*
            byte[] _buf = byteToSubByte(buf, start, start + size);
            String str = new String(_buf, "MS949"); str += "/";
                   str += new String(_buf, "UTF-8");  str += "/";
                   str += new String(_buf, "ksc5601") ; str += "/";
            return str;
*/
            return new String(buf, start, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String byteToMString (byte[] buf , int start , int size) {
        try {

            byte[] _buf = byteToSubByte(buf, start, start + size);
            String str = new String(_buf, "MS949");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    public static String [] getTransaction(Configuration avt) {
         return new String [] { "0.0.0.0.1" , "0000"} ;
    }

    public static byte[] int2byte(int i)
    {
        byte[] buf = new byte[4];
        buf[0] = (byte)(i >>> 24);
        buf[1] = (byte)(i >>> 16 & 0xff);
        buf[2] = (byte)(i >>> 8 & 0xff);
        buf[3] = (byte)i;
        return buf;
    }

    public static int byte2Int(byte[] src)
    {
        int s1 = src[0] & 0xFF;
        int s2 = src[1] & 0xFF;
        int s3 = src[2] & 0xFF;
        int s4 = src[3] & 0xFF;

        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
    }

    public static int byte2Int(byte  src)
    {
        return (src & 0xFF);
       // return src.intValue();
    }



    public static byte[] addArray(byte[] data, byte[] s) {
        if(data == null) return s;

        byte[] tmp	=	new byte[data.length + s.length];

        System.arraycopy(data, 0,  tmp, 0, data.length);
        System.arraycopy(s, 0,  tmp, data.length, s.length);

        return tmp;
    }
    public static byte[] addArray(byte[] data, byte s) {
        if(data == null) return data;

        byte[] tmp	=	new byte[data.length + 1];

        System.arraycopy(data, 0,  tmp, 0, data.length);
        tmp[data.length] = s;

        return tmp;
    }

    public static String toString(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        String str = "{";

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                String type = field.getType().toString().
                        substring(field.getType().toString().lastIndexOf(".") + 1);
                str += type + " " + field.getName() + ":" + field.get(object) + " " ;
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        str += "}";
        return str;
    }

    public static String generateString(int length) {
        Random rnd = new Random();

        StringBuffer buf = new StringBuffer();


        for (int i = 0; i < length; i++) {
            if (rnd.nextBoolean()) {
                buf.append((char) ((int) (rnd.nextInt(26)) + 97));
            } else {
                buf.append((rnd.nextInt(10)));
            }
        }
        return buf.toString();
    }

    public static String getApproveRequestReturnCode(int rtn) {
        String msg = "" + rtn;
        switch (rtn) {
            case -1 :   msg = "인자 오류"; break;
            case -2 :   msg = "암호화실패"; break;
            case -3 :   msg = "연결 실페"; break;
            case -4 :   msg = "송신 실패"; break;
            case -5 :   msg = "수신 실패"; break;
            case -6 :   msg = "복호화 실패"; break;
            case -7 :   msg = "ACK송신 실패"; break;
            case -9 :   msg = "망취소 상황이 발생하였으나 망취소 대상 전문이 아님. 정상으로 판단"; break;
            case -100 :   msg = "망취소 정상. 서버에서 취소 되어 있음"; break;
            case -102 :   msg = "망취소 시도 중 암호화 실패. 서버에서 승인 되어 있음"; break;
            case -103 :   msg = "망취소 시도 중 연결 실패. 서버에는 승인 되어 있음"; break;
            case -104 :   msg = "망취소 시도 중 송신 실패. 서버에는 승인 되어 있음"; break;
            case -105 :   msg = "망취소 시도 중 수신 실패. 서버에는 취소 되어 있음"; break;
            case -106 :   msg = "망취소 시도 중 복호화 실패. 서버에는 취소 되어 있음"; break;
        }
        return msg;
    }


    public static void main(String[] argv ) {
        System.out.println(generateString(12).length());
        System.out.println(generateString(12).getBytes().length);

    }
}
