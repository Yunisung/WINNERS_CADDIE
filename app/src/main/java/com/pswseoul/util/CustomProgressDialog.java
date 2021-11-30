package com.pswseoul.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by parksuwon on 2017-12-12.
 */

public class CustomProgressDialog  extends ProgressDialog {
    Context mContext = null;

    public CustomProgressDialog(Context context, String msg) {
        super(context);

        this.mContext = context;
        this.setMessage(msg);
        this.setCanceledOnTouchOutside(false);
        this.setIndeterminate(true);
        this.setInverseBackgroundForced(true);
    }

    public CustomProgressDialog(Context context){
        this(context, null);
    }
}
