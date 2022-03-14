package com.bkwinners.caddie.data;

import java.io.Serializable;
import java.util.ArrayList;

public class PayData implements Serializable {

    private String authCd;
    private Card card;
    private ArrayList<Product> products;
    private String transactionDate;
    private String trxId;
    private String trxType;
    private String tmnId;
    private String trackId;
    private Double amount;
    private String udf1;
    private String udf2;

    public String getAuthCd() {
        return authCd;
    }

    public void setAuthCd(String authCd) {
        this.authCd = authCd;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getTrxType() {
        return trxType;
    }

    public void setTrxType(String trxType) {
        this.trxType = trxType;
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

    public String getAmount() {
        return String.valueOf(amount.intValue());
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }
}
