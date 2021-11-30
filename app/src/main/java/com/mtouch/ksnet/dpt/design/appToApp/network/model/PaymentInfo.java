package com.mtouch.ksnet.dpt.design.appToApp.network.model;

import java.io.Serializable;

public class PaymentInfo implements Serializable {

    private String rfdTime;        // "",
    private String amount;         // "1004",
    private String van;            // "KSPAY1",
    private String vanTrxId;       // "TX200410001188",
    private String authCd;         // "20243540",
    private String tmnId;          // "TEST0003",
    private String trackId;        // "TX200410001188",
    private String bin;            // "540926",
    private String cardType;       // "신용",
    private String trxId;          // "T200410001461",
    private String issuer;         // "국민",
    private String regDay;         // "20200410",
    private String resultMsg;      // "정상승인:국민마스타특별:OK:20243540",
    private String number;         // "540926**********",
    private String trxResult;      // "승인",
    private String regTime;        // "165711",
    private String vanId;          // "2006400005",
    private String _idx;           // "1",
    private String installment;    // "00",
    private String rfdDay;         // "",
    private String mchtId;         // "ktest",
    private String brand;          // "KB국민카드",
    private String rfdId;          // ""



    public String getRfdTime() {
        return rfdTime;
    }

    public void setRfdTime(String rfdTime) {
        this.rfdTime = rfdTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVan() {
        return van;
    }

    public void setVan(String van) {
        this.van = van;
    }

    public String getVanTrxId() {
        return vanTrxId;
    }

    public void setVanTrxId(String vanTrxId) {
        this.vanTrxId = vanTrxId;
    }

    public String getAuthCd() {
        return authCd;
    }

    public void setAuthCd(String authCd) {
        this.authCd = authCd;
    }

    public String getTmnId() {
        return tmnId;
    }

    public void setTmnId(String tmnId) {
        this.tmnId = tmnId;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getRegDay() {
        return regDay;
    }

    public void setRegDay(String regDay) {
        this.regDay = regDay;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTrxResult() {
        return trxResult;
    }

    public void setTrxResult(String trxResult) {
        this.trxResult = trxResult;
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime;
    }

    public String getVanId() {
        return vanId;
    }

    public void setVanId(String vanId) {
        this.vanId = vanId;
    }

    public String get_idx() {
        return _idx;
    }

    public void set_idx(String _idx) {
        this._idx = _idx;
    }

    public String getInstallment() {
        return installment;
    }

    public void setInstallment(String installment) {
        this.installment = installment;
    }

    public String getRfdDay() {
        return rfdDay;
    }

    public void setRfdDay(String rfdDay) {
        this.rfdDay = rfdDay;
    }

    public String getMchtId() {
        return mchtId;
    }

    public void setMchtId(String mchtId) {
        this.mchtId = mchtId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRfdId() {
        return rfdId;
    }

    public void setRfdId(String rfdId) {
        this.rfdId = rfdId;
    }
}
