package com.bkwinners.ksnet.dpt.design.appToApp;

import com.bkwinners.ksnet.dpt.design.util.GsonUtil;

public class ResponseObj extends Response{
    public String resultCd  = "-1";
    public String resultMsg  = "";
    public String processingCd  = "";


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

    public String getStringValue(String key){
        return (String) data.get(key);
    }





}
