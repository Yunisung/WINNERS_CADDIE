package com.bkwinners.caddie.network.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderListDetailResponse extends Response implements Serializable {

    private ArrayList<OrderDetail> list;

    public ArrayList<OrderDetail> getList() {
        return list;
    }

    public void setList(ArrayList<OrderDetail> list) {
        this.list = list;
    }
}
