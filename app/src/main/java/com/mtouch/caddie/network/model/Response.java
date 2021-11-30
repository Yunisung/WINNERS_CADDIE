package com.mtouch.caddie.network.model;

import java.util.HashMap;

public class Response {
    private String resultCd;
    private String resultMsg;
    private HashMap<String, Object> data;

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public String getResultCd() {
        return resultCd;
    }

    public void setResultCd(String resultCd) {
        this.resultCd = resultCd;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public boolean isSuccess(){
        return resultCd!=null && resultCd.equals("0000");
    }
}
