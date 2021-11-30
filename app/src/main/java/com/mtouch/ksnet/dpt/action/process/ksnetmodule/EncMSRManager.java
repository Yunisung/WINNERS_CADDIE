package com.mtouch.ksnet.dpt.action.process.ksnetmodule;


import com.mtouch.ksnet.dpt.action.process.util.KsnetUtils;
import com.mtouch.ksnet.dpt.common.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 상완 on 2016-10-27.
 */
public class EncMSRManager {
    //커맨드
    static public final byte STX = 0x02;
    static public final byte ETX = 0x03;
    static public final byte ACK = 0x06;
    static public final byte NAK = 0x15;
    static public final byte EOT = 0x04;
    static public final byte ESC = 0x1B;
    static public final byte FS = 0x1C;
    //요청부
    static public final byte KSNET_DONGLE_INFO_REQ = (byte) 0xC0;
    static public final byte KSNET_READER_SET_REQ = (byte) 0xC1;
    static public final byte KSNET_CARDNO_REQ = (byte) 0xC2;
    static public final byte KSNET_IC_2ND_REQ = (byte) 0xC3;
    static public final byte KSNET_INTEGRITY_REQ = (byte) 0xC4;
    static public final byte KSNET_FALLBACK_REQ = (byte) 0xC5;
    static public final byte KSNET_IC_STATE_REQ = (byte) 0xC6;
    static public final byte KSNET_KEY_SHARED_REQ = (byte) 0xC7;

    //응답부
    static public final byte KSNET_DONGLE_INFO_RESP = (byte) 0xD0;
    static public final byte KSNET_READER_SET_RESP = (byte) 0xD1;
    static public final byte KSNET_CARD_MS_RESP = (byte) 0xD2;
    static public final byte KSNET_CARD_IC_RESP = (byte) 0xD2;
    static public final byte KSNET_IC_2ND_RESP = (byte) 0xD3;
    static public final byte KSNET_INTEGRITY_RESP = (byte) 0xD4;
    static public final byte KSNET_FALLBACK_RESP = (byte) 0xD5;
    static public final byte KSNET_IC_RESP = (byte) 0xD6;
    static public final byte KSNET_KEY_SHARED_RESP = (byte) 0xD7;

    // ERROR CODE
    static public final byte SUCCESS = (byte) 0;
    static public final byte ERROR_VERIFY_FAIL = (byte) 14;  //키 유효성 검증오류
    static public final byte ERROR_PUBLIC_KEY = (byte) 15;  //IPEK Key 없음
    static public final byte ERROR_TIMEOUT = (byte) 40;  //타임아웃
    static public final byte ERROR_NOCARD = (byte) 50;  //카드 미입력(IC 미 삽입)
    static public final byte ERROR_CARD_DECLINED = (byte) 60;  //2nd Generation 에러

    // FALLBACK
    static public final String FALLBACK_NO_ERROR = "00";
    static public final String FALLBACK_NO_ATR = "01";
    static public final String FALLBACK_NO_APPL = "02";
    static public final String FALLBACK_READ_FAIL = "03";
    static public final String FALLBACK_NO_DATA = "04";
    static public final String FALLBACK_CVM_FAIL = "05";
    static public final String FALLBACK_BAD_CM = "06";
    static public final String FALLBACK_BAD_OPER = "07";

    // NO FALLBACK
    static public final String NOFALLBACK_ChipBlock = "30";
    static public final String NOFALLBACK_ApplicationBlock = "31";
    static public final String NOFALLBACK_CardErr = "32";


    public byte[] _recvData = new byte[1024];

    public static int _idxRecvData = 0;

    public byte packet[] = new byte[1024];
    String Year = getTime().substring(0, 2); // 연도 16년도 "16"
    String YYMMDDhhmmss = getTime().substring(0, 12); // YYMMDDhhmmss

    public static int ReceiptCount = 0;
    private final static int IDX_STX = 0;     //STX 영역 인덱스
    private final int IDX_LEN = 1;      //Length 영역 인덱스
    private final static int IDX_COMMAND = 3; //Command 영역 인덱스
    private final int IDX_DATA = 4;     //Data영역 인덱스

    private final int SIZE_LEN = 2; //Lengh 영역 길이
    private final int SIZE_MIN = 4; //Command ~ ETX 영역을 제외한 나머지 길이
    private final static int SIZE_MIN_TOTAL = 6; //Data 영역을 제외한 나머지 길이
    private final int SIZE_CODE = 2; //응답코드 길이

    public EncMSRManager() {

    }


    private String getTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyMMddhhmmss");
        return dayTime.format(new Date(time));
    }

    //LRC 체크
    public static int LRC(byte[] bytes, int length) {
        int checksum = 0;
        //STX 1byte 뒤부터  ETX까지
        for (int i = 1; i < length; i++) {
            checksum ^= (bytes[i] & 0xFF);
        }
        return checksum;
    }

    private String m5508k() {
        return new SimpleDateFormat("yyMMddhhmmss").format(new Date(System.currentTimeMillis()));
    }

    public static int m5507a(byte[] bArr, int i) {
        int i2 = 0;
        for (int i3 = 1; i3 < i; i3++) {
            i2 ^= bArr[i3] & 255;
        }
        return i2;
    }

    public byte[] makeRequestCardInOutStatus(){
        int idx = 0;

        packet[idx++] = STX;                   //STX
        packet[idx++] = 0x00;                      //Length 2바이트
        packet[idx++] = 0x05;
        packet[idx++] = KSNET_IC_STATE_REQ; // 'C6' Command
        packet[idx++] = " ".getBytes()[0];     //공백 3바이트
        packet[idx++] = " ".getBytes()[0];
        packet[idx++] = " ".getBytes()[0];
        packet[idx++] = ETX;

        byte bLRC = (byte) LRC(packet, idx);
        packet[idx++] = bLRC;                     //LRC

        byte txPacket[] = new byte[idx];
        System.arraycopy(packet, 0, txPacket, 0, idx);
        packet = new byte[1024]; //패킷 초기화
        return txPacket;
    }

    //리더기 상태정보 요청 :C0
    public byte[] makeDongleInfo() {
        int idx = 0;

        packet[idx++] = STX;                   //STX
        packet[idx++] = 0x00;                      //Length 2바이트
        packet[idx++] = 0x05;
        packet[idx++] = KSNET_DONGLE_INFO_REQ; // 'C0' Command
        packet[idx++] = (byte) Year.charAt(0);     //연도 2바이트
        packet[idx++] = (byte) Year.charAt(1);
        packet[idx++] = '1';                      //카드데이터형식 1:카드번호 마스킹 2: 논마스킹 3: 16자리 암호화 + 마스킹
        packet[idx++] = ETX;

        byte bLRC = (byte) LRC(packet, idx);
        packet[idx++] = bLRC;                     //LRC

        byte txPacket[] = new byte[idx];
        System.arraycopy(packet, 0, txPacket, 0, idx);
        packet = new byte[1024]; //패킷 초기화
        return txPacket;
    }

    //리더기 상태변경 요청 :C1
    public byte[] changeReaderState() {
        int idx = 0;

        packet[idx++] = STX;                   //STX
        packet[idx++] = 0x00;                      //Length 2바이트
        packet[idx++] = 0x05;
        packet[idx++] = KSNET_DONGLE_INFO_REQ; // 'C0' Command
        packet[idx++] = (byte) Year.charAt(0);     //연도 2바이트
        packet[idx++] = (byte) Year.charAt(1);
        packet[idx++] = '1';                      //카드데이터형식 1:카드번호 마스킹 2: 논마스킹 3: 16자리 암호화 + 마스킹
        packet[idx++] = ETX;

        byte bLRC = (byte) LRC(packet, idx);
        packet[idx++] = bLRC;                     //LRC

        byte txPacket[] = new byte[idx];
        System.arraycopy(packet, 0, txPacket, 0, idx);
        packet = new byte[1024]; //패킷 초기화
        return txPacket;
    }

    //카드번호요청 : C2
    public byte[] makeCardNumSendReq(byte[] totalAmount, byte[] resTime) {
        int idx = 0;
        packet[idx++] = STX;                       //STX
        packet[idx++] = 0x00;                      //Length 2바이트
        packet[idx++] = 0x19;
        packet[idx++] = KSNET_CARDNO_REQ;    // 'C2' Command

        System.arraycopy(YYMMDDhhmmss.getBytes(), 0, packet, idx, 12);
        idx += 12;
        System.arraycopy(totalAmount, 0, packet, idx, 9);
        idx += 9;
        System.arraycopy(resTime, 0, packet, idx, 2);
        idx += 2;

        packet[idx++] = ETX;
        byte bLRC = (byte) LRC(packet, idx);
        packet[idx++] = bLRC;                     //LRC

        byte txPacket[] = new byte[idx];
        System.arraycopy(packet, 0, txPacket, 0, idx);

        packet = new byte[1024]; //패킷 초기화
        return txPacket;
    }

    //리더기 무결성 검증 요청 :C4
    public byte[] makeIntegrityReq(byte[] readerModelNum, byte type) {
        int idx = 0;
        packet[idx++] = STX;                       //STX

        packet[idx++] = 0x00;                      //Length 2바이트
        packet[idx++] = 0x13;
        packet[idx++] = KSNET_INTEGRITY_REQ;    // 'C4' Command

        //리더기 모델 번호
        //ReaderModelNum역역 0번째 바이트 부터 16크기만큼 readerModelNum idx부터 복사
        System.arraycopy(readerModelNum, 0, packet, idx, 16);
        idx += 16;

//        packet[idx++] = (byte)type;     // K: Key or  S : S/w
        packet[idx++] = type;     // K: Key or  S : S/w
        packet[idx++] = ETX;

        byte bLRC = (byte) LRC(packet, idx);
        packet[idx++] = bLRC;                     //LRC

        byte txPacket[] = new byte[idx];
        System.arraycopy(packet, 0, txPacket, 0, idx);
        packet = new byte[1024]; //패킷 초기화
        return txPacket;

    }

    //IC 거래시 2Th Generate 요청전문 : C3
    public byte[] make2ThGenerateReq(byte[] Amount, byte[] responseCode, byte[] tradeCode, byte[] resEMVData) {

        int idx = 0;

        packet[idx++] = STX;                                              //STX
        int Length = 27 + resEMVData.length + 1;                          //Length : CommandID 부터 ETX 까지
        System.arraycopy(KsnetUtils.make2ByteLengh(Length), 0, packet, idx, 2);
        idx += 2;

        packet[idx++] = KSNET_IC_2ND_REQ;                             //'C4' Command
        //거래일시
        System.arraycopy(YYMMDDhhmmss.getBytes(), 0, packet, idx, 12);
        idx += 12;
        //거래 총 금액
        System.arraycopy((Amount), 0, packet, idx, 9);
        idx += 9;
        //승인응답 코드
        System.arraycopy(responseCode, 0, packet, idx, 2);
        idx += 2;
        //IC 거래 코드
        System.arraycopy(tradeCode, 0, packet, idx, 3);
        idx += 3;
        //Additional Response Data , ARPC, Issuer Script
        System.arraycopy(resEMVData, 0, packet, idx, resEMVData.length);
        idx += resEMVData.length;

        packet[idx++] = ETX;

        byte bLRC = (byte) LRC(packet, idx);
        packet[idx++] = bLRC;                     //LRC

        byte txPacket[] = new byte[idx];
        System.arraycopy(packet, 0, txPacket, 0, idx);
        packet = new byte[1024]; //패킷 초기화
        return txPacket;
    }

    //FallBack후 카드번호 요청전문 : C5
    public byte[] makeFallBackCardReq(String FallBcack_ErrCode, String timeOut) {
        int idx = 0 + 1;
        this.packet[0] = 2;
        System.arraycopy(Utils.make2ByteLengh(25), 0, this.packet, idx, 2);
        int idx2 = idx + 2;
        int idx3 = idx2 + 1;
        this.packet[idx2] = KSNET_FALLBACK_REQ;
        System.arraycopy(this.YYMMDDhhmmss.getBytes(), 0, this.packet, idx3, 12);
        int idx4 = idx3 + 12;
        System.arraycopy(String.format("%09d", new Object[]{Integer.valueOf(Integer.parseInt(FallBcack_ErrCode))}).getBytes(), 0, this.packet, idx4, 9);
        int idx5 = idx4 + 9;
        System.arraycopy(timeOut.getBytes(), 0, this.packet, idx5, 2);
        int idx6 = idx5 + 2;
        int idx7 = idx6 + 1;
        this.packet[idx6] = 3;
        int idx8 = idx7 + 1;
        this.packet[idx7] = (byte) LRC(this.packet, idx7);
        byte[] txPacket = new byte[idx8];
        System.arraycopy(this.packet, 0, txPacket, 0, idx8);
        this.packet = new byte[1024];
        return txPacket;
    }

    public byte[] makeRequestTelegram(byte[] TransType,
                                      byte[] TelegramType,
                                      byte[] WorkType,
                                      byte[] TelegramNo,
                                      byte[] PosEntry,
                                      byte[] AuthNum,
                                      byte[] AuthDate,
                                      byte[] DPTID,
                                      byte[] SWModelNum,
                                      byte[] ReaderModelNum,
                                      byte[] PayType,
                                      byte[] TotalAmount,
                                      byte[] Amount,
                                      byte[] ServiceAmount,
                                      byte[] TaxAmount,
                                      byte[] FreeAmount,
                                      byte[] Filler,
                                      byte[] SignTrans,
                                      byte[] SignData,
                                      byte[] EncryptInfo,
                                      byte[] EMVData,
                                      byte[] TrackII) {

        java.nio.ByteBuffer telegramBuffer = java.nio.ByteBuffer.allocateDirect(4096);

        telegramBuffer.put((byte) 0x02);
        telegramBuffer.put(TransType);                                                          //거래구분
        if (!new String(WorkType).contains("09"))                                               //업무구분 09(이통사 할인)시 worktype 09|이통사 형태로 들어옴..전문 reseved필드에 이통사 적용
            telegramBuffer.put(WorkType);
        else {
            telegramBuffer.put("09".getBytes());
        }

        telegramBuffer.put(TelegramType);
        telegramBuffer.put("N".getBytes());                                                  //거래형태 일반 N
        telegramBuffer.put(DPTID);                                                           //단말기번호

        for (int i = 0; i < 4; i++) telegramBuffer.put(" ".getBytes());
        telegramBuffer.put(TelegramNo);
        for (int i = 0; i < 12 - TelegramNo.length; i++)
            telegramBuffer.put(" ".getBytes()); //전문일련 번호(망취소 시 사용)
        telegramBuffer.put(PosEntry);                                                          //POS Entry
        for (int i = 0; i < 20; i++) telegramBuffer.put(" ".getBytes());
        for (int i = 0; i < 20; i++) telegramBuffer.put(" ".getBytes());
        // 암호화 여부

        //Key-In 일 경우만
        if ((new String(TransType).equals("HK") || new String(TransType).equals("PC")) && new String(PosEntry).equals("K"))
            telegramBuffer.put("9".getBytes());
        else
            telegramBuffer.put("1".getBytes());

        telegramBuffer.put(SWModelNum);                                                  // S/W 모델번호
        telegramBuffer.put(ReaderModelNum);                                              // 리더기 모델 정보

        telegramBuffer.put(EncryptInfo);                                                 // 암호화정보

        for (int i = 0; i < 40 - EncryptInfo.length; i++)
            telegramBuffer.put(" ".getBytes());

        if (new String(TransType).equals("IC"))                                         // TRack II
            for (int i = 0; i < 37; i++) telegramBuffer.put(" ".getBytes());
        else if (new String(TransType).equals("MS") || new String(TransType).equals("HK") || new String(TransType).equals("PC")) {
            telegramBuffer.put(TrackII);
            for (int i = 0; i < 37 - TrackII.length; i++) telegramBuffer.put(" ".getBytes());
        }

        telegramBuffer.put((byte) 0x1C);                                               // FS
        telegramBuffer.put(PayType);                                                   // [신용]할부개월 , [현금] , [포인트]
        telegramBuffer.put(TotalAmount);                                               // 총금액
        telegramBuffer.put(ServiceAmount);                                             // 봉사료
        telegramBuffer.put(TaxAmount);                                                 // 세금
        telegramBuffer.put(Amount);                                                    // 공급금액
        telegramBuffer.put(FreeAmount);                                                // 면세금액
        telegramBuffer.put("AA".getBytes());                                          // Working Key Index
        for (int i = 0; i < 16; i++) telegramBuffer.put("0".getBytes());             //비밀번호
        //원거래 승인번호

        if (new String(TelegramType).equals("0420") || new String(TelegramType).equals("0460")) {
            telegramBuffer.put(AuthNum);
            for (int i = 0; i < 12 - AuthNum.length; i++)
                telegramBuffer.put(" ".getBytes());         //원거래승인번호
            telegramBuffer.put(AuthDate);
            for (int i = 0; i < 6 - AuthDate.length; i++)
                telegramBuffer.put(" ".getBytes());          //원거래승인일자

        } else {
            for (int i = 0; i < 12; i++) telegramBuffer.put(" ".getBytes());         //원거래승인번호
            for (int i = 0; i < 6; i++) telegramBuffer.put(" ".getBytes());          //원거래승인일자
        }

        if (!new String(WorkType).contains("09"))               //할인
            for (int i = 0; i < 73; i++)
                telegramBuffer.put(" ".getBytes());             //사용자정보 ~ DCC환율조회
        else {
            for (int i = 0; i < 45; i++)
                telegramBuffer.put(" ".getBytes());             //사용자정보 ~ 가맹점사용필드

            /* Reserved 필드 삽입 */
            String strReserved = new String(WorkType).split("|")[1];
            telegramBuffer.put(strReserved.getBytes());
            for (int i = 0; i < 4 - strReserved.length(); i++)
                telegramBuffer.put(" ".getBytes());
            /* Reserved 필드 삽입 */

            for (int i = 0; i < 24; i++)                      //32 ~ 36 까지
                telegramBuffer.put(" ".getBytes());             //KSNET RESERVED ~ 신용카드 종류
        }
        telegramBuffer.put(Filler);                                                   //Filler
        for (int i = 0; i < 30 - new String(Filler).length(); i++)
            telegramBuffer.put(" ".getBytes());
        for (int i = 0; i < 60; i++) telegramBuffer.put(" ".getBytes());             //DCC환율조회

        //IC 거래일 경우 EMV 데이터 추가
        if (new String(TransType).equals("IC"))
            telegramBuffer.put(EMVData);

        if (new String(SignTrans).equals("N"))
            telegramBuffer.put(SignTrans);                                           //전자서명(무서명)
        else {
            telegramBuffer.put(SignTrans);                                           // 전자서명(서명)
            telegramBuffer.put("83".getBytes());                                    //Working Key Index
            for (int i = 0; i < 16; i++) telegramBuffer.put(" ".getBytes());       //제품 코드 및 버전
            telegramBuffer.put(String.format("%04d", new String(SignData).length()).getBytes());
            telegramBuffer.put(SignData);  // base64
        }
        telegramBuffer.put((byte) 0x03);                                            //ETX
        telegramBuffer.put((byte) 0x0D);                                            //CR

        byte[] telegram = new byte[telegramBuffer.position()];
        telegramBuffer.rewind();
        telegramBuffer.get(telegram);

        byte[] requestTelegram = new byte[telegram.length + 4];
        String telegramLength = String.format("%04d", telegram.length);

        System.arraycopy(telegramLength.getBytes(), 0, requestTelegram, 0, 4);
        System.arraycopy(telegram, 0, requestTelegram, 4, telegram.length);

        byte[] _overwrightData1 = new byte[telegram.length];
        byte[] _overwrightData2 = new byte[telegram.length];

        for (int i = 0; i < telegram.length; i++) {
            _overwrightData1[i] = (byte) 0x00;
        }
        for (int i = 0; i < telegram.length; i++) {
            _overwrightData2[i] = (byte) 0xFF;
        }

        telegramBuffer.clear();
        telegramBuffer.clear();
        telegramBuffer.clear();

        telegramBuffer = null;

        System.arraycopy(_overwrightData1, 0, telegram, 0, telegram.length);
        System.arraycopy(_overwrightData2, 0, telegram, 0, telegram.length);
        System.arraycopy(_overwrightData1, 0, telegram, 0, telegram.length);

        return requestTelegram;
    }

    public String chkFallBack(String errCode) {
        String errName = "";
        switch (errCode) {
            //Fallback 에러
            case FALLBACK_NO_ATR:
                errName = "01";
                //errName = "Chip 미응답";
                break;
            case FALLBACK_NO_APPL:
                errName = "02";
                //errName = "Application 미존재";
                break;
            case FALLBACK_READ_FAIL:
                errName = "03";
                //errName = "Chip 데이터읽기 실패";
                break;
            case FALLBACK_NO_DATA:
                errName = "04";
                //errName = "Mandatory 데이터 미 포함";
                break;
            case FALLBACK_CVM_FAIL:
                errName = "05";
                //errName = "CVM 커맨드 응답 실패";
                break;
            case FALLBACK_BAD_CM:
                errName = "06";
                //errName = "EMV 커맨드 오 설정";
                break;
            case FALLBACK_BAD_OPER:
                errName = "07";
                //errName = "리더기 오 동작";
                break;

            //비정상 Fallback
            //Fallback 거래 하지않음
            case NOFALLBACK_ApplicationBlock:
                errName = "ApplicationBlock";
                break;
            case NOFALLBACK_ChipBlock:
                errName = "ChipBlock";
                break;
            case NOFALLBACK_CardErr:
                errName = "CardErr";
                break;

        }
        return errName;
    }


    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        int length = a.length;
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x ", new Object[]{Byte.valueOf(a[i])}));
        }
        return sb.toString();
    }

    public static String byteArrayToHex(byte[] a, int length) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x ", new Object[]{Byte.valueOf(a[i])}));
        }
        return sb.toString();
    }

    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }


    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public static String byteToString(byte n) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%02x", new Object[]{Byte.valueOf(n)}));
        return sb.toString();
    }

    public static byte GetCommandID(byte[] t_data) {
        byte cmd;
        if (t_data == null || t_data.length < SIZE_MIN_TOTAL) return (byte) 0x01;
        if (t_data[IDX_STX] != (byte) 0x02) return (byte) 0x02;
        if (t_data[_idxRecvData - 2] != (byte) 0x03) return (byte) 0x03;

        cmd = t_data[IDX_COMMAND];
        return cmd;
    }

    private String numbertoString(int number) {
        return String.format("%,d", number);
    }


}
