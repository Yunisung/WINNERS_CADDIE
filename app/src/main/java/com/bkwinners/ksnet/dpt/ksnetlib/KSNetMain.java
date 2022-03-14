package com.bkwinners.ksnet.dpt.ksnetlib;

import com.bkwinners.ksnet.dpt.common.Utility;

import java.util.Hashtable;

public class KSNetMain {
    DataEncryptionStandard m_DES = new DataEncryptionStandard();
    Utility m_Utility = new Utility();
    VanMessage m_VanMessage = new VanMessage();

    public byte[] requestApproval(String str, int i, byte[] bArr) {
        return this.m_VanMessage.requestApproval(str, i, bArr);
    }

    public Hashtable<String, String> requestApproval(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22, String str23, String str24, String str25, String str26, String str27, String str28, String str29, String str30, String str31, String str32) {
        String str33 = str;
        return this.m_VanMessage.requestApproval(str, i, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, str17, str18, str19, str20, str21, str22, str23, str24, str25, str26, str27, str28, str29, str30, str31, str32);
    }

    public Hashtable<String, String> requestApproval(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22, String str23, String str24, String str25, String str26, String str27, String str28) {
        if (str28.length() <= 0) {
            return this.m_VanMessage.requestApproval(str, i, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, str17, str18, str19, str20, str21, str22, str23, str24, str25, str26, str27, "N", "", "", "", "");
        }
        return this.m_VanMessage.requestApproval(str, i, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, str17, str18, str19, str20, str21, str22, str23, str24, str25, str26, str27, "S", "83", "                ", String.format("%d", new Object[]{Integer.valueOf(str28.length())}), str28);
    }

    public Hashtable<String, String> requestAuthentication(String str, int i, String str2, String str3) {
        return this.m_VanMessage.requestAuthentication(str, i, str2, str3);
    }

    public Hashtable<String, String> requestAuthentication(String str, int i, String str2, String str3, String str4, String str5, String str6) {
        return this.m_VanMessage.requestAuthentication(str, i, str2, str3, str4, str5, str6);
    }

    public Hashtable<String, String> requestRegistration(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9) {
        return this.m_VanMessage.requestRegistration(str, i, str2, str3, str4, str5, str6, str7, str8, str9);
    }

    public String encryptSign(String str) {
        return this.m_DES.encryptSign(str);
    }

    public String encryptSign(byte[] bArr) {
        return this.m_DES.encryptSign(bArr);
    }

    public String getErrorMessage() {
        return this.m_VanMessage.m_ErrorMsg;
    }

    public int getErrorCode() {
        return this.m_VanMessage.m_ErrorCode;
    }

    public byte[] getErrorTelegram() {
        return this.m_VanMessage.m_ErrorTelegram;
    }
}