package com.bkwinners.ksnet.dpt.action.obj;

import com.bkwinners.ksnet.dpt.action.process.ksnetmodule.obj.AdminInfo;
import com.pswseoul.util.GsonUtil;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;

/**
 * Created by parksuwon on 2018-02-19.
 */

public class receiveObj {
    public AdminInfo adminInfo;
    public HashMap<String , String> data = new HashMap<>();

    public String toJsonString(){
        return  GsonUtil.toJson(this,true,"");
    }

    public void setadminInfo(AdminInfo adminInfo ) {
        this.adminInfo = adminInfo;
    }

    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
