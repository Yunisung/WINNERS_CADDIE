package com.bkwinners.caddie.network.model;

import java.io.Serializable;

public class Amount implements Serializable {
    private String payType;
    private String phone;
    private AmountDetail amountDetail;
    private String amount;
    private String name;

    public Amount() {
    }

    public Amount(String payType, String phone, String amount, String tip, String roundingAmount, String name) {
        this.payType = payType;
        this.phone = phone;
        this.amountDetail = new AmountDetail(tip,roundingAmount);
        this.amount = amount;
        this.name = name;
    }

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

    public String getTip() {
        return amountDetail.getTip();
    }

    public String getRoundingAmount() {
        return amountDetail.getRoundingAmount();
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "{"
                + "\"payType\":\"" + payType + "\""
                + ", \"phone\":\"" + phone + "\""
                + ", \"amountDetail\":" + amountDetail
                + ", \"amount\":\"" + amount + "\""
                + ", \"name\":\"" + name + "\""
                + "}";
    }
}
