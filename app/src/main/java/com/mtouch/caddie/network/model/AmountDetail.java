package com.mtouch.caddie.network.model;

import java.io.Serializable;

class AmountDetail implements Serializable {
    private String tip;
    private String roundingAmount;

    public AmountDetail(String tip, String roundingAmount) {
        this.tip = tip;
        this.roundingAmount = roundingAmount;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getRoundingAmount() {
        return roundingAmount;
    }

    public void setRoundingAmount(String roundingAmount) {
        this.roundingAmount = roundingAmount;
    }

    @Override
    public String toString() {
        return "{"
                + "\"tip\":\"" + tip + "\""
                + ", \"roundingAmount\":\"" + roundingAmount + "\""
                + "}";
    }
}