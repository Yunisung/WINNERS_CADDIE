package com.mtouch.ksnet.dpt.design.appToApp;

import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class Response {
    public HashMap<String, Object> data = new HashMap<>();

    public HashMap getMap() {
        return data;
    }

    public String geKeyValye(String key) {
        return (String)data.get(key);
    }

    public HashMap<String, Object> geMap() {
        return  data;
    }

    public void setMap(String key, String value) {
        data.put(key, value);
    }

    public void setDataMap(String json ) {
        this.data.putAll(new GsonBuilder().create().fromJson(json, HashMap.class));
    }
}
