package com.vagabond.popularmovie.data;

import android.test.AndroidTestCase;

/**
 * Created by HoaNV on 7/25/16.
 */
public class TestDB extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        deleteTheDatabase();
    }

    public void testCreateDb() {

    }

    public void testMovieTable() {

    }
}
