package com.mtouch.caddie.network.model;

import java.io.Serializable;

public class PaymentInfo implements Serializable {

    public PaymentInfo() {
    }

    public PaymentInfo(String place) {
        this.place = place;
    }

    private String place;
    private String tipAmount;
    private String roundingAmount;

    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getRoundingAmount() {
        return roundingAmount;
    }

    public void setRoundingAmount(String roundingAmount) {
        this.roundingAmount = roundingAmount;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    @Override
    public String toString() {
        return "{"+
                "\"place\":\""+place+"\""
                +"}";
    }

}
