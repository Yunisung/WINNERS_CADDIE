package com.bkwinners.caddie.network.model;

import java.io.Serializable;

public class OrderDetail implements Serializable {

    public static final String INTENT_KEY_ORDER_DETAIL = "order_detail";
    public static final String INTENT_KEY_ORDER_DETAIL_LIST = "order_detail_list";

    private String amount;
    private String trackId;
    private String regDate;
    private String trxId;
    private String number;
    private String name;
    private String payType;
    private String reqDay;
    private String _idx;
    private String gcKey;
    private String smsKey;
    private String resDate;
    private AmountDetail amountDetail;
    private String status;

    public AmountDetail getAmountDetail() {
        return amountDetail;
    }

    public void setAmountDetail(AmountDetail amountDetail) {
        this.amountDetail = amountDetail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalAmount() {
        return amount;
    }

    public void setTotalAmount(String totalAmount) {
        this.amount = totalAmount;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getReqDay() {
        return reqDay;
    }

    public void setReqDay(String reqDay) {
        this.reqDay = reqDay;
    }

    public String get_idx() {
        return _idx;
    }

    public void set_idx(String _idx) {
        this._idx = _idx;
    }

    public String getGcKey() {
        return gcKey;
    }

    public void setGcKey(String gcKey) {
        this.gcKey = gcKey;
    }

    public String getSmsKey() {
        return smsKey;
    }

    public void setSmsKey(String smsKey) {
        this.smsKey = smsKey;
    }

    public String getResDate() {
        return resDate;
    }

    public void setResDate(String resDate) {
        this.resDate = resDate;
    }

    public String getTip() {
        return amountDetail.getTip();
    }

    public String getRoundingAmount() {
        return amountDetail.getRoundingAmount();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
