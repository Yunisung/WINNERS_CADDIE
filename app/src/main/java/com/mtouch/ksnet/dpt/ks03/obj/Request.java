package com.mtouch.ksnet.dpt.ks03.obj;

import com.pswseoul.util.GsonUtil;
import com.pswseoul.util.ParamMap;

/**
 * @author Administrator
 *
 */
public class Request {
	public ParamMap<String,Object> data	= null;

	public Request( ) {
		data = new ParamMap<String,Object>();
	}

	public String toJsonString(){
		return  GsonUtil.toJson(this,true,"");
	}

}
