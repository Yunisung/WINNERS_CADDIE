package com.bkwinners.ksnet.dpt;

import android.content.Context;
import android.widget.Toast;

public class Toasty {

    public static Toast error(Context context,String text, int time){
        return Toast.makeText(context,text,time);
    }
    public static Toast info(Context context,String text, int time){
        return Toast.makeText(context,text,time);
    }

    public static Toast error(Context context,String text, int time, boolean ss){
        return Toast.makeText(context,text,time);
    }
    public static Toast info(Context context,String text, int time, boolean ss){
        return Toast.makeText(context,text,time);
    }

    public static Toast success(Context context,String text, int time){
        return Toast.makeText(context,text,time);
    }
    public static Toast success(Context context,String text, int time, boolean ss){
        return Toast.makeText(context,text,time);
    }



}
