package com.mtouch.ksnet.dpt.ks03.obj;

import com.pswseoul.util.GsonUtil;
import com.pswseoul.util.ParamMap;

/**
 * @author Administrator
 *
 */
public class Response {
	public ParamMap<String,Object> data	= new ParamMap<String,Object>();
	
	public Response() {
		// TODO Auto-generated constructor stub
	}
	public String toJsonString(){
		return  GsonUtil.toJson(this,true,"");
	}

}
