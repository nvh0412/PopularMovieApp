package com.vagabond.popularmovie;

/**
 * Created by HoaNV on 7/20/16.
 */
public class MovieUtils {
    public static int getReadableReleaseYear(String year) {
        if (year != null && !year.isEmpty()) {
            return Integer.parseInt(year.split("-")[0]);
        } else {
            return 0;
        }
    }
}
