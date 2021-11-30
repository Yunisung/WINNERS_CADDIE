package com.mtouch.ksnet.dpt.common;

import android.app.Activity;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class TransResult {
    private DbHelper mDbHelper;

    public enum TrTypes {
        All,
        Card,
        Cash,
        Point
    }

    public TransResult(Activity activity) {
        this.mDbHelper = new DbHelper(activity);
    }

    public String getErrMsg() {
        return this.mDbHelper.getErrMsg();
    }

    public boolean inputTransResult(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, String str13, String str14, String str15, String str16, Hashtable<String, String> hashtable) {
        String str17;
        String str18;
        String str19;
        String str20;
        String str21;
        String str22;
        String str23;
        String str24;
        String str25;
        String str26;
        String str27;
        String str28;
        String str29;
        String str30;
        String str31;
        String str32;
        String str33;
        String str34;
        Hashtable<String, String> hashtable2 = hashtable;
        if (hashtable2 == null || !hashtable2.get("Status").equals("O")) {
            return false;
        }
        String str35 = hashtable2.containsKey("Point_Classification") ? hashtable2.get("Point_Classification") : hashtable2.get("Classification");
        if (hashtable2.containsKey("Point_Classification")) {
            str31 = hashtable2.get("Point_Authdate");
            String str36 = hashtable2.get("Point_Cardtype");
            String str37 = hashtable2.get("Point_AuthNum");
            str28 = hashtable2.get("Point_Code1");
            str27 = hashtable2.get("Point_CardName");
            str26 = hashtable2.get("Point_Code2");
            str25 = hashtable2.get("Point_CompName");
            String str38 = hashtable2.get("Point_Balance");
            str24 = hashtable2.get("Point_Type1");
            str23 = hashtable2.get("Point_Type2");
            str22 = hashtable2.get("Point_Type3");
            String str39 = hashtable2.get("Point_Notice1");
            StringBuilder sb = new StringBuilder();
            String str40 = hashtable2.get("Point_Notice2");
            sb.append(str.trim());
            sb.append(str37.trim());
            String str41 = hashtable2.get("Point_FranchiseID");
            sb.append(str31.substring(0, 6));
            str30 = sb.toString();
            str19 = "";
            str18 = str19;
            str17 = str18;
            str20 = str40;
            str32 = str37;
            str29 = str41;
            str21 = str39;
        } else {
            str31 = hashtable2.get("Authdate");
            str28 = hashtable2.get("IssueCode");
            str27 = hashtable2.get("CardName");
            str26 = hashtable2.get("PurchaseCode");
            str25 = hashtable2.get("PurchaseName");
            str24 = hashtable2.get("point1");
            str23 = hashtable2.get("point2");
            str22 = hashtable2.get("point3");
            str21 = hashtable2.get("notice1");
            String str42 = hashtable2.get("Filler").trim().replace(" ", "").split(";")[0];
            StringBuilder sb2 = new StringBuilder();
            String str43 = hashtable2.get("notice2");
            sb2.append(str.trim());
            sb2.append(str15.trim());
            String str44 = hashtable2.get("FranchiseID");
            sb2.append(str31.substring(0, 6));
            String sb3 = sb2.toString();
            str17 = hashtable2.get("BranchNM");
            str19 = str42;
            str20 = str43;
            str18 = hashtable2.get("BIZNO");
            str29 = str44;
            str30 = sb3;
            str32 = str15;
        }
        PrintStream printStream = System.out;
        String str45 = str25;
        StringBuilder sb4 = new StringBuilder();
        String str46 = str26;
        sb4.append(":::::TransResult : AuthNum");
        sb4.append(str32);
        printStream.println(sb4.toString());
        String trim = str6.trim().equals("") ? "0" : str6.trim();
        if (str7.trim().equals("")) {
            str33 = "0";
            str34 = str33;
        } else {
            str34 = str7.trim();
            str33 = "0";
        }
        String trim2 = str8.trim().equals("") ? str33 : str8.trim();
        String str47 = str28;
        String trim3 = str9.trim().equals("") ? str33 : str9.trim();
        String str48 = str27;
        String trim4 = trim3.trim().equals("") ? str33 : trim3.trim();
        String trim5 = str10.trim().equals("") ? str33 : str10.trim();
        String str49 = str29;
        String trim6 = trim5.trim().equals("") ? str33 : trim5.trim();
        String str50 = str34;
        String trim7 = str24.trim().equals("") ? str33 : str24.trim();
        String trim8 = str23.trim().equals("") ? str33 : str23.trim();
        String trim9 = str22.trim().equals("") ? str33 : str22.trim();
        StringBuilder sb5 = new StringBuilder();
        sb5.append("20");
        String str51 = trim9;
        sb5.append(str31.substring(0, 2));
        sb5.append("-");
        String str52 = trim8;
        sb5.append(str31.substring(2, 4));
        sb5.append("-");
        sb5.append(str31.substring(4, 6));
        sb5.append(" ");
        sb5.append(str31.substring(6, 8));
        sb5.append(":");
        sb5.append(str31.substring(8, 10));
        sb5.append(":");
        sb5.append(str31.substring(10, 12));
        String sb6 = sb5.toString();
        String trim10 = str5.trim();
        if (new String(hashtable2.get("TelegramType")).equals("0420")) {
            DbHelper dbHelper = this.mDbHelper;
            dbHelper.excuteNonQuery("UPDATE KSN_APP_TRANS SET TR_CNCLYN = 'Y', TR_TIME = '" + sb6 + "',TR_CNCLTIME = '" + sb6 + "' WHERE TR_ADMNO = '" + str32 + "';");
            return true;
        }
        return this.mDbHelper.excuteNonQuery("INSERT INTO KSN_APP_TRANS(TR_ID,TR_TYPE,TR_TERMID,TR_PEM,TR_CRDNO,TR_QUOTA,TR_SERVICE,TR_TAX,TR_AMOUNT,TR_FREE,TR_TOTAL,TR_USER,TR_RESVD,TR_RESVDK,TR_TIME,TR_ADMNO,TR_MEMNO,TR_CRDTYPE,TR_CRDNAME,TR_CISCODE,TR_CPRCODE,TR_CPRNAME,TR_BALANCE,TR_POINT1,TR_POINT2,TR_POINT3,TR_NOTICE1,TR_NOTICE2,TR_CNCLYN,TR_CNCLTIME,TR_STAFF_ID,TR_EV_ID,TR_BRANCHNM , TR_RESERVED1) VALUES('" + str30 + "','" + str + "','" + str2 + "','" + str4 + "','" + trim10 + "'," + Integer.parseInt(trim) + "," + Long.parseLong(trim2) + "," + Long.parseLong(trim4) + "," + Long.parseLong(trim5) + "," + Long.parseLong(trim6) + "," + Long.parseLong(str50) + ",'" + str12 + "','" + str13 + "','" + str14 + "','" + sb6 + "','" + str32 + "','" + str49 + "','" + "CardType" + "','" + str48 + "','" + str47 + "','" + str46 + "','" + str45 + "'," + Long.parseLong(str33) + "," + Long.parseLong(trim7) + "," + Long.parseLong(str52) + "," + Long.parseLong(str51) + ",'" + str21 + "','" + str20 + "','N','','" + str19 + "','" + str18 + "','" + str17 + "','" + str3 + "')");
    }

    public TypeSummary getTypeSummary(String str, String str2) {
        TypeSummary typeSummary = new TypeSummary();
        String[][] excuteQuery = this.mDbHelper.excuteQuery("SELECT TR_TYPE, TR_CNCLYN, COUNT(*), SUM(TR_TOTAL) FROM KSN_APP_TRANS WHERE DATE(TR_TIME) BETWEEN DATE('" + str + "') AND DATE('" + str2 + "') AND TR_CNCLYN = 'N' GROUP BY TR_TYPE, TR_CNCLYN;");
        if (excuteQuery != null) {
            for (String[] strArr : excuteQuery) {
                if (strArr[0].equals("MS") || strArr[0].equals("IC")) {
                    typeSummary.mCardCnt += Integer.parseInt(strArr[2]);
                    if (!strArr[1].equals("Y")) {
                        typeSummary.mCardAmt += Long.parseLong(strArr[3]);
                    }
                } else if (strArr[0].equals("HK")) {
                    typeSummary.mCashCnt += Integer.parseInt(strArr[2]);
                    if (strArr[0].equals("HK") && !strArr[1].equals("Y")) {
                        typeSummary.mCashAmt += Long.parseLong(strArr[3]);
                    }
                } else {
                    typeSummary.mPointCnt += Integer.parseInt(strArr[2]);
                }
            }
        }
        return typeSummary;
    }

    public CardSummary getStaffSummary(String str, String str2) {
        CardSummary cardSummary = new CardSummary();
        String[][] excuteQuery = this.mDbHelper.excuteQuery("SELECT TR_TYPE, TR_STAFF_ID, TR_CNCLYN, COUNT(*), SUM(TR_TOTAL) AS TOT FROM KSN_APP_TRANS WHERE DATE(TR_TIME) BETWEEN DATE('" + str + "') AND DATE('" + str2 + "') AND TR_CNCLYN = 'N' AND (TR_TYPE = 'IC' OR TR_TYPE = 'MS' OR TR_TYPE = 'HK') GROUP BY TR_STAFF_ID, TR_CNCLYN ORDER BY TOT DESC;");
        if (excuteQuery != null) {
            Hashtable hashtable = new Hashtable();
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (String[] strArr : excuteQuery) {
                if (!hashtable.containsKey(strArr[1])) {
                    hashtable.put(strArr[1], Integer.valueOf(i));
                    arrayList.add(strArr[1]);
                    i++;
                }
            }
            cardSummary.mNames = new String[i];
            cardSummary.mCounts = new int[i];
            cardSummary.mAmounts = new long[i];
            Iterator it = arrayList.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                cardSummary.mNames[i2] = (String) it.next();
                cardSummary.mCounts[i2] = 0;
                cardSummary.mAmounts[i2] = 0;
                i2++;
            }
            for (String[] strArr2 : excuteQuery) {
                int intValue = ((Integer) hashtable.get(strArr2[1])).intValue();
                int[] iArr = cardSummary.mCounts;
                iArr[intValue] = iArr[intValue] + Integer.parseInt(strArr2[3]);
                if ((strArr2[0].equals("IC") || strArr2[0].equals("MS") || strArr2[0].equals("HK")) && !strArr2[2].equals("Y")) {
                    long[] jArr = cardSummary.mAmounts;
                    jArr[intValue] = jArr[intValue] + Long.parseLong(strArr2[4]);
                }
            }
        }
        return cardSummary;
    }

    public CardSummary getBranchSummary(String str, String str2) {
        CardSummary cardSummary = new CardSummary();
        String[][] excuteQuery = this.mDbHelper.excuteQuery("SELECT TR_TYPE, TR_EV_ID, TR_CNCLYN, COUNT(*), SUM(TR_TOTAL) AS TOT FROM KSN_APP_TRANS WHERE DATE(TR_TIME) BETWEEN DATE('" + str + "') AND DATE('" + str2 + "') AND TR_CNCLYN = 'N' AND (TR_TYPE = 'IC' OR TR_TYPE = 'MS' OR TR_TYPE = 'HK') GROUP BY TR_EV_ID, TR_CNCLYN ORDER BY TOT DESC;");
        if (excuteQuery != null) {
            Hashtable hashtable = new Hashtable();
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (String[] strArr : excuteQuery) {
                if (!hashtable.containsKey(strArr[1])) {
                    hashtable.put(strArr[1], Integer.valueOf(i));
                    arrayList.add(strArr[1]);
                    i++;
                }
            }
            cardSummary.mNames = new String[i];
            cardSummary.mCounts = new int[i];
            cardSummary.mAmounts = new long[i];
            Iterator it = arrayList.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                cardSummary.mNames[i2] = (String) it.next();
                cardSummary.mCounts[i2] = 0;
                cardSummary.mAmounts[i2] = 0;
                i2++;
            }
            for (String[] strArr2 : excuteQuery) {
                int intValue = ((Integer) hashtable.get(strArr2[1])).intValue();
                int[] iArr = cardSummary.mCounts;
                iArr[intValue] = iArr[intValue] + Integer.parseInt(strArr2[3]);
                if ((strArr2[0].equals("IC") || strArr2[0].equals("MS")) && !strArr2[2].equals("Y")) {
                    long[] jArr = cardSummary.mAmounts;
                    jArr[intValue] = jArr[intValue] + Long.parseLong(strArr2[4]);
                }
            }
        }
        return cardSummary;
    }

    public CardSummary getCardSummary(String str, String str2) {
        CardSummary cardSummary = new CardSummary();
        String[][] excuteQuery = this.mDbHelper.excuteQuery("SELECT TR_TYPE, TR_CPRNAME, TR_CNCLYN, COUNT(*), SUM(TR_TOTAL) AS TOT FROM KSN_APP_TRANS WHERE DATE(TR_TIME) BETWEEN DATE('" + str + "') AND DATE('" + str2 + "') AND (TR_TYPE = 'JA' OR TR_TYPE = 'JC') GROUP BY TR_TYPE, TR_CPRNAME, TR_CNCLYN ORDER BY TOT DESC;");
        if (excuteQuery != null) {
            Hashtable hashtable = new Hashtable();
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (String[] strArr : excuteQuery) {
                if (!hashtable.containsKey(strArr[1])) {
                    hashtable.put(strArr[1], Integer.valueOf(i));
                    arrayList.add(strArr[1]);
                    i++;
                }
            }
            cardSummary.mNames = new String[i];
            cardSummary.mCounts = new int[i];
            cardSummary.mAmounts = new long[i];
            Iterator it = arrayList.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                cardSummary.mNames[i2] = (String) it.next();
                cardSummary.mCounts[i2] = 0;
                cardSummary.mAmounts[i2] = 0;
                i2++;
            }
            for (String[] strArr2 : excuteQuery) {
                int intValue = ((Integer) hashtable.get(strArr2[1])).intValue();
                int[] iArr = cardSummary.mCounts;
                iArr[intValue] = iArr[intValue] + Integer.parseInt(strArr2[3]);
                if ((strArr2[0].equals("IC") || strArr2[0].equals("MS")) && !strArr2[2].equals("Y")) {
                    long[] jArr = cardSummary.mAmounts;
                    jArr[intValue] = jArr[intValue] + Long.parseLong(strArr2[4]);
                }
            }
        }
        return cardSummary;
    }

    public String[] getCardPurchases(String str, String str2) {
        int i = 0;
        String[] strArr = new String[0];
        String[][] excuteQuery = this.mDbHelper.excuteQuery("SELECT TR_CPRNAME, SUM(TR_TOTAL) AS TOT FROM KSN_APP_TRANS WHERE DATE(TR_TIME) BETWEEN DATE('" + str + "') AND DATE('" + str2 + "') AND (TR_TYPE = 'JA' OR TR_TYPE = 'JC') GROUP BY TR_CPRNAME ORDER BY TOT DESC;");
        if (excuteQuery != null) {
            ArrayList arrayList = new ArrayList();
            for (String[] strArr2 : excuteQuery) {
                arrayList.add(strArr2[0]);
            }
            strArr = new String[arrayList.size()];
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                strArr[i] = (String) it.next();
                i++;
            }
        }
        return strArr;
    }

    public TransList getTransList(String str, String str2, TrTypes trTypes, String str3) {
        String str4;
        TransList transList = new TransList();
        if (trTypes == TrTypes.Card) {
            str4 = " AND (TR_TYPE = 'MS' OR TR_TYPE = 'IC')";
        } else if (trTypes == TrTypes.Cash) {
            str4 = " AND (TR_TYPE = 'HK')";
        } else {
            str4 = trTypes == TrTypes.Point ? " AND TR_TYPE (TR_TYPE = 'PC')" : "";
        }
        if (!str3.equals("")) {
            str4 = str4 + " AND TR_CPRNAME = '" + str3 + "'";
        }
        String[][] excuteQuery = this.mDbHelper.excuteQuery("SELECT TR_ID, TR_TIME, TR_TYPE, TR_CPRNAME, TR_QUOTA, TR_TOTAL,TR_CNCLYN, TR_CRDNO, TR_ADMNO , TR_EV_ID,TR_BRANCHNM ,TR_RESERVED1 FROM KSN_APP_TRANS WHERE DATE(TR_TIME) BETWEEN DATE('" + str + "') AND DATE('" + str2 + "')" + str4 + " ORDER BY TR_TIME DESC;");
        if (excuteQuery != null) {
            transList.mIDs = new String[excuteQuery.length];
            transList.mDateTimes = new String[excuteQuery.length];
            transList.mTypes = new String[excuteQuery.length];
            transList.mCprName = new String[excuteQuery.length];
            transList.mQuota = new int[excuteQuery.length];
            transList.mAmounts = new long[excuteQuery.length];
            transList.mCancels = new boolean[excuteQuery.length];
            transList.mCrdNo = new String[excuteQuery.length];
            transList.mAdmNo = new String[excuteQuery.length];
            transList.mBizNo = new String[excuteQuery.length];
            transList.mBranchNM = new String[excuteQuery.length];
            transList.mWorkType = new String[excuteQuery.length];
            int i = 0;
            for (String[] strArr : excuteQuery) {
                transList.mIDs[i] = strArr[0];
                transList.mDateTimes[i] = strArr[1].substring(2, 19).replace("-", "").replace(":", "").replace(" ", "");
                transList.mTypes[i] = strArr[2];
                transList.mCprName[i] = strArr[3];
                String str5 = "0";
                transList.mQuota[i] = Integer.parseInt(strArr[4].trim().equals("") ? str5 : strArr[4].trim());
                long[] jArr = transList.mAmounts;
                if (!strArr[5].trim().equals("")) {
                    str5 = strArr[5].trim();
                }
                jArr[i] = Long.parseLong(str5);
                transList.mCancels[i] = strArr[6].equals("Y");
                transList.mCrdNo[i] = strArr[7];
                transList.mAdmNo[i] = strArr[8];
                transList.mBizNo[i] = strArr[9];
                transList.mBranchNM[i] = strArr[10];
                transList.mWorkType[i] = strArr[11];
                i++;
            }
        }
        return transList;
    }

    public TransList getCreditCancelTransList(String str, String str2, TrTypes trTypes, String str3) {
        String str4;
        TransList transList = new TransList();
        if (!str3.equals("")) {
            str4 = "" + " AND TR_CPRNAME = '" + str3 + "'";
        } else {
            str4 = "";
        }
        String[][] excuteQuery = this.mDbHelper.excuteQuery("SELECT TR_ID, TR_TIME, TR_TYPE, TR_CPRNAME, TR_QUOTA, TR_TOTAL, TR_CRDNO,TR_CRDNAME,TR_ADMNO, TR_CNCLYN, TR_RESERVED1 FROM KSN_APP_TRANS WHERE DATE(TR_TIME) BETWEEN DATE('" + str + "') AND DATE('" + str2 + "')" + str4 + " AND TR_CNCLYN = 'N' ORDER BY TR_TIME DESC;");
        if (excuteQuery != null) {
            transList.mIDs = new String[excuteQuery.length];
            transList.mDateTimes = new String[excuteQuery.length];
            transList.mTypes = new String[excuteQuery.length];
            transList.mCprName = new String[excuteQuery.length];
            transList.mQuota = new int[excuteQuery.length];
            transList.mAmounts = new long[excuteQuery.length];
            transList.mCrdNo = new String[excuteQuery.length];
            transList.mCrdName = new String[excuteQuery.length];
            transList.mAdmNo = new String[excuteQuery.length];
            transList.mCancels = new boolean[excuteQuery.length];
            transList.mWorkType = new String[excuteQuery.length];
            int i = 0;
            for (String[] strArr : excuteQuery) {
                transList.mIDs[i] = strArr[0];
                transList.mDateTimes[i] = strArr[1].substring(2, 19).replace("-", "").replace(":", "").replace(" ", "");
                transList.mTypes[i] = strArr[2];
                if (strArr[2].trim().equals("HK")) {
                    transList.mCprName[i] = "현금영수증";
                } else if (strArr[2].trim().equals("PC")) {
                    transList.mCprName[i] = "포인트카드";
                } else {
                    transList.mCprName[i] = strArr[3];
                }
                transList.mQuota[i] = Integer.parseInt(strArr[4].trim().equals("") ? "0" : strArr[4].trim());
                transList.mAmounts[i] = Long.parseLong(strArr[5]);
                transList.mCrdNo[i] = strArr[6];
                transList.mCrdName[i] = strArr[7];
                transList.mAdmNo[i] = strArr[8];
                transList.mCancels[i] = strArr[9].equals("Y");
                transList.mWorkType[i] = strArr[10];
                i++;
            }
        }
        return transList;
    }

    public TransDetail getTransDetail(String str) {
        String str2;
        TransDetail transDetail = new TransDetail();
        String str3 = "";
        if (str.equals(str3)) {
            str2 = "SELECT TR_ID,TR_TYPE,TR_TERMID,TR_PEM,TR_CRDNO,TR_QUOTA,TR_SERVICE,TR_TAX,TR_AMOUNT,TR_TOTAL,TR_USER,TR_RESVD,TR_RESVDK,TR_TIME,TR_ADMNO,TR_MEMNO,TR_CRDTYPE,TR_CRDNAME,TR_CISCODE,TR_CPRCODE,TR_CPRNAME,TR_BALANCE,TR_POINT1,TR_POINT2,TR_POINT3,TR_NOTICE1,TR_NOTICE2,TR_CNCLYN,TR_CNCLTIME,TR_EV_ID,TR_BRANCHNM,TR_RESERVED1 FROM KSN_APP_TRANS WHERE TR_CNCLYN = 'N' AND TR_TYPE IN ('MS', 'IC') ORDER BY TR_TIME DESC LIMIT 1 OFFSET 0";
        } else {
            str2 = "SELECT TR_ID,TR_TYPE,TR_TERMID,TR_PEM,TR_CRDNO,TR_QUOTA,TR_SERVICE,TR_TAX,TR_AMOUNT,TR_TOTAL,TR_USER,TR_RESVD,TR_RESVDK,TR_TIME,TR_ADMNO,TR_MEMNO,TR_CRDTYPE,TR_CRDNAME,TR_CISCODE,TR_CPRCODE,TR_CPRNAME,TR_BALANCE,TR_POINT1,TR_POINT2,TR_POINT3,TR_NOTICE1,TR_NOTICE2,TR_CNCLYN,TR_CNCLTIME,TR_EV_ID,TR_BRANCHNM,TR_RESERVED1 FROM KSN_APP_TRANS WHERE TR_ID = '" + str + "';";
        }
        String[][] excuteQuery = this.mDbHelper.excuteQuery(str2);
        if (excuteQuery != null && excuteQuery.length > 0) {
            String[] strArr = excuteQuery[0];
            String str4 = strArr[1];
            if (str4.equals("JA")) {
                str4 = "JB";
            } else if (str4.equals("JG")) {
                str4 = "JH";
            } else if (str4.equals("BA")) {
                str4 = "BB";
            } else if (str4.equals("BE")) {
                str4 = "BF";
            } else if (str4.equals("BK")) {
                str4 = "BL";
            } else if (str4.equals("JC")) {
                str4 = "JD";
            } else if (str4.equals("JI")) {
                str4 = "JJ";
            } else if (str4.equals("BC")) {
                str4 = "BD";
            } else if (str4.equals("BG")) {
                str4 = "BH";
            } else if (str4.equals("BM")) {
                str4 = "BN";
            }
            transDetail.mTrType = str4;
            transDetail.mTermId = strArr[2];
            transDetail.mPem = strArr[3];
            transDetail.mCrdNo = strArr[4];
            String str5 = "0";
            transDetail.mQuota = Integer.parseInt(strArr[5].trim().equals(str3) ? str5 : strArr[5].trim());
            transDetail.mService = Long.parseLong(strArr[6].trim().equals(str3) ? str5 : strArr[6].trim());
            transDetail.mTax = Long.parseLong(strArr[7].trim().equals(str3) ? str5 : strArr[7].trim());
            transDetail.mAmount = Long.parseLong(strArr[8].trim().equals(str3) ? str5 : strArr[8].trim());
            transDetail.mTotal = Long.parseLong(strArr[9].trim().equals(str3) ? str5 : strArr[9].trim());
            transDetail.mUsrInfo = strArr[10];
            transDetail.mResvd = strArr[11];
            transDetail.mResvdKs = strArr[12];
            transDetail.mTrTime = strArr[13].substring(2, 19).replace("-", str3).replace(":", str3).replace(" ", str3);
            transDetail.mAdmNo = strArr[14];
            transDetail.mMemNo = strArr[15];
            transDetail.mCrdType = strArr[16];
            transDetail.mCrdName = strArr[17];
            transDetail.mCisCode = strArr[18];
            transDetail.mCprCode = strArr[19];
            transDetail.mCprName = strArr[20];
            transDetail.mBalance = Long.parseLong(strArr[21].trim().equals(str3) ? str5 : strArr[21].trim());
            transDetail.mPoint1 = Long.parseLong(strArr[22].trim().equals(str3) ? str5 : strArr[22].trim());
            transDetail.mPoint2 = Long.parseLong(strArr[23].trim().equals(str3) ? str5 : strArr[23].trim());
            if (!strArr[24].trim().equals(str3)) {
                str5 = strArr[24].trim();
            }
            transDetail.mPoint3 = Long.parseLong(str5);
            transDetail.mNotice1 = strArr[25];
            transDetail.mNotice2 = strArr[26];
            transDetail.mCancel = strArr[27].equals("Y");
            if (strArr[28].trim().length() > 0) {
                str3 = strArr[28].trim().substring(2, 19).replace("-", str3).replace(":", str3).replace(" ", str3);
            }
            transDetail.mBizNo = strArr[29];
            transDetail.mBranchNM = strArr[30];
            transDetail.mCnclTime = str3;
            transDetail.mWorkType = strArr[31];
        }
        return transDetail;
    }

    public TransDetail getTransDetail_AdminNo(String str) {
        TransDetail transDetail = new TransDetail();
        String[][] excuteQuery = this.mDbHelper.excuteQuery("SELECT TR_ID,TR_TYPE,TR_TERMID,TR_PEM,TR_CRDNO,TR_QUOTA,TR_SERVICE,TR_TAX,TR_AMOUNT,TR_TOTAL,TR_USER,TR_RESVD,TR_RESVDK,TR_TIME,TR_ADMNO,TR_MEMNO,TR_CRDTYPE,TR_CRDNAME,TR_CISCODE,TR_CPRCODE,TR_CPRNAME,TR_BALANCE,TR_POINT1,TR_POINT2,TR_POINT3,TR_NOTICE1,TR_NOTICE2,TR_CNCLYN,TR_CNCLTIME,TR_EV_ID,TR_BRANCHNM FROM KSN_APP_TRANS WHERE TR_ADMNO = '" + str + "';");
        if (excuteQuery != null && excuteQuery.length > 0) {
            String[] strArr = excuteQuery[0];
            String str2 = strArr[1];
            if (str2.equals("JA")) {
                str2 = "JB";
            } else if (str2.equals("JG")) {
                str2 = "JH";
            } else if (str2.equals("BA")) {
                str2 = "BB";
            } else if (str2.equals("BE")) {
                str2 = "BF";
            } else if (str2.equals("BK")) {
                str2 = "BL";
            } else if (str2.equals("JC")) {
                str2 = "JD";
            } else if (str2.equals("JI")) {
                str2 = "JJ";
            } else if (str2.equals("BC")) {
                str2 = "BD";
            } else if (str2.equals("BG")) {
                str2 = "BH";
            } else if (str2.equals("BM")) {
                str2 = "BN";
            }
            transDetail.mTrType = str2;
            transDetail.mTermId = strArr[2];
            transDetail.mPem = strArr[3];
            transDetail.mCrdNo = strArr[4];
            String str3 = "";
            String str4 = "0";
            transDetail.mQuota = Integer.parseInt(strArr[5].trim().equals(str3) ? str4 : strArr[5].trim());
            transDetail.mService = Long.parseLong(strArr[6].trim().equals(str3) ? str4 : strArr[6].trim());
            transDetail.mTax = Long.parseLong(strArr[7].trim().equals(str3) ? str4 : strArr[7].trim());
            transDetail.mAmount = Long.parseLong(strArr[8].trim().equals(str3) ? str4 : strArr[8].trim());
            transDetail.mTotal = Long.parseLong(strArr[9].trim().equals(str3) ? str4 : strArr[9].trim());
            transDetail.mUsrInfo = strArr[10];
            transDetail.mResvd = strArr[11];
            transDetail.mResvdKs = strArr[12];
            transDetail.mTrTime = strArr[13].substring(2, 19).replace("-", str3).replace(":", str3).replace(" ", str3);
            transDetail.mAdmNo = strArr[14];
            transDetail.mMemNo = strArr[15];
            transDetail.mCrdType = strArr[16];
            transDetail.mCrdName = strArr[17];
            transDetail.mCisCode = strArr[18];
            transDetail.mCprCode = strArr[19];
            transDetail.mCprName = strArr[20];
            transDetail.mBalance = Long.parseLong(strArr[21].trim().equals(str3) ? str4 : strArr[21].trim());
            transDetail.mPoint1 = Long.parseLong(strArr[22].trim().equals(str3) ? str4 : strArr[22].trim());
            transDetail.mPoint2 = Long.parseLong(strArr[23].trim().equals(str3) ? str4 : strArr[23].trim());
            if (!strArr[24].trim().equals(str3)) {
                str4 = strArr[24].trim();
            }
            transDetail.mPoint3 = Long.parseLong(str4);
            transDetail.mNotice1 = strArr[25];
            transDetail.mNotice2 = strArr[26];
            transDetail.mCancel = strArr[27].equals("Y");
            if (strArr[28].trim().length() > 0) {
                str3 = strArr[28].trim().substring(2, 19).replace("-", str3).replace(":", str3).replace(" ", str3);
            }
            transDetail.mBizNo = strArr[29];
            transDetail.mBranchNM = strArr[30];
            transDetail.mCnclTime = str3;
        }
        return transDetail;
    }

    public boolean deleteTransResult() {
        return this.mDbHelper.excuteNonQuery("DELETE FROM KSN_APP_TRANS;");
    }
}