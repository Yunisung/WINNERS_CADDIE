package com.bkwinners.ksnet.dpt.action.obj;

import com.google.gson.GsonBuilder;
import com.pswseoul.util.GsonUtil;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;

/**
 * Created by parksuwon on 2018-02-19.
 */

public class responseObj {
    public String resultCd  = "-1";
    public String resultMsg  = "";
    public String processingCd  = "";
    public HashMap<String , String> data = new HashMap<>();

    public HashMap getMap() {
        return data;
    }

    public String toJsonString(){
        return  GsonUtil.toJson(this,true,"");
    }

    public void setResultCd(String resultCd ) {
        this.resultCd = resultCd;
        data.put("resultCd" , resultCd);
    }

    public void setProcessingCd(String processingCd ) {
        this.processingCd = processingCd;
        data.put("processingCd" , processingCd);
    }
    public void setResultMsg(String resultMsg ) {
        this.resultMsg = resultMsg;
        data.put("resultMsg" , resultMsg);
    }

    public String geKeyValye(String  key) {
        return (String)data.get(key);
    }

    public HashMap<String , String>  geMap() {
        return  data;
    }

    public void setMap(String  key, String value) {
        data.put(key, value);
    }

    public void setDataMap(String  json ) {
        this.data.putAll(new GsonBuilder().create().fromJson(json, HashMap.class));
    }

    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
