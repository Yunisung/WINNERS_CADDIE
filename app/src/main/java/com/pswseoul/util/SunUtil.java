package com.pswseoul.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SunUtil
{


  public static  String LF = "\n";
  public static  String DLF = "\n\n";
  public static  String Line = "--------------------------------" ;
  public static  String SPACE = "                                                      " ;
  public static  int substringlen = 19;
  // 
  public static byte[] getBytesFromFile(File file) throws IOException
  {
    InputStream is = new FileInputStream(file);

    // Get the size of the file
    long length = file.length();

    if (length > Integer.MAX_VALUE)
    {
      // File is too large
    }

    byte[] bytes = new byte[(int) length];

    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
    {
      offset += numRead;
    }

    if (offset < bytes.length)
    {
      throw new IOException("Could not completely read file " + file.getName());
    }

    is.close();
    return bytes;
  }
  
  // app
  public static void setAppPreferences(Activity context, String key, String value)
  {
    SharedPreferences pref = null;
    pref = context.getSharedPreferences("FacebookCon", 0);
    SharedPreferences.Editor prefEditor = pref.edit();
    prefEditor.putString(key, value);

    prefEditor.commit();
  }
  
  //
  public static String getAppPreferences(Activity context, String key)
  {
    String returnValue = null;
    
    SharedPreferences pref = null;
    pref = context.getSharedPreferences("FacebookCon", 0);
    
    returnValue = pref.getString(key, "");
    
    return returnValue;
  }
  
  // app
  public static String getAppPreferences(Context context, String key)
  {
    String returnValue = null;
    
    SharedPreferences pref = null;
    pref = context.getSharedPreferences("FacebookCon", 0);
    
    returnValue = pref.getString(key, "");
    
    return returnValue;
  }
  
  public static JSONObject parseJson(String response)
          throws JSONException {
        JSONObject json = new JSONObject(response);
       return json;
  }

  private void saveMap(Context context, String key, Map<String,Boolean> inputMap){
    SharedPreferences pSharedPref = context.getSharedPreferences(key, Context.MODE_PRIVATE);
    if (pSharedPref != null){
      JSONObject jsonObject = new JSONObject(inputMap);
      String jsonString = jsonObject.toString();
      SharedPreferences.Editor editor = pSharedPref.edit();
      editor.remove(key).commit();
      editor.putString(key, jsonString);
      editor.commit();
    }
  }

  private Map<String,Boolean> loadMap(Context context, String spkey){
    Map<String,Boolean> outputMap = new HashMap<>();
    SharedPreferences pSharedPref = context.getSharedPreferences(spkey, Context.MODE_PRIVATE);
    try{
      if (pSharedPref != null){
        String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<String> keysItr = jsonObject.keys();
        while(keysItr.hasNext()) {
          String key = keysItr.next();
          Boolean value = (Boolean) jsonObject.get(key);
          outputMap.put(key, value);
        }
      }
    }catch(Exception e){
      e.printStackTrace();
    }
    return outputMap;
  }


}