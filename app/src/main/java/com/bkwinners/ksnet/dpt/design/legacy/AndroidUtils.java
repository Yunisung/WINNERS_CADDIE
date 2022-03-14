package com.bkwinners.ksnet.dpt.design.legacy;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AndroidUtils
{


    public static String LF = "\n";
    public static String DLF = "\n\n";
    public static String Line = "--------------------------------" ;
    public static String SPACE = "                                                      " ;
    public static  int substringlen = 19;
    public static String TAG  = "SunUtil";
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
        pref = context.getSharedPreferences("FacebookCon", Context.MODE_PRIVATE);

        returnValue = pref.getString(key, "");

        return returnValue;
    }

    public static void clearAppPreference(Context context){
        context.getSharedPreferences("FacebookCon", Context.MODE_PRIVATE).edit().clear().commit();
    }
    public static Map<String, ?> getAllAppPreference(Context context){
        return context.getSharedPreferences("FacebookCon", Context.MODE_PRIVATE).getAll();
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

    private void saveMap(Context context, String key, Map<String, Boolean> inputMap){
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

    private Map<String, Boolean> loadMap(Context context, String spkey){
        Map<String, Boolean> outputMap = new HashMap<>();
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public static void setListViewHeightBasedOnChildren(ListView listView, ScrollView scroller) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public static String getIntentData(Intent intent) {
        StringBuffer sb = new StringBuffer();
        sb.append("intent : \n");
        if(intent != null) {
            Bundle extra = intent.getExtras();

            Set<String> keys = extra.keySet();
            sb.append("keys.size() : " + keys.size());
            sb.append("\n");
            for (String key : extra.keySet()) {
                sb.append(key+ " : "+ extra.get(key).toString());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static String printHex(byte[] b) {
        System.out.print ("printHex :");
        for (int i = 0; i < b.length; ++i) {
            System.out.print (Integer.toHexString((b[i]&0xFF) | 0x100).substring(1,3) + " ");
            if (i % 16 == 15 || i == b.length - 1)
            {
                int j;
                System.out.println ("");
                int start = (i / 16) * 16;
                int end = (b.length < i + 1) ? b.length : (i + 1);
                for (j = start; j < end; ++j)
                    if (b[j] >= 32 && b[j] <= 126)
                        System.out.print ((char)b[j] );
                        // + " " + Character.toString((char)b[j])
                    else
                        System.out.print (".");
            }
        }
        System.out.println ();
        return "\r\n";
    }

    public static String printHex(byte[] b, Integer[] per) {
        System.out.print ("printHex :");
        int k = 0;
        for (int i = 0; i < b.length; ++i) {
            System.out.print (Integer.toHexString((b[i]&0xFF) | 0x100).substring(1,3) + " ");
//            if (i % 16 == 15 || i == b.length - 1)
            if (i == b.length - 1)
            {

                int j;
                System.out.println ("");
                int start = (i / 16) * 16;
                int end = (b.length < i + 1) ? b.length : (i + 1);
                for (j = start; j < end; ++j)
                    if (b[j] >= 32 && b[j] <= 126)
                        System.out.print ((char)b[j]);
                    else
                        System.out.print (".");
            }
        }
        System.out.println ();
        return "\r\n";
    }

    public static String printHex(byte b) {
        System.out.print ("printHex :");
        System.out.print (b);
        return "\r\n";
    }

    public static String getPhoneNumber(Context cxt) {
        return "00000000000";
    }

    public static void prnIntentData (Intent data) {

        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    Log.d(TAG, String.format("%s %s (%s)", key,
                            value.toString(), value.getClass().getName()));
                }
            }
        } else {
            Log.d(TAG,  "Intent  is Null" );
        }
    }


    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

}