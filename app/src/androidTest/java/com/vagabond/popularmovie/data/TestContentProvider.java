package com.vagabond.popularmovie.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.vagabond.popularmovie.MovieProvider;
import com.vagabond.popularmovie.data.MovieContract.MovieEntry;

/**
 * Created by HoaNV on 7/26/16.
 */
public class TestContentProvider extends AndroidTestCase {
    private static final String LOG_TAG = TestContentProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI, null, null);

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        assertEquals("Error: Records not deleted from Movie Table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecordsFromDB() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    private void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieProvider.class.getName());

        try {
            ProviderInfo pf = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: WeatherProvider registered with authority: " + pf.authority +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY, pf.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(), false);
        }
    }

    public void testGetType() {
        // content://com.vagabond.popularmovie.app/movie/
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.vagabond.popularmovie.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE", MovieEntry.CONTENT_TYPE, type);

        long testMovieId = 123456;
        // content://com.vagabond.popularmovie.app/movie/123456
        type = mContext.getContentResolver().getType(MovieEntry.buildMovieWithMovieId(testMovieId));
        // vnd.android.cursor.dir/com.vagabond.popularmovie.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with movie id should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);
    }

    public void testBasicMovieQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();
        long tableRowID = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Unable to Insert MovieEntry into the Database", tableRowID != -1);

        db.close();

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, testValues);
    }
}
