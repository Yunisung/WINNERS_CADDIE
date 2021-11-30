package com.mtouch.caddie.network.model;

import java.util.ArrayList;

public class OrderListDetailResponse extends Response{

    private ArrayList<OrderDetail> list;

    public ArrayList<OrderDetail> getList() {
        return list;
    }

    public void setList(ArrayList<OrderDetail> list) {
        this.list = list;
    }
}
