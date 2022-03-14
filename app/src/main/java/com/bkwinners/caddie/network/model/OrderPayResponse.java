package com.bkwinners.caddie.network.model;

import com.bkwinners.caddie.data.PayData;
import com.bkwinners.caddie.data.WidgetData;

public class OrderPayResponse extends Response{

    private PayData pay;
    private WidgetData widget;

    public PayData getPay() {
        return pay;
    }

    public void setPay(PayData pay) {
        this.pay = pay;
    }

    public WidgetData getWidget() { return this.widget; }
    public void setWidget(WidgetData widget) { this.widget = widget; }
}
