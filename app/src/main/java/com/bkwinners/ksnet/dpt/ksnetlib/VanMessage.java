package com.bkwinners.ksnet.dpt.ksnetlib;

import androidx.core.view.InputDeviceCompat;

import androidx.core.view.InputDeviceCompat;

import com.bkwinners.ksnet.dpt.common.EncMSRManager;
import com.bkwinners.ksnet.dpt.common.Utility;

import java.util.Hashtable;

class VanMessage {
    EncryptComm m_EncryptComm = new EncryptComm();
    public int m_ErrorCode = 0;
    public String m_ErrorMsg = "";
    public byte[] m_ErrorTelegram;
    Utility m_Util = new Utility();

    /* access modifiers changed from: package-private */
    public Hashtable<String, String> parseMessage(byte[] bArr) {
        if (bArr != null) {
            return parseMessage(bArr, bArr.length);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public Hashtable<String, String> parseMessage(byte[] bArr, int i) {
        Hashtable<String, String> hashtable = new Hashtable<>();
        int parseInt = Integer.parseInt(this.m_Util.byteToString(bArr, 0, 4), 10);
        hashtable.put("Classification", this.m_Util.byteToString(bArr, 5, 2));
        hashtable.put("UniqNum", this.m_Util.byteToString(bArr, 21, 12));
        hashtable.put("Status", this.m_Util.byteToString(bArr, 33, 1));
        hashtable.put("Authdate", this.m_Util.byteToString(bArr, 34, 12));
        hashtable.put("Cardtype", this.m_Util.byteToString(bArr, 46, 1));
        hashtable.put("Message1", this.m_Util.byteToString(bArr, 47, 16));
        hashtable.put("Message2", this.m_Util.byteToString(bArr, 63, 16));
        hashtable.put("AuthNum", this.m_Util.byteToString(bArr, 79, 12));
        hashtable.put("FranchiseID", this.m_Util.byteToString(bArr, 91, 15));
        hashtable.put("Code1", this.m_Util.byteToString(bArr, 106, 2));
        hashtable.put("CardName", this.m_Util.byteToString(bArr, 108, 16));
        hashtable.put("Code2", this.m_Util.byteToString(bArr, 124, 2));
        hashtable.put("CompName", this.m_Util.byteToString(bArr, 126, 16));
        hashtable.put("Balance", this.m_Util.byteToString(bArr, 160, 9));
        hashtable.put("point1", this.m_Util.byteToString(bArr, 169, 9));
        hashtable.put("point2", this.m_Util.byteToString(bArr, 178, 9));
        hashtable.put("point3", this.m_Util.byteToString(bArr, 187, 9));
        hashtable.put("Notice1", this.m_Util.byteToString(bArr, 196, 20));
        hashtable.put("Notice2", this.m_Util.byteToString(bArr, 216, 40));
        String byteToString = this.m_Util.byteToString(bArr, 256, 1);
        hashtable.put("IC_Flag", byteToString);
        if (byteToString.equals("H")) {
            hashtable.put("EMV_Data", this.m_Util.byteToString(bArr, InputDeviceCompat.SOURCE_KEYBOARD, ((((parseInt + 4) - 2) - 40) - 5) - InputDeviceCompat.SOURCE_KEYBOARD));
        } else {
            hashtable.put("EMV_Data", "");
        }
        int i2 = ((parseInt + 4) - 2) - 40;
        hashtable.put("Reserved", this.m_Util.byteToString(bArr, i2 - 5, 5));
        hashtable.put("KSNET_Reserved", this.m_Util.byteToString(bArr, i2, 40));
        return hashtable;
    }

    public byte[] requestApproval(String str, int i, byte[] bArr) {
        byte[] bArr2 = new byte[(bArr.length + 1)];
        bArr2[0] = EncMSRManager.ERROR_NOCARD;
        System.arraycopy(bArr, 0, bArr2, 1, bArr.length);
        byte[] kspay_send_socket = this.m_EncryptComm.kspay_send_socket(str, i, bArr2, false);
        this.m_ErrorCode = Integer.parseInt(this.m_EncryptComm.m_sErrorCode);
        if (this.m_ErrorCode >= 100) {
            this.m_ErrorTelegram = new byte[this.m_EncryptComm.m_bErrorTelegram.length];
            byte[] bArr3 = this.m_EncryptComm.m_bErrorTelegram;
            byte[] bArr4 = this.m_ErrorTelegram;
            System.arraycopy(bArr3, 0, bArr4, 0, bArr4.length);
        }
        this.m_ErrorMsg = this.m_EncryptComm.m_sErrorMsg;
        return kspay_send_socket;
    }

    public Hashtable<String, String> requestApproval(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, String str17, String str18, String str19, String str20, String str21, String str22, String str23, String str24, String str25, String str26, String str27, String str28, String str29, String str30, String str31, String str32) {
        String str33 = str30;
        String str34 = "1";
        if (str2.length() <= 2 && str3.length() <= 10 && str4.length() <= 4 && str5.length() <= 12 && str6.length() <= 1 && str7.length() <= 37 && str8.length() <= 2 && str9.length() <= 9 && str10.length() <= 9 && str11.length() <= 9 && str12.length() <= 9 && str13.length() <= 2 && str14.length() <= 16 && str15.length() <= 12 && str16.length() <= 6 && str17.length() <= 13 && str18.length() <= 2 && str19.length() <= 30 && str20.length() <= 4 && str21.length() <= 20 && str22.length() <= 1 && str23.length() <= 1 && str24.length() <= 1 && str25.length() <= 1 && str26.length() <= 1 && str28.length() <= 1 && str29.length() <= 2 && str30.length() <= 16) {
            str34 = "";
        }
        Hashtable<String, String> hashtable = new Hashtable<>();
        if (str34 != "") {
            hashtable.put("ErrorCode", str34);
            hashtable.put("ErrorMsg", "전문 필드 길이 오류");
            hashtable.put("ErrorTelegram", "");
            return hashtable;
        } else if (this.m_Util.stringToByte("Test Encoding") == null) {
            if (this.m_ErrorMsg.length() > 0) {
                this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + "\n";
            }
            this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + this.m_Util.m_sbErrorMsg;
            hashtable.put("ErrorCode", "2");
            hashtable.put("ErrorMsg", "인코딩(ksc5601) 오류");
            hashtable.put("ErrorTelegram", "");
            return hashtable;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(2);
            stringBuffer.append(this.m_Util.matchFormat(str2, 2, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str3, 10, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str4, 4, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str5, 12, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str6, 1, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str7, 37, 'X'));
            stringBuffer.append(28);
            stringBuffer.append(this.m_Util.matchFormat(str8, 2, '9'));
            stringBuffer.append(this.m_Util.matchFormat(str9, 9, '9'));
            stringBuffer.append(this.m_Util.matchFormat(str10, 9, '9'));
            stringBuffer.append(this.m_Util.matchFormat(str11, 9, '9'));
            stringBuffer.append(this.m_Util.matchFormat(str12, 9, '9'));
            stringBuffer.append(this.m_Util.matchFormat(str13, 2, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str14, 16, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str15, 12, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str16, 6, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str17, 13, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str18, 2, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str19, 30, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str20, 4, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str21, 20, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str22, 1, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str23, 1, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str24, 1, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str25, 1, 'X'));
            stringBuffer.append(this.m_Util.matchFormat(str26, 1, 'X'));
            stringBuffer.append(str27);
            stringBuffer.append(this.m_Util.matchFormat(str28, 1, 'X'));
            if (str33 != null) {
                stringBuffer.append(this.m_Util.matchFormat(str29, 2, '9'));
                stringBuffer.append(this.m_Util.matchFormat(str33, 16, 'X'));
                stringBuffer.append(this.m_Util.matchFormat(str31, 4, '9'));
                stringBuffer.append(str32);
            }
            stringBuffer.append(3);
            stringBuffer.append(13);
            stringBuffer.insert(0, this.m_Util.matchFormat(stringBuffer.length(), 4, '9'));
            stringBuffer.insert(0, "2");
            byte[] stringToByte = this.m_Util.stringToByte(stringBuffer.toString());
            if (stringToByte != null) {
                byte[] kspay_send_socket = this.m_EncryptComm.kspay_send_socket(str, i, stringToByte, false);
                if (kspay_send_socket == null) {
                    if (this.m_ErrorMsg.length() > 0) {
                        this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + "\n";
                    }
                    this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + this.m_EncryptComm.m_sErrorMsg;
                } else {
                    hashtable = parseMessage(kspay_send_socket);
                }
                hashtable.put("ErrorCode", this.m_EncryptComm.m_sErrorCode);
                hashtable.put("ErrorMsg", this.m_EncryptComm.m_sErrorMsg);
                if (this.m_EncryptComm.m_bErrorTelegram != null) {
                    hashtable.put("ErrorTelegram", new String(this.m_EncryptComm.m_bErrorTelegram, 0, this.m_EncryptComm.m_bErrorTelegram.length));
                } else {
                    hashtable.put("ErrorTelegram", "");
                }
            } else {
                hashtable.put("ErrorCode", "3");
                hashtable.put("ErrorMsg", "전문 생성 오류");
                hashtable.put("ErrorTelegram", "");
            }
            return hashtable;
        }
    }

    public Hashtable<String, String> requestAuthentication(String str, int i, String str2, String str3) {
        return requestAuthentication(str, i, str2, "", "", str3, "");
    }

    public Hashtable<String, String> requestAuthentication(String str, int i, String str2, String str3, String str4, String str5, String str6) {
        if (this.m_Util.stringToByte("Test Encoding") == null) {
            if (this.m_ErrorMsg.length() > 0) {
                this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + "\n";
            }
            this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + this.m_Util.m_sbErrorMsg;
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(2);
        stringBuffer.append(this.m_Util.matchFormat("DU", 2, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str2, 10, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str3, 8, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str4, 6, '9'));
        stringBuffer.append(this.m_Util.matchFormat(str5, 10, '9'));
        stringBuffer.append(this.m_Util.matchFormat(str6, 21, 'X'));
        stringBuffer.append(3);
        stringBuffer.append(13);
        stringBuffer.insert(0, "2");
        byte[] stringToByte = this.m_Util.stringToByte(stringBuffer.toString());
        if (stringToByte == null) {
            return null;
        }
        byte[] kspay_send_socket = this.m_EncryptComm.kspay_send_socket(str, i, stringToByte, false);
        if (kspay_send_socket == null) {
            if (this.m_ErrorMsg.length() > 0) {
                this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + "\n";
            }
            this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + this.m_EncryptComm.m_sErrorMsg;
            return null;
        }
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("Classification", this.m_Util.byteToString(kspay_send_socket, 1, 2));
        hashtable.put("Dpt_Id", this.m_Util.byteToString(kspay_send_socket, 3, 10));
        hashtable.put("Enterprise_Info", this.m_Util.byteToString(kspay_send_socket, 13, 8));
        hashtable.put("Full_Text_Num", this.m_Util.byteToString(kspay_send_socket, 21, 6));
        hashtable.put("Status", this.m_Util.byteToString(kspay_send_socket, 27, 1));
        hashtable.put("SendDate", this.m_Util.byteToString(kspay_send_socket, 28, 12));
        hashtable.put("Message1", this.m_Util.byteToString(kspay_send_socket, 40, 16));
        hashtable.put("Message2", this.m_Util.byteToString(kspay_send_socket, 56, 16));
        hashtable.put("Representative", this.m_Util.byteToString(kspay_send_socket, 72, 10));
        hashtable.put("FranchiseID", this.m_Util.byteToString(kspay_send_socket, 82, 50));
        hashtable.put("FranchiseAddress", this.m_Util.byteToString(kspay_send_socket, 132, 50));
        hashtable.put("FranchiseTel", this.m_Util.byteToString(kspay_send_socket, 182, 15));
        hashtable.put("Filler", this.m_Util.byteToString(kspay_send_socket, 197, 11));
        return hashtable;
    }

    public Hashtable<String, String> requestRegistration(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9) {
        if (this.m_Util.stringToByte("Test Encoding") == null) {
            if (this.m_ErrorMsg.length() > 0) {
                this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + "\n";
            }
            this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + this.m_Util.m_sbErrorMsg;
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(2);
        stringBuffer.append(this.m_Util.matchFormat("PW", 2, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str2, 10, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str3, 8, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str4, 6, '9'));
        stringBuffer.append(this.m_Util.matchFormat(str5, 10, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str6, 10, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str7, 3, '9'));
        stringBuffer.append(this.m_Util.matchFormat(str8, 20, 'X'));
        stringBuffer.append(this.m_Util.matchFormat(str9, 38, 'X'));
        stringBuffer.append(3);
        stringBuffer.append(13);
        stringBuffer.insert(0, "2");
        byte[] stringToByte = this.m_Util.stringToByte(stringBuffer.toString());
        if (stringToByte == null) {
            return null;
        }
        String str10 = str;
        byte[] kspay_send_socket = this.m_EncryptComm.kspay_send_socket(str, i, stringToByte, false);
        if (kspay_send_socket == null) {
            if (this.m_ErrorMsg.length() > 0) {
                this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + "\n";
            }
            this.m_ErrorMsg = String.valueOf(this.m_ErrorMsg) + this.m_EncryptComm.m_sErrorMsg;
            return null;
        }
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("Classification", this.m_Util.byteToString(kspay_send_socket, 1, 2));
        hashtable.put("bizRegNum", this.m_Util.byteToString(kspay_send_socket, 3, 10));
        hashtable.put("bizInfo", this.m_Util.byteToString(kspay_send_socket, 13, 8));
        hashtable.put("posNum", this.m_Util.byteToString(kspay_send_socket, 21, 6));
        hashtable.put("Status", this.m_Util.byteToString(kspay_send_socket, 27, 1));
        hashtable.put("Message1", this.m_Util.byteToString(kspay_send_socket, 28, 16));
        hashtable.put("Message2", this.m_Util.byteToString(kspay_send_socket, 44, 16));
        hashtable.put("Message3", this.m_Util.byteToString(kspay_send_socket, 60, 16));
        hashtable.put("Message4", this.m_Util.byteToString(kspay_send_socket, 76, 16));
        hashtable.put("Representative", this.m_Util.byteToString(kspay_send_socket, 92, 10));
        hashtable.put("FranchiseID", this.m_Util.byteToString(kspay_send_socket, 102, 50));
        hashtable.put("FranchiseAddress", this.m_Util.byteToString(kspay_send_socket, 152, 50));
        hashtable.put("FranchiseTel", this.m_Util.byteToString(kspay_send_socket, 202, 15));
        return hashtable;
    }
}