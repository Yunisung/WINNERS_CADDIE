package com.pswseoul.util;

//import java.awt.image.renderable.ParameterBlock;

import java.io.BufferedReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;


public class tools {

	  public static boolean debug_mode = true;
  
      public static String deli			= "\b";	  
      public final static String  SPACE =	"                                    ";
      
      /* log file parameter    */
      
      public static boolean  system_output = true;
      public static boolean  file_output = false;
      public static boolean  debug_out = false;
      
      public static String dir = "";
      
      //public static XecureSmart mXecureSmart;
      public static boolean verify_real = false;
      
      public static  boolean _test = false;
      
      public static String encry_key = ""; 
      
      public final static String  version = "0.0"; 
      
	  public static byte[] key			= null;
	  public static final byte[] right	= new byte[]{(byte)0x0, (byte)0x7F, (byte)0x3F, (byte)0x1F, (byte)0xF, (byte)0x7, (byte)0x3, (byte)0x1};
	  public static final byte[] left	= new byte[]{(byte)0xFF, (byte)0xFE, (byte)0xFC, (byte)0xF8, (byte)0xF0, (byte)0xE0, (byte)0xC0, (byte)0x80};

      public static String sDate = "";
      
      private final static Pattern LTRIM = Pattern.compile("^\\s+");
      private final static Pattern RTRIM = Pattern.compile("\\s+$");
      
      public static String web_address = "http://www.usefeel.com/";
      public static String web_url = "app/appjson.php?";
      
      public static String customkey  = "app_001";
      public static String userkey  = "ices_000000001";
      
      public static String gubun[] = {"board" , "company" , "contact" , "reserv" , "coupon" , "notice" , "gallery"}; 
    	      
      public static String  regId = "";
      
      public static String file_path ="/sdcard/shopping.txt";
      
      public static int device_width = 0;

      public static int device_height = 0;

      public static String ltrim(String s) {
          return LTRIM.matcher(s).replaceAll("");
      }

      public static String rtrim(String s) {
          return RTRIM.matcher(s).replaceAll("");
      }

	  public static byte[] getStrMoneytoTgAmount(String Money)
	{
		byte[] TgAmount = null;
		if (Money.length() == 0 ) {
			return "000000001004".getBytes();
		}else
		{
			Long longMoney = Long.parseLong(Money.replace(",", ""));
			Money = String.format("%012d",longMoney);

			return Money.getBytes();
		}

	}


      public static String getPacket(String param) {
    	  StringBuffer sb = new StringBuffer();    	  
    	  sb.append(web_address+web_url+"customkey="+customkey+"&userkey="+userkey+"&");
    	  sb.append("param="+param);
    	  /*
    	  if(param.equals("all")) {
    		  sb.append("param="+param);
    	  } else if(param.equals("board")) {
    		  
    	  } else if(param.equals("company")) {
    		  
    	  } else if(param.equals("contact")) {
    		  
    	  } else if(param.equals("reserv")) {
    		  
    	  } else if(param.equals("point")) {
    		  
    	  } else if(param.equals("notice")) {
    		  
    	  } else if(param.equals("contentview")) {
    		  
    	  }
    	  */
    	  return sb.toString();
      }
      /*
      http://www.usefeel.com/app/appjson.php?customkey=app_001&userkey=ices_000000001&param=all 
    	  http://www.usefeel.com/app/appjson.php?customkey=app_001&userkey=ices_000000001&param=board
    	  http://www.usefeel.com/app/appjson.php?customkey=app_001&userkey=ices_000000001&param=company
    	  http://www.usefeel.com/app/appjson.php?customkey=app_001&userkey=ices_000000001&param=contact
    	  http://www.usefeel.com/app/appjson.php?customkey=app_001&userkey=ices_000000001&param=reserv
    	  http://www.usefeel.com/app/appjson.php?customkey=app_001&userkey=ices_000000001&param=point
    	  http://www.usefeel.com/app/appjson.php?customkey=app_001&userkey=ices_000000001&param=notice
    	  http://www.usefeel.com/app/appjson.php?customkey=app_001&userkey=ices_000000001&param=contentview&docno=123 <--������ȣ(document_srl)�Է�
      */
      public static  void addToMakePackage() {
    	    if(verify_real) addToMakePackage(version);
      }
      
      public static byte[]  addToMakePackage(Object o) {
    	  byte[] data_buf = new byte[]{};
    	  
          if(o  instanceof Boolean) {
        	  verify_real = true;
    	  } else if(o  instanceof String) {
    	 	 String O = (String)o ;

			  	 data_buf = tools.addArray(data_buf, tools.int2byte(O.getBytes().length));
				 data_buf = tools.addArray(data_buf, O.getBytes());

    	  } else if(o  instanceof byte[]) {
      		 byte[] O = (byte[])o ;
			 data_buf = tools.addArray(data_buf, tools.int2byte(O.length));
			 data_buf = tools.addArray(data_buf, O);
			 
    	  } else if(o  instanceof Integer) { 
    		 byte[] buf = tools.int2byte(((Number)o).intValue());
	  	  	 data_buf = tools.addArray(data_buf, buf);

    	  } else if(o  instanceof Double) {
    		 byte[] buf = tools.double2byte(((Number)o).doubleValue());
	  		data_buf = tools.short2byte((short)6);
			data_buf = tools.addArray(data_buf, buf);	 
    	  }  else {
			  Log.d("debug","Dont't Catch Data type");
    		  
    	  }
    	  return data_buf;
      }
//Double
      	public static String getSysyyyyMMdd(){
      		return getSysDate("yyyy-MM-dd");
	  	}
	  	public static String getSysHHmmss(){
	  		return getSysDate("HH-mm-ss-SSS");
	  	}
	  	
	  	public static String getUgiDate(){
	  		return getSysDate("yyyy.MM.dd HH:mm:ss");
	  	}
	  	
	  	public static String getUgiTime(){
	  		return getSysDate("yyyy.MM.dd");
	  	}
	  	
	  	public static String getConcorrentTime(){
	  		return getSysDate("yyyy.MM.dd.HH.mm");
	  	}
	  
	  public static String getSysDateTIme() {
		    return getSysDate("yyyyMMddHHmmss");
	 }
	  
	  public static String getMysqlDate(){
	  		return getSysDate("yyyy-MM-dd HH:mm:ss");
	  }
	   	
	     
	  public static String getDate() {
		    return getSysDate("yyyyMMdd");
	  }
	  
	  public static String getTime(){
		  return getSysDate("HH");
	  }
	  
	  public static String getHourMin(){
		  return getSysDate("HHmm");
	  }
	  
	  public static String getDateTime() {
	    return getSysDate("HHmmss");
	  }

	  public static String getSysDate() {
		return getSysDate("yyyyMMddHHmmSS");
	  }
	  
	  public static String getSysTime(){
		  return getSysDate("yyyyMMddHH");
	  }

	  public static String getSysDateMM() {
		return getSysDate("yyyyMMddHHmm");
	  }	  
	  
	  public static int  getSystemMM() {
		   String s_mm = getSysDate("HHmm");
		   int HH = Integer.parseInt(s_mm.substring(0, 2));
		   int MM = Integer.parseInt(s_mm.substring(2));
           
		   return ( (HH * 60) + MM );
	  }	  

	  public static String getSysDate(String fmt) {
	    SimpleDateFormat formatter = new SimpleDateFormat(fmt, Locale.KOREAN);
	    Date currentDate = new Date(); 
	    return formatter.format(currentDate);
	  }

	  public static String getBeforSysDateHour(int hour) {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
			Calendar today = Calendar.getInstance ( );
			today.add( Calendar.HOUR, 11 );
			Date currentDate = today.getTime();
		    return formatter.format(currentDate);
	  }	  

	  public static String getBeforSysDateMin(int min) {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
			Calendar today = Calendar.getInstance ( );
			today.add( Calendar.MINUTE, min );
			Date currentDate = today.getTime();
		    return formatter.format(currentDate);
	  }
	  
	  public static String getBeforSysDate(int day) {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
			Calendar today = Calendar.getInstance ( );
			today.add( Calendar.DATE, day );
			
			Date currentDate = today.getTime();
		    return formatter.format(currentDate);
	  }
	  
	// 
	  public static boolean ComparisonTime(String nowtime, String comparetime){
		  
		 int i_N_Date = Integer.parseInt(nowtime.substring(0, 8));
		 int i_N_Time = Integer.parseInt(nowtime.substring(8, 12));
		 
		 int i_C_Date = Integer.parseInt(comparetime.substring(0, 8));
		 int i_C_Time = Integer.parseInt(comparetime.substring(8, 12));
		 
		 
		 
		 //
		 
		 if ( i_N_Date < i_C_Date ){
			 return false;
		 }else{
			 if ( i_N_Time < i_C_Time ){
				 return false;
			 }else{
				 return true;
			 }
		 }
	  }
	  
	  // 
	  public static String MinusTime(String compareTime, String nowTime){
		  
		  DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			
		  try{
			  
			  Date d1 = df.parse(compareTime);
			  Date d2 = df.parse(nowTime);
			
			  long time = d1.getTime() - d2.getTime();
			
			  
			  if ( time > 2147483647 )
				  time = 2147483647;
			  
			  return "" +time;
			
		  }catch(Exception e){ e.printStackTrace(); }
		  
		  return null;
		  
	  }
	  
	  public static int getSec(String zentime, String nowtime){
		  if( zentime.equals("0") || zentime == null )
			  return 0;
		  
		  if ( nowtime.equals("0") || nowtime == null )
			  return 0;
		  
		  int zen_year = Integer.parseInt(zentime.substring(0, 4));
		  int zen_month = Integer.parseInt(zentime.substring(4, 6));
		  int zen_day = Integer.parseInt(zentime.substring(6, 8));
		  int zen_hour = Integer.parseInt(zentime.substring(8, 10));
		  int zen_min = Integer.parseInt(zentime.substring(10, 12));
		  
		  int now_year = Integer.parseInt(nowtime.substring(0, 4));
		  int now_month = Integer.parseInt(nowtime.substring(4, 6));
		  int now_day = Integer.parseInt(nowtime.substring(6, 8));
		  int now_hour = Integer.parseInt(nowtime.substring(8, 10));
		  int now_min = Integer.parseInt(nowtime.substring(10, 12));
		  
		  int days[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		  int day = getDaysOfMonth(zen_year, 2);
		  days[1] = day;
		  
		  int zen_yeartodays = zen_year * 365;
		  int zen_allDay = 0;
		  
		  if( zen_month != 1){
			  int zen_count = zen_month - 2;
			  int zen_addDays = 0;
			  for(int i=0; i<zen_count+1; i++){
				  zen_addDays += days[i];
			  }
			  zen_allDay = zen_yeartodays + zen_addDays + zen_day;
		  }
		  else{
			  zen_allDay = zen_yeartodays + zen_day;
		  }
		  
		  int now_yeartodays = now_year * 365;
		  int now_allDay = 0;
		  
		  if( now_month != 1 ){
			  int now_count = now_month - 2;
			  int now_addDays = 0;
			  for(int i=0; i<now_count+1; i++){
				  now_addDays += days[i];
			  }
			  now_allDay = now_yeartodays + now_addDays + now_day;
		  } else{
			  now_allDay = now_yeartodays + now_day;
		  }
		  
		  if ( zen_allDay - now_allDay < 0 )
			  return 0;
		  
		  else{
			  int resultsec = (zen_allDay - now_allDay) * 24 * 60 * 60;
			  
			  int zen_sec = (zen_hour * 60 * 60) + (zen_min * 60 );
			  int now_sec = (now_hour * 60 * 60) + (now_min * 60 );
			  
			  int sec = zen_sec - now_sec;
			  
			  int totalsec = resultsec + sec;
			  
			  return totalsec;
		  }
	  }
	  

//==============================================================================//
	  
	  /**
	   
	   */
	  public static int getDaysOfMonth(int year, int month) {
	    int days[] = {
	        31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	    int day = days[month - 1];

	    if ( ( (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) &&
	        (month == 2)) {
	      day++;
	    }

	    return day;
	  }

	  public static String[] duparray(String s[], int len) {
		    String tmps[] = new String[len];
		    System.arraycopy(s, 0, tmps, 0, len);
		    return tmps;
		  }

		  public static String[] duparray(String s[]) {
		    if (s == null)return null;

		    String tmps[] = new String[s.length];
		    System.arraycopy(s, 0, tmps, 0, s.length);
		    return tmps;
		  }
		 
			public static String[] strToArray(String str) {
		        return strToArray(str, deli);
		    }			  

		  public static String[] strToArray(String str, String deli) {
		    if (str == null || str.length() == 0)return new String[] {};

		    StringTokenizer st = new StringTokenizer(str, deli);

		    String[] tmp = new String[st.countTokens()];
		    for (int i = 0; i < tmp.length; i++) {
		      tmp[i] = st.nextToken();
		    }

		    return tmp;
		  }
		  
			public static String arrayToStr(String str[]) {
				String ret = "";
				if(str == null) return "";

				for(int i = 0; i < str.length; i++) {
					if (i == (str.length - 1)) ret = ret + str[i];
					else ret = ret + str[i] + deli;
				}
				return ret;
				
				
			} 			  
		  	  

		  public static String arrayToStr(String str[], String deli) {
		    String ret = "";
		    if (str == null)return "";

		    for (int i = 0; i < str.length; i++) {
		      if (i == (str.length - 1)) ret = ret + str[i];
		      else ret = ret + str[i] + deli;
		    }
		    return ret;
		  }

		  public static String[] addStringArray(String[] data, String s) {
		    if (s == null)return null;
		    if (data == null)return new String[] {
		        s};

		    String[] tmp = new String[data.length + 1];

		    System.arraycopy(data, 0, tmp, 0, data.length);
		    tmp[data.length] = s;

		    return tmp;
		  }		
		  
		  public static String[] addStringArrayToArray(String[] data, String[] s) {
			    if (s == null)return null;
			    if (data == null)return s;

			    String[] tmp = new String[data.length + s.length];

			    System.arraycopy(data, 0, tmp, 0, data.length);
			    System.arraycopy(s, 0, tmp, data.length, s.length);

			    return tmp;
			  }				  
		  
			public static byte[] addArray(byte[] data, byte[] s) {
				if(data == null) return s;

				byte[] tmp	=	new byte[data.length + s.length];

				System.arraycopy(data, 0,  tmp, 0, data.length);
				System.arraycopy(s, 0,  tmp, data.length, s.length);

				return tmp;
			}	
			


			public static byte[] addArray(byte[] data, byte s) {
				if(data == null) return data;

				byte[] tmp	=	new byte[data.length + 1];

				System.arraycopy(data, 0,  tmp, 0, data.length);
                tmp[data.length] = s;

				return tmp;
			}				  
			
		  public static String ToUS8859(String str) {
			    try {
			      if (str == null && str.equals(""))return "";
//							return str;
			      return new String(str.getBytes("KSC5601"), "8859_1");
			    }
			    catch (UnsupportedEncodingException e) {
			      return null;
			    }
			  }

		public static String ToKSC5601(String str) {
			    try {
			      if (str == null && str.equals(""))return "";
//							return str;
			      return new String(str.getBytes("8859_1"), "KSC5601");
			    }
			    catch (UnsupportedEncodingException e) {
			      return null;
			    }
		 }
			  
	    public static String replace(String source, String target, String wantWord) {
		    if (source == null || target == null || wantWord == null ||
		        target.length() == 0)return source;

		    StringBuffer result = new StringBuffer(source.length());

		    int idx1 = 0, idx2 = 0;
		    while ( (idx2 = source.indexOf(target, idx1)) >= 0) {
		      result.append(source.substring(idx1, idx2));
		      result.append(wantWord);
		      idx1 = idx2 + target.length();
		    }

		    result.append(source.substring(idx1));
		    return result.toString();
		}		

	    public static byte[] replace(byte[] source, String target, String wantWord) {
		    if (source == null || target == null || wantWord == null ||
		        target.length() == 0)return source;
		    
		    String data = source.toString();

		    StringBuffer result = new StringBuffer(data.length());

		    int idx1 = 0, idx2 = 0;
		    while ( (idx2 = data.indexOf(target, idx1)) >= 0) {
		      result.append(data.substring(idx1, idx2));
		      result.append(wantWord);
		      idx1 = idx2 + target.length();
		    }

		    result.append(data.substring(idx1));
		    return result.toString().getBytes();
        }		
	    
	    public static byte[] datacut(byte [] buf ) {
	    	int i = 0;
	    	for(i = 0  ; i < buf.length ; ++i ){
	    		if(buf[i] == 0x2D) {
                    break ;
	    		}
	    	}
	    	byte[]	sb = new byte[i];
	    	for(i = 0  ; i < sb.length ; ++i ){
                   sb[i] = buf[i];   
    		}
	    	
	    	return sb;
	    }
	    
		public static byte[] int2byte(int i)
		{
			byte[] buf = new byte[4];
			buf[0] = (byte)(i >>> 24);
			buf[1] = (byte)(i >>> 16 & 0xff);
			buf[2] = (byte)(i >>> 8 & 0xff);
			buf[3] = (byte)i;	
			return buf;
	   }    	
		
	    public static byte[] short2byte(short s)
	    {
	        byte dest[] = new byte[2];
	        dest[1] = (byte)(s & 0xff);
	        dest[0] = (byte)(s >>> 8 & 0xff);
	        return dest;
	    }
	    	    	
	    public static int byte2Int(byte[] src)
	    {
	        int s1 = src[0] & 0xFF;
	        int s2 = src[1] & 0xFF;
	        int s3 = src[2] & 0xFF;
	        int s4 = src[3] & 0xFF;
	    
	        return ((s1 << 24) + (s2 << 16) + (s3 << 8) + (s4 << 0));
	    }

	 public static byte[] long2Bytes(long l)
			    {
			        byte[] buf = new byte[8];
			        buf[0] = (byte)( (l >>> 56) & 0xFF );
			        buf[1] = (byte)( (l >>> 48) & 0xFF );
			        buf[2] = (byte)( (l >>> 40) & 0xFF );
			        buf[3] = (byte)( (l >>> 32) & 0xFF );   
			        buf[4] = (byte)( (l >>> 24) & 0xFF );
			        buf[5] = (byte)( (l >>> 16) & 0xFF );
			        buf[6] = (byte)( (l >>>  8) & 0xFF );
			        buf[7] = (byte)( (l >>>  0) & 0xFF );
			    
			        return buf;
			    }
	 

	  public static void Debug(String s, String s1) {
		    if (!debug_mode)
		      return;
		 //   printf(s + " == " + s1);
	  }		  

	  public static void Debug(String s, Throwable e) {
		    if (!debug_mode)
		      return;
		//     printf(s+ ": ==> "+e.getStackTrace());
	  }		  
	  
	  public static void Debug(Exception e) {
          e.printStackTrace();		  
	  }

	    
	    public static String byteTrim(byte b[], int len)
	    {
	        byte nb[] = (byte[])null;
	        if(b == null || b.length == 0)
	        {
	            nb = new byte[len];
	            for(int i = 0; i < len; i++)
	                b[i] = 32;

	        } else
	        {
	            int oldLen = b.length;
	            if(len <= oldLen)
	            {
	                nb = new byte[len];
	                for(int i = 0; i < len; i++)
	                    nb[i] = b[i];

	            } else
	            {
	                byte l[] = new byte[len - oldLen];
	                for(int i = 0; i < len - oldLen; i++)
	                    l[i] = 32;

	                nb = byteAppend(b, l);
	            }
	        }
	        return new String(nb);
	    }

	    public static byte[] byteTrim(String str, int len)
	    {
	        byte b[] = str.getBytes();
	        byte nb[] = byteTrim(b, len).getBytes();
	        if(nb.length == len)
	            return nb;
	        byte k[] = new byte[len];
	        for(int i = 0; i < len; i++)
	            k[i] = nb[i];

	        return k;
	    }	
	    
	    public static byte[] byteAppend(byte b[], byte appendByte[])
	    {
	        if(b == null && appendByte == null)
	            return new byte[0];
	        if(b == null)
	            return appendByte;
	        if(appendByte == null)
	            return b;
	        if(b == null && appendByte == null)
	            return new byte[0];
	        int len = b.length + appendByte.length;
	        byte nb[] = new byte[len];
	        int i;
	        for(i = 0; i < b.length; i++)
	            nb[i] = b[i];

	        for(int j = 0; j < appendByte.length; j++)
	        {
	            nb[i] = appendByte[j];
	            i++;
	        }

	        return nb;
	    }	
	    
		public static String[] delStringArray(String [] data, String str) {
			String[] tmp	=	null;
			for(int i=0; i < data.length; i++) {
				if(str.equals(data[i])) continue;
				tmp	=	tools.addStringArray(tmp, data[i]);
			}
			if(tmp == null) tmp	= new String[]{};
			return tmp;
		}
		
		public static int unsign(byte arg) {
			int value	=	(int)arg;
			return value < 0 ? 0x100 + value : value;
		}
		
	    public static final double getdouble(byte src[], int offset)
	    {
	        return Double.longBitsToDouble(getlong(src, offset));
	    }		
	    
	    public static final long getlong(byte src[], int offset)
	    {
	        return 
	            (long)getint(src, offset) << 32 | 
	            (long)getint(src, offset + 4) & 0xffffffffL;
	    }

	    public static final int getint(byte src[], int offset)
	    {
	        return 
	            (src[offset] & 0xff) << 24 | 
	            (src[offset + 1] & 0xff) << 16 | 
	            (src[offset + 2] & 0xff) << 8 | 
	            src[offset + 3] & 0xff;
	    }	    
		
	      public static final byte[] double2byte(double d)
	      {
	          byte dest[] = new byte[8];
	          return setdouble(dest,0,d);
	      }	    
	      
	      public static final byte[] setdouble(byte dest[], int offset, double d)
	      {
	          return setlong(dest, offset, Double.doubleToLongBits(d));
	      }

	      public static final byte[] setlong(byte dest[], int offset, long l)
	      {
	          setint(dest, offset, (int)(l >>> 32));
	          setint(dest, offset + 4, (int)(l & 0xffffffffL));
	          return dest;
	      }	  

	      public static final byte[] setint(byte dest[], int offset, int i)
	      {
	          dest[offset] = (byte)(i >>> 24 & 0xff);
	          dest[offset + 1] = (byte)(i >>> 16 & 0xff);
	          dest[offset + 2] = (byte)(i >>> 8 & 0xff);
	          dest[offset + 3] = (byte)(i & 0xff);
	          return dest;
	      }	  
	      
	  	public static String reMakeHtml(String url) {
			String target	=	url.trim();

			int idx	=	target.indexOf(' ');
			if(idx != -1) {
				target	=	target.substring(0, idx);
			}

			return target;
		}	      
	      

    public static int  getRandem(int start, int end ){
  	        return (int) (Math.random() * (end-start)) + 1;
    }
    
 
    
    public static int Random(int start, int end){
    	return (int) (Math.random() * ( end-start)) + start;
    }
    
    public static int StartFromZero_Random(int end){
    	return (int) (Math.random() * end );
    }
    
    //=============================================//
    public static int random(int end_number){ 	
     	return (int) ((Math.random() * end_number))+1;
     }
    
    public static int notrandom(int start, int end, int not1, int not2){
   	 while(true){
   		 int result = (int) (Math.random() * ( end-start)) + start;
   		 if( result != not1 && result != not2){
   			 return result;
   		 }
   	 }
    }
    
  
    public static void saveObjectArray(String filename, Object[] theObjectAr,  Context ctx) {
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(int2byte(theObjectAr.length));
			Log.d("debud","theObjectAr.length=>"+ theObjectAr.length);
            for(int i = 0 ; i < theObjectAr.length; ++i) {
            	byte[] buf = toByteArray(theObjectAr[i]);
            	fos.write(int2byte(buf.length));
            	fos.write(buf); 
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        } finally {
        	try { if(fos!=null) fos.close(); }   catch (Exception e) {};
        }
    }
    
    public static Object[] readObjectArray(String filename,  Context ctx) {
        FileInputStream oos;
        byte[] buf = new byte[4];
        byte[] _buf;
        Object[] o ;
        try {
        	oos = ctx.openFileInput(filename);
        	if(oos !=null) {
            oos.read(buf);
            int cnt = byte2Int(buf);
            o = new Object[cnt];
            for(int i = 0; i < cnt ; ++i ) {
            	oos.read(buf);
            	_buf = new byte[byte2Int(buf)];
            	oos.read(_buf);
            	o[i] = toObject(_buf);
            }
            oos.close();
            return o;
        	}
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }      
	    
    public static void saveObject(String filename, Object theObjectAr,  Context ctx) {
        FileOutputStream fos;
        try {
            fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(theObjectAr); 
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }	     

    public static Object readObject(String filename, Context ctx) {
        FileInputStream fos;
        File f  = new File(filename);
        if(!f.exists()) return null;
        try {
            fos = ctx.openFileInput(filename);
            if(fos == null)  return null;        
            ObjectInputStream oos = new ObjectInputStream(fos);
            Object o = oos.readObject(); 
            oos.close();
            return o;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }        
        return null;        
    }	     

    
    public static byte[] toByteArray (Object obj)
    {
      byte[] bytes = null;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try {
        ObjectOutputStream oos = new ObjectOutputStream(bos); 
        oos.writeObject(obj);
        oos.flush(); 
        oos.close(); 
        bos.close();
        bytes = bos.toByteArray ();
      }
      catch (IOException ex) {
    	  ex.printStackTrace();
        //TODO: Handle the exception
      }
      return bytes;
    }
        
    public static Object toObject (byte[] bytes)
    {
      Object obj = null;
      try {
        ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
        ObjectInputStream ois = new ObjectInputStream (bis);
        obj = ois.readObject();
      }
      catch (IOException ex) {
        //TODO: Handle the exception
      }
      catch (ClassNotFoundException ex) {
        //TODO: Handle the exception
      }
      return obj;
    }    
    

	  // convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
    
    public static  Map<String, String> getMapResult(String query) {
    	Map<String, String> params = new HashMap<String, String>();
    	String deli = "&";
    	for (String param : query.split(deli)) {
    		DwStringTokenizer dst = new DwStringTokenizer(param, "=");
    		String key = dst.nextToken();
    		String value = dst.nextToken();
    		
    		String values = (String)params.get(key);
    		if (values == null) {
   			   params.put(key, value);
    		}    		
    	}
    	return params;
    }
	
	
	public static int getMinDay(){
    	  Date today = new Date ( );
    	  Calendar cal = Calendar.getInstance ( );
    	  cal.setTime ( today );// ?�늘�??�정.
    	  Calendar cal2 = Calendar.getInstance ( );
    	  cal2.set ( 2013, 8, 12 ); // 기�??�로 ?�정. month??경우 ?�당?�수-1???�줍?�다.
    	  int count = 0;
    	  while ( !cal2.after ( cal ) )
    	  {
	    	  count++;
	    	  cal2.add ( Calendar.DATE, 1 );
    	  }
          return count;   	  
     }

	public static boolean CheckNumber(String str){
		char check;

		if(str.equals(""))
		{
			//문자열이 공백인지 확인
			return false;
		}

		for(int i = 0; i<str.length(); i++){
			check = str.charAt(i);
			if( check < 48 || check > 58)
			{
				//해당 char값이 숫자가 아닐 경우
				return false;
			}

		}
		return true;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args)  {
	}

}

