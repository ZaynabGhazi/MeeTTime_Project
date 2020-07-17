package com.zaynab.meettime.support;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Logger {
    public static void notify(String TAG, String msg, Context context, Throwable e) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        if (e != null)
            Log.e(TAG, msg, e);
        else Log.i(TAG, msg);
    }
}
