package com.bkwinners.caddie.network.model;

import java.util.ArrayList;

public class OrderListResponse extends Response{
    private ArrayList<Order> list;

    public ArrayList<Order> getList() {
        return list;
    }

    public void setList(ArrayList<Order> list) {
        this.list = list;
    }
}
