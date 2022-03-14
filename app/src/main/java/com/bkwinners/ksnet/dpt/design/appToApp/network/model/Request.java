package com.bkwinners.ksnet.dpt.design.appToApp.network.model;

import com.bkwinners.ksnet.dpt.design.util.GsonUtil;

import java.util.HashMap;

public class Request {
    public HashMap<String, Object> data	= null;

    public Request( ) {
        data = new HashMap<String, Object>();
    }

    public String toJsonString(){
        return  GsonUtil.toJson(this,true,"");
    }


    public Request put(String key, Object object){
        data.put(key,object);
        return this;
    }
}