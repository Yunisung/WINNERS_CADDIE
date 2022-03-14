package com.pswseoul.comunity.imp;

import android.net.Uri;


import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

public  interface AsyncTaskCompleteListener{
	String SCHEME = Uri.parse(BuildConfig.BASE_URL).getScheme();//"https";
	String AUTHORITY = Uri.parse(BuildConfig.BASE_URL).getAuthority();//"svctms.mtouch.com";

	String REGISTRY_STORE = "/v0/mcht/name";
	String REGISTRY_APP = "/v0/key";
	String AVALABLE_KEY = "/v0/key";
	String BASIC_VALUE = "/v0/trx/summary";
	String APPROVE_CHECK = "/v0/trx/rule";  // trackid
	String SERVER_SEND = "/v0/trx/push";

	// URI
	Uri REGISTRY_STORE_URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(REGISTRY_STORE).build();
	Uri REGISTRY_APP_URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(REGISTRY_APP).build();
	Uri AVALABLE_KEY_URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(AVALABLE_KEY).build();
	Uri BASIC_VALUE_URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(BASIC_VALUE).build();
	Uri APPROVE_CEHCK_URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(APPROVE_CHECK).build();
	Uri SERVER_SEND_URI = new Uri.Builder().scheme(SCHEME).authority(AUTHORITY).path(SERVER_SEND).build();

	// URI
	//"https://api.pay-sharp.com:7443/v0/trx/push";
	String REGISTRY_STORE_URL = SCHEME+"://"+AUTHORITY+ REGISTRY_STORE;
	String REGISTRY_APP_URL =  SCHEME+"://"+AUTHORITY+ REGISTRY_APP;
	String AVALABLE_KEY_URL = SCHEME+"://"+AUTHORITY+ AVALABLE_KEY;
	String BASIC_VALUE_URL = SCHEME+"://"+AUTHORITY+ BASIC_VALUE;
	String APPROVE_CEHCK_URL = SCHEME+"://"+AUTHORITY+ APPROVE_CHECK;
	String SERVER_SEND_URL =    SCHEME+"://"+AUTHORITY+ SERVER_SEND;

	public void onTaskComplete(String result);
}