package com.pswseoul.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonUtil {
	
	public static String toString(Object obj){
		if(obj == null){
			return "";
		}else{
			if(obj instanceof String){
				return (String)obj;
			}else if(obj instanceof Integer){
				Integer i = (Integer)obj;
				return i.toString();
			}else if(obj instanceof Long){
				Long l = (Long)obj;
				return l.toString();
			}else if(obj instanceof Double){
				Double d = (Double)obj;
				return d.toString();
			}else if(obj instanceof Byte){
				Byte b = (Byte)obj;
				return b.toString();
			}else if(obj instanceof byte[]){
				byte[] b = (byte[])obj;
				return new String(b);
			}else if(obj instanceof Character){
				Character c = (Character)obj;
				return String.valueOf(c);
			}else if(obj instanceof char[]){
				char[] c = (char[])obj;
				return String.valueOf(c);
			}else if(obj instanceof BigInteger){
				BigInteger b =  (BigInteger)obj;
				return b.toString();
			}else if(obj instanceof BigDecimal){
				BigDecimal b =  (BigDecimal)obj;
				return b.toString();
			}else if(obj instanceof Boolean){
				Boolean b =  (Boolean)obj;
				return b.toString();
			}else if(obj instanceof Timestamp){
				return timestampToString((Timestamp)obj,"yyyy/MM/dd HH:mm:ss.S");
			}else if(obj instanceof String[]){
				String[] s = (String[])obj;
				StringBuilder ret = new StringBuilder();
				for(int i=0;i<s.length;i++){
					ret.append(CommonUtil.nToB(s[i]));
					if(i+1 != s.length){
						ret.append(",");
					}
				}
				return ret.toString();
			}else{
				return "";
			}
			
			
		}
	}
	
	/**
	 * Object 를 int format 으로 변환하여 반환한다.
	 * @param obj
	 * @return
	 */
	public static int parseInt(Object obj){
		if(obj == null){
			return 0;
		}else{
			try{
				if(obj instanceof String){
					String str = (String)obj;
					return Integer.parseInt(str);
				}else if(obj instanceof Integer){
					Integer i = (Integer)obj;
					return i.intValue();
				}else if(obj instanceof Long){
					Long l = (Long)obj;
					return l.intValue();
				}else if(obj instanceof Double){
					Double d = (Double)obj;
					return d.intValue();
				}else if(obj instanceof Byte){
					Byte b = (Byte)obj;
					return b.intValue();
				}else if(obj instanceof byte[]){
					byte[] b = (byte[])obj;
					String str = new String(b);
					return Integer.parseInt(str);
				}else{
					return 0;
				}
			}catch(Exception e){
				
			}
			return 0;
		}
	}
	
	/**
	 * Object 를 long format 으로 변환하여 반환한다.
	 * @param obj
	 * @return
	 */
	public static long parseLong(Object obj){
		if(obj == null){
			return 0;
		}else{
			try{
				if(obj instanceof String){
					String str = (String)obj;
					return Long.parseLong(str);
				}else if(obj instanceof Integer){
					Integer i = (Integer)obj;
					return i.longValue();
				}else if(obj instanceof Long){
					Long l = (Long)obj;
					return l.longValue();
				}else if(obj instanceof Double){
					Double d = (Double)obj;
					return d.longValue();
				}else if(obj instanceof Byte){
					Byte b = (Byte)obj;
					return b.longValue();
				}else if(obj instanceof byte[]){
					byte[] b = (byte[])obj;
					String str = new String(b);
					return Long.parseLong(str);
				}else{
					return 0;
				}
			}catch(Exception e){
				
			}
			return 0;
		}
	}
	
	/**
	 * Object 를 double format 으로 변환하여 반환한다.
	 * @param obj
	 * @return
	 */
	public static double parseDouble(Object obj){
		if(obj == null){
			return 0;
		}else{
			try{
				if(obj instanceof String){
					String str = (String)obj;
					return Double.parseDouble(str);
				}else if(obj instanceof Integer){
					Integer i = (Integer)obj;
					return i.doubleValue();
				}else if(obj instanceof Long){
					Long l = (Long)obj;
					return l.doubleValue();
				}else if(obj instanceof Double){
					Double d = (Double)obj;
					return d;
				}else if(obj instanceof Byte){
					Byte b = (Byte)obj;
					return b.doubleValue();
				}else if(obj instanceof byte[]){
					byte[] b = (byte[])obj;
					String str = new String(b);
					return Double.parseDouble(str);
				}else{
					return 0;
				}
			}catch(Exception e){
				
			}
			return 0;
		}
	}
		
	public static String timestampToString(Timestamp ts) {
		return timestampToString(ts,"yyyy-MM-dd HH:mm:ss");
	}
	
	public static String timestampToString(Timestamp ts,String format) {
		if (ts == null) {
			ts = new Timestamp(new Date().getTime());
		}
		return getDateString(format, ts.getTime(),null);
	}
	
	public static String getDateString(String format,Date date,Locale locale){
		if(locale == null){
			locale = new Locale("KOREAN","KOREA");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format,locale);
		return sdf.format(date);
	}
	
	public static String getDateString(String format,long time,Locale locale){
		return getDateString(format, new Date(time), locale);
	}
	
	public static String nToB(String str){
		if(isNull(str)){
			return "";
		}else{
			return str;
		}
	}
		
	public static boolean isNull(String str) {
		if (str == null)
			return true;
		else
			return false;
	}
	
	public static boolean isNullOrSpace(String str){
		if(isNull(str)){
			return true;
		}
		if(str.trim().length() == 0){
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(String str){
		return isNullOrSpace(str);
	}
	
	
}
