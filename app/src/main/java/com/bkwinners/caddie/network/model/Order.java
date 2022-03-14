package com.bkwinners.caddie.network.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    public static final String INTENT_KEY_ORDER = "order";

    private String amount;
    private String resultCount;
    private String trackId;
    private String regDate;
    private ArrayList<Amount> amountList;
    private String reqDay;
    private String _idx;
    private String gcKey;
    private String reqPayCount;
    private String id;
    private PaymentInfo paymentInfo;
    private String status;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getResultCount() {
        return resultCount;
    }

    public void setResultCount(String resultCount) {
        this.resultCount = resultCount;
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

    public ArrayList<Amount> getAmountList() {
        return amountList;
    }

    public void setAmountList(ArrayList<Amount> amountList) {
        this.amountList = amountList;
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

    public String getReqPayCount() {
        return reqPayCount;
    }

    public void setReqPayCount(String reqPayCount) {
        this.reqPayCount = reqPayCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Order{" +
                "amount='" + amount + '\'' +
                ", resultCount='" + resultCount + '\'' +
                ", trackId='" + trackId + '\'' +
                ", regDate='" + regDate + '\'' +
                ", amountInfo=" + amount +
                ", reqDay='" + reqDay + '\'' +
                ", _idx='" + _idx + '\'' +
                ", gcKey='" + gcKey + '\'' +
                ", reqPayCount='" + reqPayCount + '\'' +
                ", id='" + id + '\'' +
                ", payInfo=" + paymentInfo +
                ", status='" + status + '\'' +
                '}';
    }
}
