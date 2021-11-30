package com.mtouch.ksnet.dpt.design.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;

import com.mtouch.caddie.data.Schedual;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;

import java.util.Map;
import java.util.Set;

public class SharedPreferenceUtil {

    public static final String KEY = "Variable";
    public static final String SCHEDUAL = "schedual";

    public static void removeSchedual(Context context, Schedual schedual){
        if(schedual==null) return;
        SharedPreferences pref = context.getSharedPreferences(SCHEDUAL, Context.MODE_PRIVATE);
        pref.edit().remove(schedual.getDay()+"").commit();
    }
    public static Map getAllSchedual(Context context){
        SharedPreferences pref = context.getSharedPreferences(SCHEDUAL, Context.MODE_PRIVATE);
        return pref.getAll();
    }
    public static String getSchedual(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(SCHEDUAL, Context.MODE_PRIVATE);
        return pref.getString(key,"");
    }

    public static void putSchedual(Context context, String day, Schedual schedual){
        SharedPreferences pref = context.getSharedPreferences(SCHEDUAL, Context.MODE_PRIVATE);
        pref.edit().putString(day,schedual.toString()).commit();
    }

    public static String getVersion(Context context){
        SharedPreferences pref = context.getSharedPreferences(KEY,Context.MODE_PRIVATE);
        return pref.getString(Constants.KEY_VERSION,"0");
    }
    public static void putVersion(Context context){
        SharedPreferences pref = context.getSharedPreferences(KEY,Context.MODE_PRIVATE);
        pref.edit().putString(Constants.KEY_VERSION, BuildConfig.VERSION_NAME).commit();
    }

    public static String getData(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getString(key,"");
    }
    public static String getData(Context context, String key, String defaultValue){
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return pref.getString(key,defaultValue);
    }

    public static void putData(Context context, String key, String value){
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        pref.edit().putString(key,value).commit();
    }

    public static void putData(Context context, Map<String, ?> allAppPreference) {
        if(allAppPreference==null) return;

        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        for(Map.Entry<String, ?> entry : allAppPreference.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof Integer){
                pref.edit().putInt(key, (Integer) value).commit();
            }else if(value instanceof Boolean){
                pref.edit().putBoolean(key, (Boolean) value).commit();
            }else if(value instanceof Long){
                pref.edit().putLong(key, (Long) value).commit();
            }else if(value instanceof Float){
                pref.edit().putFloat(key, (Float) value).commit();
            }else if(value instanceof String){
                pref.edit().putString(key, (String) value).commit();
            }else if(value instanceof Set){
                pref.edit().putStringSet(key,(Set<String>) value).commit();
            }
        }
    }

    public static void removeData(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        pref.edit().remove(key).commit();
    }

    public static void clearData(Context context){
        SharedPreferences pref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        pref.edit().clear().commit();
        SharedPreferences preff = context.getSharedPreferences(SCHEDUAL, Context.MODE_PRIVATE);
        preff.edit().clear().commit();
    }
}
