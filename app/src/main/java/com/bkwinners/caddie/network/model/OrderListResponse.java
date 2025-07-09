package com.bkwinners.caddie.network.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderListResponse extends Response implements Serializable {
    private ArrayList<Order> list;

    public ArrayList<Order> getList() {
        return list;
    }

    public void setList(ArrayList<Order> list) {
        this.list = list;
    }
}
