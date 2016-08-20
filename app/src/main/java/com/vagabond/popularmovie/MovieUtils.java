package com.vagabond.popularmovie;

import android.util.Log;

/**
 * Created by HoaNV on 7/20/16.
 */
public class MovieUtils {
    public static int getReadableReleaseYear(String year) {
        Log.d("MovieUtils", "Year: " + year);
        if (year != null && !year.isEmpty()) {
            return Integer.parseInt(year.split("-")[0]);
        } else {
            return 0;
        }
    }
}
