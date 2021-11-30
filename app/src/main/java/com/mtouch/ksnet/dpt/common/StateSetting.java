package com.mtouch.ksnet.dpt.common;

import android.app.Application;

public class StateSetting extends Application {
    public static final int HOME = 1;
    public static final int PREV = 0;
    private Boolean IsBlueToothConnect = false;
    private Boolean IsBootFirst = true;
    private String ReaderMACAddr;
    private String ReaderModelName;
    private String SWModelName;
    private Boolean UseRegistReader = true;
    private String bizInfo;
    private String bizNo;
    private String boss;
    private String branchAddr;
    private String branchNm;
    private String branchPhone;
    private String dtpId;
    private String mDateEnd;
    private String mDateStart;
    private String mFlagCardNm;
    private TransResult.TrTypes mFlagTrType;
    private int mMode;
    private String msg1;
    private String msg2;
    private String msg3;
    private String msg4;
    private String posNo;
    private String status;
    private String userPhone;

    public void onCreate() {
        super.onCreate();
        this.mMode = 1;
        this.mFlagTrType = TransResult.TrTypes.All;
    }

    public void onTerminate() {
        super.onTerminate();
    }

    public void setTransType(TransResult.TrTypes trTypes) {
        this.mFlagTrType = trTypes;
    }

    public TransResult.TrTypes getTransType() {
        return this.mFlagTrType;
    }

    public void setTransCdName(String str) {
        this.mFlagCardNm = str;
    }

    public String getTransCdName() {
        return this.mFlagCardNm;
    }

    public void setDateStart(String str) {
        this.mDateStart = str;
    }

    public String getDateStart() {
        return this.mDateStart;
    }

    public void setDateEnd(String str) {
        this.mDateEnd = str;
    }

    public String getDateEnd() {
        return this.mDateEnd;
    }

    public int getMode() {
        return this.mMode;
    }

    public void setMode(int i) {
        this.mMode = i;
    }

    public String getBizNo() {
        return this.bizNo;
    }

    public void setBizNo(String str) {
        this.bizNo = str.trim();
    }

    public String getBizInfo() {
        return this.bizInfo;
    }

    public void setBizInfo(String str) {
        this.bizInfo = str.trim();
    }

    public String getPosNo() {
        return this.posNo;
    }

    public void setPosNo(String str) {
        this.posNo = str.trim();
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String str) {
        this.status = str.trim();
    }

    public String getMsg1() {
        return this.msg1;
    }

    public void setMsg1(String str) {
        this.msg1 = str.trim();
    }

    public String getMsg2() {
        return this.msg2;
    }

    public void setMsg2(String str) {
        this.msg2 = str.trim();
    }

    public String getMsg3() {
        return this.msg3;
    }

    public void setMsg3(String str) {
        this.msg3 = str.trim();
    }

    public String getMsg4() {
        return this.msg4;
    }

    public void setMsg4(String str) {
        this.msg4 = str.trim();
    }

    public String getBoss() {
        return this.boss;
    }

    public void setBoss(String str) {
        this.boss = str.trim();
    }

    public String getBranchNm() {
        return this.branchNm;
    }

    public void setBranchNm(String str) {
        this.branchNm = str.trim();
    }

    public String getBranchAddr() {
        return this.branchAddr;
    }

    public void setBranchAddr(String str) {
        this.branchAddr = str.trim();
    }

    public String getBranchPhone() {
        return this.branchPhone;
    }

    public void setBranchPhone(String str) {
        this.branchPhone = str.trim();
    }

    public String getDptID() {
        return this.dtpId;
    }

    public void setDptID(String str) {
        this.dtpId = str.trim();
    }

    public String getUserPhone() {
        return this.userPhone;
    }

    public void setUserPhone(String str) {
        this.userPhone = str.trim();
    }

    public String getSWModelName() {
        return this.SWModelName;
    }

    public void setSWModelName(String str) {
        this.SWModelName = str;
    }

    public String getReaderModelName() {
        return this.ReaderModelName;
    }

    public void setReaderModelName(String str) {
        this.ReaderModelName = str;
    }

    public String getReaderMACAddr() {
        return this.ReaderMACAddr;
    }

    public void setReaderMACAddr(String str) {
        this.ReaderMACAddr = str;
    }

    public Boolean getUseRegistReader() {
        return this.UseRegistReader;
    }

    public void setUseRegistReader(Boolean bool) {
        this.UseRegistReader = bool;
    }

    public Boolean GetIsBlueToothConnect() {
        return this.IsBlueToothConnect;
    }

    public void setIsBlueToothConnect(Boolean bool) {
        this.IsBlueToothConnect = bool;
    }

    public Boolean GetIsBootFirst() {
        return this.IsBootFirst;
    }

    public void setIsBootFirst(Boolean bool) {
        this.IsBootFirst = bool;
    }
}