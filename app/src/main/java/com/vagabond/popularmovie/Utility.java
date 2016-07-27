package com.vagabond.popularmovie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by HoaNV on 7/28/16.
 */
public class Utility {
    public static String getPreferredOrderType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_order_key),
                "popular");
    }
}
