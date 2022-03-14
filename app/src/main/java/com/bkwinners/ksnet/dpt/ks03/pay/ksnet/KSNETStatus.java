package com.bkwinners.ksnet.dpt.ks03.pay.ksnet;

import android.app.Activity;
import android.util.Log;

import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.obj.KsnetResponseObj;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
import com.pswseoul.util.AndroidUtils;

import java.util.HashMap;

/**
 * Created by parksuwon on 2017-12-15.
 *
 "identity": "1102233333",
 "name": "더페이원 단말기",
 "ceoName": "권유현",
 "addr": "서울 강남구 테헤란로38길 5 (역삼동, 스타팅빌딩) 역삼동, 스타팅빌딩 13층",
 "key": "mtm71fd-177a47-5b7-6d83b",
 "telNo": "0234463730"
 "version": "1.0.0"
 "appId": ""
 */

public class KSNETStatus {

    //////////////////////// 업체 정보 /////////////////////////////

    public static String  idType= "";      // 사업자 번호
    public static String  name= "";        // 상호
    public static String  mchtId= "";     // 아이디
    public static String  tmnId  = "";     // 터미널 아이디

    public static String identity  = "";     // 사업자 번호

    public static String  ceoName= "";
    public static String  addr= "";

    public static String  key= "";
    public static String  appId = "" ; // appId

    public static String  token = "" ; // token

    public static String  telNo = "" ; // telNo
    public static String  DPTID = "";  // KS단말기
    /////////////////////

    ////////////////////////// 결재 정보 //////////////////////////////////
    public static String van = "";  // van
    public static String vanid = "";  // vanid
    public static String trackId = "";  // trackId
    public static String secondKey = "";  // secondKey

    ////////////////////////////////////////////////////////////////
    public static boolean BT_PRN_FLAG = false;
    public static String BT_PRN_ADDRESS = "";

    public static boolean BT_BLUETHOOTH_FLAG = false;

    public static String  versionName = BuildConfig.VERSION_NAME; // versionName
    public static int  versionCode = 0 ; // versionCode


    public  static KSNETStatus instance = null;
    Activity activitry ;
    public KSNETStatus(Activity activitry) {
        this.activitry = activitry;
        getSaveData();
    }

    public static KSNETStatus getInstance(Activity activitry) {
        if(instance == null)
            instance = new KSNETStatus(activitry);
        return instance;
    }

    public void getSaveData() {
        KSNETStatus.identity = SharedPreferenceUtil.getData(activitry,"identity");
        KSNETStatus.name = SharedPreferenceUtil.getData(activitry,"name");
        KSNETStatus.ceoName = SharedPreferenceUtil.getData(activitry,"ceoName");
        KSNETStatus.addr = SharedPreferenceUtil.getData(activitry,"addr");
        KSNETStatus.key = SharedPreferenceUtil.getData(activitry,"payKey");
        KSNETStatus.telNo = SharedPreferenceUtil.getData(activitry,"telNo");
        KSNETStatus.DPTID = SharedPreferenceUtil.getData(activitry,"DPTID");
        KSNETStatus.token = SharedPreferenceUtil.getData(activitry,"key");

    }

    public void setSaveData(String str, String value) {
        AndroidUtils.setAppPreferences(activitry, str , value);
    }

    public void KSApproveSystem(HashMap<String, String> m_hash , KsnetResponseObj ksnetresp){
        //KTC 인증용 출력
        if(Constants.IS_TEST) {
            Log.d("debug", "recv [Classification]:: " + new String(m_hash.get("Classification")));
            Log.d("debug", "recv [TelegramType]:: " + new String(m_hash.get("TelegramType")));
            Log.d("debug", "recv [Dpt_Id]:: " + new String(m_hash.get("Dpt_Id")));
            Log.d("debug", "recv [Enterprise_Info]:: " + new String(m_hash.get("Enterprise_Info")));

            Log.d("debug", "recv [Full_Text_Num]:: " + new String(m_hash.get("Full_Text_Num")));
            Log.d("debug", "recv [Status]:: " + new String(m_hash.get("Status")));
            Log.d("debug", "recv [Authdate]:: " + new String(m_hash.get("Authdate")));
            Log.d("debug", "recv [Message1]:: " + new String(m_hash.get("Message1")));
            Log.d("debug", "recv [Message2]:: " + new String(m_hash.get("Message2")));

            Log.d("debug", "recv [AuthNum]:: " + new String(m_hash.get("AuthNum")));
            Log.d("debug", "recv [FranchiseID]:: " + new String(m_hash.get("FranchiseID")));
            Log.d("debug", "recv [IssueCode]:: " + new String(m_hash.get("IssueCode")));
            Log.d("debug", "recv [CardName]:: " + new String(m_hash.get("CardName")));
            Log.d("debug", "recv [PurchaseCode]:: " + new String(m_hash.get("PurchaseCode")));
            Log.d("debug", "recv [PurchaseName]:: " + new String(m_hash.get("PurchaseName")));
            Log.d("debug", "recv [Remain]:: " + new String(m_hash.get("Remain")));
            Log.d("debug", "recv [point1]:: " + new String(m_hash.get("point1")));
            Log.d("debug", "recv [point2]:: " + new String(m_hash.get("point2")));
            Log.d("debug", "recv [point3]:: " + new String(m_hash.get("point3")));
            Log.d("debug", "recv [notice1]:: " + new String(m_hash.get("notice1")));
            Log.d("debug", "recv [notice2]:: " + new String(m_hash.get("notice2")));
            Log.d("debug", "recv [CardNo]:: " + new String(m_hash.get("CardNo")));

            Log.d("debug", "ksnetresp.toString():: " + ksnetresp.PrnData());
        }
        /*
                    Intent i = new Intent(BTPrint.this,  com.example.parksuwon.myapplication.pay.ksnet.ResultActivity.class);
                    i.setFlags(i.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra("result",m_hash);
                    startActivity(i);
*/
    }
}
