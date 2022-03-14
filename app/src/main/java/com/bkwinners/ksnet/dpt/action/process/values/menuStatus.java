package com.bkwinners.ksnet.dpt.action.process.values;

/**
 * Created by parksuwon on 2018-01-29.
 */

public class menuStatus {
    /*  단말기에 대한 메뉴얼 값 REQ */
    public static final int DPT_CO  = 1192 ;  //상태정보 요청
    public static final int DPT_C1  = DPT_CO + 1 ;  // 리더기 상태 세팅
    public static final int DPT_C2  = DPT_CO + 2 ;  // 카드번호 전송 요청
    public static final int DPT_C3  = DPT_CO + 3 ;  // IC 카드 2th Generate 요청
    public static final int DPT_C4  = DPT_CO + 4 ;  // 무결성점검 요청
    public static final int DPT_C5  = DPT_CO + 5 ;  // Fall Back 카드번호 요청
    public static final int DPT_C6  = DPT_CO + 6 ;  // IC 카드 삽입/제거 상태 요청
    public static final int DPT_C7  = DPT_CO + 7 ;  // 미사용
    public static final int DPT_C8  = DPT_CO + 8 ;  // 현금 IC 카드 데이터 요청
    public static final int DPT_C9  = DPT_CO + 9 ;  //키인 카드번호 암호화 요청
    public static final int DPT_E0  = DPT_CO + 10 ;  // 카드종류 요청
    public static final int DPT_E1  = DPT_CO + 11 ;  // 카드종류 요청

    /*  단말기에 대한 메뉴얼 값 RESP */
    public static final int DPT_DO  = 1208 ;  //상태정보 요청
    public static final int DPT_D1  = DPT_DO + 1 ;  // 리더기 상태 세팅
    public static final int DPT_D2  = DPT_DO + 2 ;  // 카드번호 전송 요청
    public static final int DPT_D3  = DPT_DO + 3 ;  // IC 카드 2th Generate 요청
    public static final int DPT_D4  = DPT_DO + 4 ;  // 무결성점검 요청
    public static final int DPT_D5  = DPT_DO + 5 ;  // Fall Back 카드번호 요청
    public static final int DPT_D6  = DPT_DO + 6 ;  // IC 카드 삽입/제거 상태 요청
    public static final int DPT_D7  = DPT_DO + 7 ;  // 미사용
    public static final int DPT_D8  = DPT_DO + 8 ;  // 현금 IC 카드 데이터 요청
    public static final int DPT_D9  = DPT_DO + 9 ;  //키인 카드번호 암호화 요청
    public static final int DPT_F0  = DPT_DO + 10 ;  // 카드종류 요청
    public static final int DPT_F1  = DPT_DO + 11 ;  // 카드종류 요청

   /* MESSAGE 구분 메세지는 단말에게 전달 된 값과 보낼 값을 한곳에 모읍니다  */

    public static final int MESSAGE_DPT_CONNECTED  = 10000 ;  // 단말기 연결 여부
    public static final int MESSAGE_BAT  = MESSAGE_DPT_CONNECTED + 1 ;  // 밧데리 요청 입니다
    public static final int MESSAGE_DPT_C0  = MESSAGE_DPT_CONNECTED + 2 ;  // 단말기 상태 정보 요청
    public static final int MESSAGE_DPT_D0  = MESSAGE_DPT_CONNECTED + 3 ;  // 단말기 상태 정보 응답
    public static final int MESSAGE_DPT_C2  = MESSAGE_DPT_CONNECTED + 4 ;  // 카드 번호 전송 요청
    public static final int MESSAGE_DPT_D2  = MESSAGE_DPT_CONNECTED + 5;  // 카드 번호 전송 응답
    public static final int MESSAGE_DPT_C3  = MESSAGE_DPT_CONNECTED + 6;  // IC 카드 2th Generate 요청
    public static final int MESSAGE_DPT_D3  = MESSAGE_DPT_CONNECTED + 7;  // IC 카드 2th Generate 응답
    public static final int MESSAGE_DPT_C4  = MESSAGE_DPT_CONNECTED + 8;  // IC 카드 2th Generate 요청
    public static final int MESSAGE_DPT_D4  = MESSAGE_DPT_CONNECTED + 9;  // IC 카드 2th Generate 응답
    public static final int MESSAGE_DPT_C5  = MESSAGE_DPT_CONNECTED + 10;  // Fall Back 카드번호 요청
    public static final int MESSAGE_DPT_D5  = MESSAGE_DPT_CONNECTED + 11;  // Fall Back 카드번호 응답
    public static final int MESSAGE_DPT_C6  = MESSAGE_DPT_CONNECTED + 12;  // IC 카드 삽입/제거 상태 요청
    public static final int MESSAGE_DPT_D6  = MESSAGE_DPT_CONNECTED + 13;  // IC 카드 삽입/제거 상태 요청

    public static final int MESSAGE_ICCARD_APPROVE_COMPLETE  = MESSAGE_DPT_CONNECTED + 14;  // 승인 완료된경우
    public static final int MESSAGE_ICCARD_CANCEL_APPROVE_COMPLETE  = MESSAGE_DPT_CONNECTED + 15;  // 승인 취소

    public static final int MESSAGE_RETURN_OK_ACTIVITY  = MESSAGE_DPT_CONNECTED + 16;  // 화면 전환
    public static final int MESSAGE_RETURN_NOK_ACTIVITY  = MESSAGE_DPT_CONNECTED + 17;  // 화면 전환

     /*  BLUE TOOTH STATUS  */
    public static final int MESSAGE_ACTION_GATT_CONNECTED  = 3000 ;  // 단말기기 연결
    public static final int MESSAGE_ACTION_GATT_DISCONNECTED  = MESSAGE_ACTION_GATT_CONNECTED + 1 ;  // 단말기기 연결
    public static final int MESSAGE_ACTION_GATT_SERVICES_DISCOVERED  = MESSAGE_ACTION_GATT_CONNECTED + 2 ;  // 단말기기 연결
    public static final int MESSAGE_ACTION_DATA_AVAILABLE  = MESSAGE_ACTION_GATT_CONNECTED + 3 ;  // 단말기기 연결

    public static final int MESSAGE_READ  = MESSAGE_ACTION_GATT_CONNECTED + 4 ;  // 데이터 읽기
    public static final int MESSAGE_WRITE  = MESSAGE_ACTION_GATT_CONNECTED + 5 ;  // 데이터 쓰기

    public static final int MESSAGE_KSNET_READER_SEARCH  = MESSAGE_ACTION_GATT_CONNECTED + 6 ;  // KSNET 리더기 찾기




    public static final int MESSAGE_COMPLETE  = 9999 ;  //나가기

    /* 리더기 연결 */
    public static final int ACTIVITY_KSNET_READER_CONNECT  = 20000 ;  // 단말기기 연결
    public static final int ACTIVITY_KSNET_READER_CONNECTED  = ACTIVITY_KSNET_READER_CONNECT+1 ;  // 단말기기 연결

    public static final int ACTIVITY_KSNET_READER_SEARCH  = ACTIVITY_KSNET_READER_CONNECT+2 ;  // 단말기기 찾기
   //서버 샤인
    public static final int  ACTIVITY_MENU_GET_SIGN = 999;
}
