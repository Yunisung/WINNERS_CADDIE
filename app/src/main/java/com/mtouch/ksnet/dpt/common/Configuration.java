package com.mtouch.ksnet.dpt.common;

import android.app.Activity;

import com.mtouch.ksnet.dpt.ksnetlib.KSNetMain;

import java.util.GregorianCalendar;
import java.util.Hashtable;

public class Configuration {
    private String mMessage;
    private final String mNewVersionFile = "version.txt";
    private Activity mParent;
    private TextPref mPref;
    private final String mPrefFile = "config.pref";
    private final String mServAddr = "http://ad.ksnet.co.kr/kscm/update";
    private boolean mValid;
    /* access modifiers changed from: private */
    public KSNetMain m_KSNetMain;

    public enum Types {
        TransType,
        MSRType,
        Tax_Use,
        Tax_Rate,
        Service_Use,
        Service_Rate,
        Password,
        EmailPassword,
        Comm_IP,
        Comm_Port
    }

    public Configuration(Activity activity) {
        this.mParent = activity;
        this.mMessage = "";
        this.mValid = true;
        try {
            this.mPref = new TextPref(this.mParent, "config.pref");
            this.mPref.ready();
        } catch (Exception e) {
            this.mMessage = e.getMessage();
            this.mValid = false;
        }
    }

    public Configuration() {
    }

    public String getErrMsg() {
        return this.mMessage;
    }

    public boolean downLoad(String str, String str2, String str3, Boolean bool) {
        String str4 = str2;
        if (!this.mValid) {
            return false;
        }
        try {
            new Hashtable();
            String[] transaction = Utils.getTransaction(new Configuration(this.mParent));
            this.m_KSNetMain = new KSNetMain();
            String[] strArr = {""};
            requestRegistrationThread requestregistrationthread = new requestRegistrationThread(transaction[2], str2, str, "KSSMP" + str3, bool);
            requestregistrationthread.start();
            requestregistrationthread.join();
            Hashtable<String, String> m_hash = requestregistrationthread.getM_hash();
            if (m_hash == null) {
                strArr[0] = "승인실패\n";
                strArr[0] = strArr[0] + this.m_KSNetMain.getErrorMessage();
                this.mMessage = strArr[0];
                return false;
            }
            if (m_hash.get("Classification").equals("DV")) {
                m_hash.put("bizRegNum", str4);
                m_hash.put("posNum", "      ");
                m_hash.put("Message3", "                ");
                m_hash.put("Message4", "전표내용확인바람");
                m_hash.put("bizInfo", "        ");
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(m_hash.get("Status"));
            stringBuffer.append(m_hash.get("FranchiseID"));
            stringBuffer.append(m_hash.get("bizRegNum"));
            stringBuffer.append(m_hash.get("Representative"));
            stringBuffer.append(m_hash.get("FranchiseAddress"));
            stringBuffer.append(m_hash.get("FranchiseTel"));
            stringBuffer.append(m_hash.get("Message2"));
            stringBuffer.append(m_hash.get("bizInfo"));
            stringBuffer.append(m_hash.get("Message1"));
            stringBuffer.append(m_hash.get("posNum"));
            stringBuffer.append(m_hash.get("Message3"));
            stringBuffer.append(m_hash.get("Classification"));
            stringBuffer.append(m_hash.get("Message4"));
            strArr[0] = stringBuffer.toString();
            System.out.println(m_hash.get("Status") + "/" + m_hash.get("FranchiseID") + "/" + m_hash.get("bizRegNum") + "/" + m_hash.get("Representative") + "/" + m_hash.get("FranchiseTel") + "/" + m_hash.get("FranchiseAddress"));
            if (m_hash.get("Status").equals("X")) {
                this.mMessage = m_hash.get("Message1") + m_hash.get("Message2") + m_hash.get("Message3") + m_hash.get("Message4");
                return false;
            }
            strArr[0] = AESUtils.des(strArr[0], "!@1234#$", 1);
            DbHelper dbHelper = new DbHelper(this.mParent);
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            String format = String.format("%02d-%02d-%02d", new Object[]{Integer.valueOf(gregorianCalendar.get(1)), Integer.valueOf(gregorianCalendar.get(2) + 1), Integer.valueOf(gregorianCalendar.get(5))});
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM KSN_APP_ENV WHERE EV_SUBID = '");
            String str5 = str2;
            sb.append(str5);
            sb.append("'");
            dbHelper.excuteNonQuery(sb.toString());
            dbHelper.excuteNonQuery("INSERT INTO KSN_APP_ENV(EV_ID, EV_SUBID, EV_VALUE1, EV_VALUE2, EV_VALUE3, EV_ENTDATE, EV_UPDDATE)VALUES('download', '" + str5 + "', '" + strArr[0] + "','" + str + "' , '', '" + format + "', '')");
            return true;
        } catch (Exception e) {
            this.mMessage = e.getMessage();
            return false;
        }
    }

    class requestRegistrationThread extends Thread {
        Boolean IsPhoneNumUse;
        String bizNo;
        String dptid;
        Hashtable<String, String> m_hash;
        String phoneNum;
        String vs;

        requestRegistrationThread(String str, String str2, String str3, String str4, Boolean bool) {
            this.vs = str;
            this.bizNo = str2;
            this.dptid = str3;
            this.phoneNum = str4;
            this.IsPhoneNumUse = bool;
        }

        public void run() {
            if (this.IsPhoneNumUse.booleanValue()) {
                this.m_hash = Configuration.this.m_KSNetMain.requestRegistration(this.vs, 9531, this.bizNo, "MOBILEMS", "1", "", this.dptid, "031", this.phoneNum, "");
                System.out.println("########IsPhoneNumUse");
                return;
            }
            this.m_hash = Configuration.this.m_KSNetMain.requestAuthentication(this.vs, 9531, this.dptid, "MOBILEMS", "", this.bizNo, "");
            System.out.println("########IsPhoneNum NOUse");
        }

        public Hashtable<String, String> getM_hash() {
            return this.m_hash;
        }
    }

    public boolean getDownLoadInfo(StateSetting stateSetting, String str) {
        if (!this.mValid) {
            return false;
        }
        try {
            Utility utility = new Utility();
            DbHelper dbHelper = new DbHelper(this.mParent);
            String[][] excuteQuery = dbHelper.excuteQuery("SELECT EV_VALUE1 FROM KSN_APP_ENV WHERE EV_ID = 'download' and EV_SUBID = '" + str + "'");
            if (excuteQuery == null) {
                return false;
            }
            byte[] stringToByte = utility.stringToByte(AESUtils.des(excuteQuery[0][0], "!@1234#$", 2));
            stateSetting.setStatus(utility.byteToString(stringToByte, 0, 1));
            stateSetting.setBranchNm(utility.byteToString(stringToByte, 1, 50));
            stateSetting.setBizNo(utility.byteToString(stringToByte, 51, 10));
            stateSetting.setBoss(utility.byteToString(stringToByte, 61, 10));
            stateSetting.setBranchAddr(utility.byteToString(stringToByte, 71, 50));
            stateSetting.setBranchPhone(utility.byteToString(stringToByte, 121, 15));
            stateSetting.setDptID(utility.byteToString(stringToByte, 136, 16));
            stateSetting.setBizInfo(utility.byteToString(stringToByte, 152, 8));
            stateSetting.setUserPhone(Utils.getPhoneNumber(this.mParent));
            if (Utils.getPhoneNumber(this.mParent).trim().equals("")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            this.mMessage = e.getMessage();
            return false;
        }
    }

    public String[][] getDownLoadBranch() {
        String[][] strArr = null;
        try {
            new Utility();
            return new DbHelper(this.mParent).excuteQuery("SELECT EV_VALUE1 FROM KSN_APP_ENV WHERE EV_ID = 'download'");
        } catch (Exception e) {
            this.mMessage = e.getMessage();
            return strArr;
        }
    }

    public String[][] getStaffInfo() {
        String[][] strArr = null;
        try {
            new Utility();
            return new DbHelper(this.mParent).excuteQuery("SELECT CS_CUSTID,CS_CUSTNAME FROM KSN_APP_CUSTOMER");
        } catch (Exception e) {
            this.mMessage = e.getMessage();
            return strArr;
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:3|4|5|6|7|(3:9|(3:11|(1:13)|14)|15)|16) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0020 */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0035 A[Catch:{ Exception -> 0x0061 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */

    public String[] getConfig(String str, Types[] typesArr, String[] strArr) {
        if (!this.mValid || typesArr.length != strArr.length) {
            return null;
        }
        String[] strArr2 = new String[typesArr.length];
        int i = 0;
        for (Types types : typesArr) {
            strArr2[i] = this.mPref.readString(str + "|" + types.name(), strArr[i]);
            if (types == Types.Password || types == Types.EmailPassword) {
                try {
                    strArr2[i] = AESUtils.des(strArr2[i], "!@1234#$", 2);
                } catch (Exception unused) {
                }
            }
            i++;
        }
        return strArr2;
    }

    public boolean setConfig(String str, Types[] typesArr, String[] strArr) {
        if (!this.mValid || typesArr.length != strArr.length) {
            return false;
        }
        this.mMessage = "";
        if (str.equals("전체")) {
            str = "";
        }
        int i = 0;
        for (Types types : typesArr) {
            if (types == Types.Password || types == Types.EmailPassword) {
                try {
                    strArr[i] = AESUtils.des(strArr[i], "!@1234#$", 1);
                } catch (Exception e) {
                    this.mMessage = e.getMessage();
                    return false;
                }
            }
            this.mPref.writeString(str + "|" + types.name(), strArr[i]);
            i++;
        }
        try {
            this.mPref.commitWrite();
            this.mPref.ready();
            return true;
        } catch (Exception e2) {
            this.mMessage = e2.getMessage();
            return false;
        }
    }
}