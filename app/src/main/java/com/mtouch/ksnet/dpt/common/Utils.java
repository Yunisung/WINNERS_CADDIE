package com.mtouch.ksnet.dpt.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final String HEX = "0123456789ABCDEF";

    public enum PointAddType {
        Only,
        Double
    }

    public enum Pos_Entry_Mode {
        KEYIN,
        SWIPE,
        RF,
        IR
    }

    public enum TransactionType {
        OnlyCreate,
        DoubleCreatePoint
    }

    public static String approvalErrMsg(int i) {
        if (i == -100) {
            return "망취소 정상 완료";
        }
        switch (i) {
            case -106:
            case -105:
                return "망취소 완료";
            case -104:
                return "망취소 송신 실패.\n해당거래를 취소해주세요";
            case -103:
                return "망취소 연결 실패.\n해당거래를 취소해주세요";
            case -102:
                return "망취소 암호화 실패.\n해당거래를 취소해주세요";
            default:
                switch (i) {
                    case -7:
                        return "ACK 송실 실패";
                    case -6:
                        return "복호화 실패";
                    case -5:
                        return "수신 실패";
                    case -4:
                        return "송실 실패";
                    case -3:
                        return "연결 실패";
                    case -2:
                        return "암호화 실패";
                    case -1:
                        return "인자 오류";
                    default:
                        return "기타에러";
                }
        }
    }

    public static int byteToInt(byte b) {
        return b < 0 ? b + Handshake.HELLO_REQUEST : b;
    }

    public static String getPointType(int i) {
        return i == 1 ? "[현금] " : i == 2 ? "[신용] " : i == 3 ? "[직불] " : i == 4 ? "[자사]" : "";
    }

    public static String makeUrlStr(String str, String str2) {
        if (str.equals("")) {
            return str2;
        }
        if (str.substring(str.length() - 1).equals("/")) {
            return str + str2;
        }
        return str + "/" + str2;
    }

    public static byte[] toByte(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = Integer.valueOf(str.substring(i2, i2 + 2), 16).byteValue();
        }
        return bArr;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public static String byteArrayToHexString(byte[] bytes){

        StringBuilder sb = new StringBuilder();

        for(byte b : bytes){

            sb.append(String.format("%02X", b&0xff));
            sb.append(" ");
        }

        return sb.toString();
    }

    public static String stringToHex(String str) {
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            str2 = str2 + String.format("%02X ", new Object[]{Integer.valueOf(str.charAt(i))});
        }
        return str2;
    }

    public static void insertIntegrityInfoDB(String str, String str2) {
        DateFormat.getTimeInstance().format(new Date());
        String format = new SimpleDateFormat("yy-MM-dd  HH:mm:ss").format(new Date());
        new DbHelper(new Activity()).excuteNonQuery("INSERT INTO KSN_APP_INTEGRITY(IG_RESULT,IG_TYPE,IG_TIME) VALUES('" + str + "','" + str2 + "','" + format + "')");
    }

    public static byte[] intToByteArray(int i) {
        ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.putInt(i);
        allocate.order(ByteOrder.BIG_ENDIAN);
        return allocate.array();
    }

    public static String toHex(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (byte appendHex : bArr) {
            appendHex(stringBuffer, appendHex);
        }
        return stringBuffer.toString();
    }

    public static void appendHex(StringBuffer stringBuffer, byte b) {
        stringBuffer.append(HEX.charAt((b >> 4) & 15));
        stringBuffer.append(HEX.charAt(b & 15));
    }

    public static byte LRC(byte[] bArr) {
        byte b = 0;
        for (int i = 1; i < bArr.length - 1; i++) {
            b = (byte) (b ^ bArr[i]);
        }
        return b;
    }

    public static int bytesToInt(byte[] bArr) {
        return (bArr[0] & 255) | (bArr[3] << 24) | ((bArr[2] & 255) << Handshake.CLIENT_KEY_EXCHANGE) | ((bArr[1] & 255) << 8);
    }

    public static byte[] make2ByteLengh(int i) {
        byte[] bArr = new byte[2];
        bArr[1] = (byte) i;
        bArr[0] = (byte) (i >>> 8);
        return bArr;
    }

    public static String getAccountGmail(Activity activity) {
        Account[] accounts = AccountManager.get(activity).getAccounts();
        int length = accounts.length;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Account account = accounts[i];
            if (account.type.indexOf("com.google") != -1) {
                sb.append(account.name);
                break;
            }
            i++;
        }
        return sb.toString();
    }

    public static boolean isStringDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    public static boolean isStringNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    public static String getNumberFormat(String str) {
        return NumberFormat.getNumberInstance().format(Long.parseLong(str.replace(",", "")));
    }

    public static String insertComma(String str) {
        String str2 = new String();
        int i = 0;
        for (int length = str.length() - 1; length >= 0; length--) {
            i++;
            str2 = str.charAt(length) + str2;
            if (length > 0 && i == 3) {
                str2 = "," + str2;
                i = 0;
            }
        }
        return str2;
    }

    public static long getPercent(long j, double d) {
        return Math.round(((double) j) * (d / 100.0d));
    }

    public static boolean isTrack2Data(String str) {
        return str.length() >= 21;
    }

    public static boolean isSecurityPwd(String str) {
        return str.length() >= 8 && str.length() <= 15 && str.matches("^.*(?=^.{8,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*_+=]).*$");
    }

    public static int getByteArrLen(byte[] bArr) {
        byte[] bArr2 = new byte[1024];
        for (int i = 0; i < bArr.length; i++) {
            if (bArr[i] == 0) {
                return i + 1;
            }
        }
        return 0;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(13:0|1|2|(1:4)|5|6|(1:8)|9|10|(1:12)|13|14|15) */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0035, code lost:
        return false;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x002b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x000f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x001d */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x002a A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001c A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isUseRooting() {
        /*
            r0 = 1
            java.io.File r1 = new java.io.File     // Catch:{ Throwable -> 0x000f }
            java.lang.String r2 = "/system/app/Superuser.apk"
            r1.<init>(r2)     // Catch:{ Throwable -> 0x000f }
            boolean r1 = r1.exists()     // Catch:{ Throwable -> 0x000f }
            if (r1 == 0) goto L_0x000f
            return r0
        L_0x000f:
            java.io.File r1 = new java.io.File     // Catch:{ Throwable -> 0x001d }
            java.lang.String r2 = "/system/bin/su"
            r1.<init>(r2)     // Catch:{ Throwable -> 0x001d }
            boolean r1 = r1.exists()     // Catch:{ Throwable -> 0x001d }
            if (r1 == 0) goto L_0x001d
            return r0
        L_0x001d:
            java.io.File r1 = new java.io.File     // Catch:{ Throwable -> 0x002b }
            java.lang.String r2 = "/system/bin/busybox"
            r1.<init>(r2)     // Catch:{ Throwable -> 0x002b }
            boolean r1 = r1.exists()     // Catch:{ Throwable -> 0x002b }
            if (r1 == 0) goto L_0x002b
            return r0
        L_0x002b:
            java.lang.Runtime r1 = java.lang.Runtime.getRuntime()     // Catch:{ Exception -> 0x0035 }
            java.lang.String r2 = "su"
            r1.exec(r2)     // Catch:{ Exception -> 0x0035 }
            return r0
        L_0x0035:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: ksnet.common.Utils.isUseRooting():boolean");
    }

    public static boolean isTrack2PointData(String str) {
        return str.length() > 15 && str.replace("=", "").matches("^[0-9]*$");
    }

    public static boolean isCashTrack2Data(String str) {
        String trim = str.replace("=", "").trim();
        return trim.length() > 9 && trim.matches("^[0-9]*$");
    }

    public static boolean isHPCashReceipt(String str) {
        if (str.length() < 10 || str.length() > 11 || !isNumber2(str)) {
            return false;
        }
        return str.matches("01[016789]\\d{3,4}\\d{4}");
    }

    public static String getMaskHPCashReceipt(String str) {
        String str2;
        String substring = str.substring(3, str.length());
        if (substring.length() == 7) {
            substring = substring.substring(0, 3);
            str2 = str.replace(substring, "XXX");
        } else {
            str2 = "";
        }
        return substring.length() == 8 ? str.replace(substring.substring(0, 4), "XXXX") : str2;
    }

    public static boolean isNumber2(String str) {
        if (str.length() == 0) {
            return false;
        }
        return str.matches("^[0-9]*$");
    }

    public static boolean isValidDPT(String str) {
        if (str.length() == 0) {
            return false;
        }
        return str.matches("^[0-9A-Z]*$");
    }

    public static boolean isEmail(String str) {
        return str.matches("[\\w]+@[\\w]+(\\.[\\w]+)+");
    }

    public static int getByteLength(String str) {
        char[] cArr = new char[str.length()];
        int i = 0;
        for (int i2 = 0; i2 < cArr.length; i2++) {
            cArr[i2] = str.charAt(i2);
            i = cArr[i2] < 128 ? i + 1 : i + 2;
        }
        return i;
    }

    public static String getBizNo(String str) {
        if (str.equals("")) {
            return "";
        }
        return str.substring(0, 3) + "-" + str.substring(3, 5) + "-" + str.substring(5, 10);
    }

    public static String getThirdRange(String str, boolean z) {
        if (z) {
            String substring = str.substring(12, 18);
            return str.substring(0, 8) + "XXXX" + substring;
        }
        String str2 = str.split("=")[0];
        return str2.substring(0, str2.length() - 8) + "XXXX" + str2.substring(str2.length() - 4, str2.length());
    }

    public static String getTransDate(String str, boolean z) {
        String substring = str.substring(0, 2);
        String substring2 = str.substring(2, 4);
        String substring3 = str.substring(4, 6);
        String substring4 = str.substring(6, 8);
        String substring5 = str.substring(8, 10);
        String substring6 = str.substring(10, 12);
        if (z) {
            return substring2 + "/" + substring3 + " " + substring4 + ":" + substring5;
        }
        return "20" + substring + "-" + substring2 + "-" + substring3 + " " + substring4 + ":" + substring5 + ":" + substring6;
    }

    public static String getTransDate(String str) {
        String substring = str.substring(0, 2);
        String substring2 = str.substring(2, 4);
        String substring3 = str.substring(4, 6);
        return "20" + substring + "-" + substring2 + "-" + substring3;
    }

    public static String getPointType(String str) {
        String str2 = str.equals("1") ? "현금" : "";
        if (str.equals("2")) {
            str2 = "신용";
        }
        if (str.equals("3")) {
            str2 = "직불";
        }
        return str.equals("4") ? "자사" : str2;
    }

    public static String getTransType(String str) {
        String trim = str.trim();
        if (trim.equals("JB")) {
            return "신용승인";
        }
        if (trim.equals("JD")) {
            return "신용취소";
        }
        if (trim.equals("JF")) {
            return "신용카드 망취소";
        }
        if (trim.equals("JH")) {
            return "현금승인";
        }
        if (trim.equals("JJ")) {
            return "현금취소";
        }
        if (trim.equals("JN")) {
            return "직불카드 승인";
        }
        if (trim.equals("JP")) {
            return "직불카드 승인취소";
        }
        return trim.equals("JT") ? "직불카드 망취소" : "";
    }

    public static String getMembershipType(String str) {
        if (str.equals("1")) {
            return "SKT ";
        }
        if (str.equals("2")) {
            return "KTF ";
        }
        return str.equals("3") ? "LGT " : "";
    }

    public static String getCardType(String str) {
        String trim = str.trim();
        if (trim.equals("N")) {
            return "신용카드";
        }
        if (trim.equals("G")) {
            return "기프트 카드";
        }
        if (trim.equals("C")) {
            return "체크 카드";
        }
        if (trim.equals("P")) {
            return "선불 카드";
        }
        if (trim.equals("O")) {
            return "OCB 원카드";
        }
        return trim.equals("B") ? "국민고운맘 바우처" : "현금/직불/수표";
    }

    public static boolean checkDate(String str) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        simpleDateFormat.setLenient(false);
        try {
            simpleDateFormat.parse(str);
            return true;
        } catch (IllegalArgumentException | ParseException unused) {
            return false;
        }
    }

    public static boolean isUseCardNo(String str) {
        return str.length() >= 15 && isNumber2(str);
    }

    public static boolean isAuthNo(String str) {
        return str.length() > 1 && isNumber2(str);
    }

    public static boolean isUseExpiryDate(String str, String str2) {
        if (isNumber2(str) && isNumber2(str2)) {
            return true;
        }
        return false;
    }

    public static int getPayType(String str) {
        if (isStringNumber(str)) {
            return Integer.parseInt(str);
        }
        return 0;
    }

    public static String getAuthDate(String str) {
        String[] split = str.split("\\-");
        String substring = split[0].substring(2, 4);
        String str2 = split[1];
        String str3 = split[2];
        return substring + str2 + str3;
    }

    public static int getPixcelToDip(Resources resources, int i) {
        return (int) (((float) i) * resources.getDisplayMetrics().density);
    }

    public static String[] getTransaction(Configuration configuration) {
        return configuration.getConfig("", new Configuration.Types[]{Configuration.Types.TransType, Configuration.Types.MSRType, Configuration.Types.Comm_IP, Configuration.Types.Comm_Port}, new String[]{"1", "1", "210.181.28.137", "9562"});
    }

    public static String byteToString(byte[] bArr, int i, int i2) {
        if (i + i2 > bArr.length) {
            return "~~~";
        }
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        try {
            return new String(bArr2, "EUC-KR");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] byteToSubByte(byte[] bArr, int i, int i2) {
        byte[] bArr2 = new byte[i2];
        if (i + i2 > bArr.length) {
            return bArr2;
        }
        System.arraycopy(bArr, i, bArr2, 0, i2);
        return bArr2;
    }

    public static String[] getTaxServiceRate(String str, Configuration configuration) {
        return configuration.getConfig(str, new Configuration.Types[]{Configuration.Types.Tax_Use, Configuration.Types.Service_Use, Configuration.Types.Tax_Rate, Configuration.Types.Service_Rate}, new String[]{"0", "0", "10.0", ""});
    }

    public static String[] getPassword(Configuration configuration) {
        return configuration.getConfig("", new Configuration.Types[]{Configuration.Types.Password}, new String[]{"0"});
    }

    public static TransResult.TrTypes getSaleType(String str) {
        TransResult.TrTypes trTypes = TransResult.TrTypes.All;
        if (str.equals("신용카드")) {
            return TransResult.TrTypes.Card;
        }
        if (str.equals("현금영수증")) {
            return TransResult.TrTypes.Cash;
        }
        return str.equals("포인트") ? TransResult.TrTypes.Point : trTypes;
    }

    public static boolean IsWifiAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null) {
            return false;
        }
        try {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
            if (!networkInfo.isAvailable() || !networkInfo.isConnected()) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean Is3GAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null) {
            return false;
        }
        try {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
            if (!networkInfo.isAvailable() || !networkInfo.isConnected()) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        try {
            String line1Number = telephonyManager.getLine1Number() != null ? telephonyManager.getLine1Number() : "010-0000-0000";
            String substring = line1Number.substring(line1Number.length() - 10, line1Number.length());
            if (substring.substring(0, 1).equals("2")) {
                substring = substring.substring(1);
            }
            if (substring.substring(0, 1).equals("0")) {
                return substring;
            }
            return "0" + substring;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}