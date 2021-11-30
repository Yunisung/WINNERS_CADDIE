package com.mtouch.ksnet.dpt.design.appToApp.sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {


    /**
     *
     */
    public PatternUtil() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 주민등록번호/외국인등록번호 유효성 체크
     *
     * @param residentRegistrationNo 주민등록번호/외국인등록번호
     * @return 유효한 주민등록번호/외국인등록번호 형식 여부
     */
    public static boolean isResidentRegistrationNo(String residentRegistrationNo) {
        String juminNo = residentRegistrationNo.replaceAll("[^0-9]", "");
        if (juminNo.length() != 13) {
            return false;
        }
        int yy = to_int(juminNo.substring(0, 2));
        int mm = to_int(juminNo.substring(2, 4));
        int dd = to_int(juminNo.substring(4, 6));
        if (yy < 1 || yy > 99 || mm > 12 || mm < 1 || dd < 1 || dd > 31) {
            return false;
        }
        int sum = 0;
        int juminNo_6 = to_int(juminNo.charAt(6));
        if (juminNo_6 == 1 || juminNo_6 == 2 || juminNo_6 == 3 || juminNo_6 == 4) {
            //내국인
            for (int i = 0; i < 12; i++) {
                sum += to_int(juminNo.charAt(i)) * ((i % 8) + 2);
            }
            if (to_int(juminNo.charAt(12)) != (11 - (sum % 11)) % 10) {
                return false;
            }
            return true;
        } else if (juminNo_6 == 5 || juminNo_6 == 6 || juminNo_6 == 7 || juminNo_6 == 8) {
            //외국인
            if (to_int(juminNo.substring(7, 9)) % 2 != 0) {
                return false;
            }
            for (int i = 0; i < 12; i++) {
                sum += to_int(juminNo.charAt(i)) * ((i % 8) + 2);
            }
            if (to_int(juminNo.charAt(12)) != ((11 - (sum % 11)) % 10 + 2) % 10) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 주민등록번호/외국인등록번호 유효성 체크
     *
     * @param juminNo 주민등록번호/외국인등록번호
     * @return 유효한 주민등록번호/외국인등록번호 형식 여부
     */
    public static boolean isJuminNo(String juminNo) {
        return isResidentRegistrationNo(juminNo);
    }

    /**
     * 법인번호 유효성 체크
     *
     * @param corporationRegistrationNo 법인번호
     * @return 유효한 법인번호 형식 여부
     */
    public static boolean isCorporationRegistrationNo(String corporationRegistrationNo) {
        String corpRegNo = corporationRegistrationNo.replaceAll("[^0-9]", "");
        if (corpRegNo.length() != 13) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += ((i % 2) + 1) * to_int(corpRegNo.charAt(i));
        }
        if (to_int(corpRegNo.charAt(12)) != (10 - (sum % 10)) % 10) {
            return false;
        }
        return true;

    }

    /**
     * 사업자등록번호 유효성 체크
     *
     * @param businessRegistrationNo 사업자등록번호
     * @return 유효한 사업자등록번호 형식 여부
     */
    public static boolean isBusinessRegistrationNo(String businessRegistrationNo) {
        String bizRegNo = businessRegistrationNo.replaceAll("[^0-9]", "");
        if (bizRegNo.length() != 10) {
            return false;
        }
        int share = (int) (Math.floor(to_int(bizRegNo.charAt(8)) * 5) / 10);
        int rest = (to_int(bizRegNo.charAt(8)) * 5) % 10;
        int sum = (to_int(bizRegNo.charAt(0))) + ((to_int(bizRegNo.charAt(1)) * 3) % 10) + ((to_int(bizRegNo.charAt(2)) * 7) % 10) + ((to_int(bizRegNo.charAt(3)) * 1) % 10) + ((to_int(bizRegNo.charAt(4)) * 3) % 10) + ((to_int(bizRegNo.charAt(5)) * 7) % 10) + ((to_int(bizRegNo.charAt(6)) * 1) % 10) + ((to_int(bizRegNo.charAt(7)) * 3) % 10) + share + rest + (to_int(bizRegNo.charAt(9)));
        if (sum % 10 != 0) {
            return false;
        }
        return true;
    }

    /**
     * 신용카드번호 유효성 체크
     *
     * @param creditCardNo 신용카드번호
     * @return 유효한 신용카드번호 형식 여부
     */
    public static boolean isCreditCardNo(String creditCardNo) {
        return PatternUtil.matchCreditCardNo(creditCardNo).find();
    }

    /**
     * 여권번호 유효성 체크
     *
     * @param passportNo 여권번호
     * @return 유효한 여권번호 형식 여부
     */
    public static boolean isPassportNo(String passportNo) {
        return PatternUtil.matchPassportNo(passportNo).find();
    }

    /**
     * 운전면허번호 유효성 체크
     *
     * @param driversLicenseNo 운전면허번호
     * @return 유효한 운전면허번호 형식 여부
     */
    public static boolean isDriversLicenseNo(String driversLicenseNo) {
        return PatternUtil.matchDriversLicenseNo(driversLicenseNo).find();
    }

    /**
     * 휴대폰번호 유효성 체크
     *
     * @param cellphoneNo 휴대폰번호
     * @return 유효한 휴대폰번호 형식 여부
     */
    public static boolean isCellphoneNo(String cellphoneNo) {
        return PatternUtil.matchCellphoneNo(cellphoneNo).find();
    }

    /**
     * 일반전화번호 유효성 체크
     *
     * @param telephoneNo 전화번호
     * @return 유효한 전화번호 형식 여부
     */
    public static boolean isTelephoneNo(String telephoneNo) {
        return PatternUtil.matchTelephoneNo(telephoneNo).find();
    }



    /**
     * 이메일주소 유효성 체크
     *
     * @param emailAddress 이메일주소
     * @return 유효한 이메일주소 형식 여부
     */
    public static boolean isEmailAddress(String emailAddress) {
        return PatternUtil.matchEmailAddress(emailAddress).find();
    }

    /**
     * 아이피주소 유효성 체크
     *
     * @param ipAddress 아이피주소
     * @return 유효한 아이피주소 형식 여부
     */
    public static boolean isIPAddress(String ipAddress) {
        return PatternUtil.matchIPAddress(ipAddress).find();
    }



    /**
     * 주민등록번호 패턴
     */
    private static final Pattern RESIDENT_REGISTRATION_NO = Pattern.compile("\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12][0-9]|3[01])(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*[1-8]\\d{6}", Pattern.MULTILINE);

    private static Matcher matchResidentRegistrationNo(String residentRegistrationNo) {
        return BUSINESS_REGISTRATION_NO.matcher(residentRegistrationNo);
    }

    /**
     * 주민등록번호 패턴
     */
    private static final Pattern JUMIN_NO = RESIDENT_REGISTRATION_NO;

    private static Matcher matchJuminNo(String juminNo) {
        return JUMIN_NO.matcher(juminNo);
    }

    /**
     * 법인번호 패턴
     */
    private static final Pattern CORPORATION_REGISTRATION_NO = Pattern.compile("\\d{6}(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*\\d{7}");

    private static Matcher matchCorporationRegistrationNo(String corporationRegistrationNo) {
        return CORPORATION_REGISTRATION_NO.matcher(corporationRegistrationNo);
    }

    /**
     * 사업자등록번호 패턴
     */
    private static final Pattern BUSINESS_REGISTRATION_NO = Pattern.compile("[0-9]{3}(?:\\s|&nbsp;)*-(?:\\s|&nbsp;)*[0-9]{2}(?:\\s|&nbsp;)*-(?:\\s|&nbsp;)*[0-9]{5}", Pattern.MULTILINE);

    private static Matcher matchBusinessRegistrationNo(String businessRegistrationNo) {
        return BUSINESS_REGISTRATION_NO.matcher(businessRegistrationNo);
    }

    /**
     * 신용카드번호 패턴
     */
    private static final Pattern CREDIT_CARD_NO = Pattern.compile("(?:5[1-5]\\d{14})|(?:4\\d{12}(\\d{3})?)|(?:3[47]\\d{13})|(?:6011\\d{12})|(?:(?:30[0-5]|36\\d|38\\d)\\d{11})", Pattern.MULTILINE);

    private static Matcher matchCreditCardNo(String creditCardNo) {
        return CREDIT_CARD_NO.matcher(creditCardNo);
    }

    /**
     * 여권번호 패턴
     */
    private static final Pattern PASSPORT_NO = Pattern.compile("");

    private static Matcher matchPassportNo(String passportNo) {
        return PASSPORT_NO.matcher(passportNo);
    }

    /**
     * 운전면허번호 패턴
     */
    private static final Pattern DRIVERS_LICENSE_NO = Pattern.compile("");

    private static Matcher matchDriversLicenseNo(String driversLicenseNo) {
        return DRIVERS_LICENSE_NO.matcher(driversLicenseNo);
    }

    /**
     * 휴대폰번호 패턴
     */
    private static final Pattern CELLPHONE_NO = Pattern.compile("01(?:0|1|6|7|8|9)(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*(?:\\d{4}|\\d{3})(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*\\d{4}", Pattern.MULTILINE);

    private static Matcher matchCellphoneNo(String cellphoneNo) {
        return CELLPHONE_NO.matcher(cellphoneNo);
    }

    /**
     * 일반전화번호 패턴
     */
    private static final Pattern TELEPHONE_NO = Pattern.compile("(?:02|0[3-9]{1}[0-9]{1})(?:\\s|&nbsp;)*(?:\\)|-)?(?:\\s|&nbsp;)*(?:\\d{4}|\\d{3})(?:\\s|&nbsp;)*-?(?:\\s|&nbsp;)*\\d{4}", Pattern.MULTILINE);

    private static Matcher matchTelephoneNo(String telephoneNo) {
        return TELEPHONE_NO.matcher(telephoneNo);
    }


    /**
     * 이메일주소 패턴
     */
    private static final Pattern EMAIL_ADDRESS = Pattern.compile("(?:\\w+\\.)*\\w+@(?:\\w+\\.)+[A-Za-z]+", Pattern.MULTILINE);

    private static Matcher matchEmailAddress(String emailAddress) {
        return EMAIL_ADDRESS.matcher(emailAddress);
    }

    /**
     * 아이피주소 패턴
     */
    private static final Pattern IP_ADDRESS = Pattern.compile("(?:(?:(?:\\d{1,2})|(?:1\\d{2})|(?:2[0-4]\\d)|(?:25[0-5]))\\.){3}(?:(?:\\d{1,2})|(?:1\\d{2})|(?:2[0-4]\\d)|(?:25[0-5]))", Pattern.MULTILINE);

    private static Matcher matchIPAddress(String ipAddress) {
        return IP_ADDRESS.matcher(ipAddress);
    }

    private static int to_int(char c) {
        return Integer.parseInt(String.valueOf(c));
    }

    /**
     * String으로 표현된 숫자를 타입을 int로 변경
     */
    private static int to_int(String s) {
        return Integer.parseInt(s);
    }

}
