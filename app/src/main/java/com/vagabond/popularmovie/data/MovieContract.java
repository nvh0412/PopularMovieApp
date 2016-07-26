package com.vagabond.popularmovie.data;

import android.provider.BaseColumns;

/**
 * Created by HoaNV on 7/25/16.
 */
public class MovieContract {
    public static final class MovieEntry implements BaseColumns {
        public static final String _ID = "id";
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_RELEASE_DATE = "release_date";
    }
}
