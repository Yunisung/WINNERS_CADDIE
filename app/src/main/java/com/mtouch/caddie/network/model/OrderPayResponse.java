package com.mtouch.caddie.network.model;

import com.mtouch.caddie.data.PayData;

public class OrderPayResponse extends Response{

    private PayData pay;

    public PayData getPay() {
        return pay;
    }

    public void setPay(PayData pay) {
        this.pay = pay;
    }
}
