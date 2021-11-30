package com.pswseoul.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtil {
	  public static final int NETWORK_WIFI = 0;
	  public static final int NETWORK_3G = 1;
	  public static final int NETWORK_NONE = 2;
	  
	  public static int checkStatus(Context context)
	  {
	    final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

	    if(wifi.isAvailable())
	    {
	      return NETWORK_WIFI;
	    }
	    else if(mobile.isAvailable())
	    {
	      return NETWORK_3G;
	    }
	    else
	    {
	      return NETWORK_NONE;
	    }

	  }
}
