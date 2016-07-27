package com.vagabond.popularmovie.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

    public void testUpdateMovie() {
        ContentValues contentValues = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);
        long movieID = ContentUris.parseId(movieUri);

        assertTrue(movieID != -1);

        ContentValues updateValues = new ContentValues(contentValues);
        updateValues.put(MovieEntry._ID, movieID);
        updateValues.put(MovieEntry.COLUMN_TITLE, "Updated");

        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updateValues,
                MovieEntry._ID + " = ? ",
                new String[]{ String.valueOf(movieID)});

        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                MovieEntry._ID + " = " + movieID,
                null,
                null);

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.", cursor, updateValues);
        cursor.close();
    }

    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our Movie delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        movieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertMovieValues() {
        long currentTestDate = TestUtilities.TEST_DATE;
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate+= millisecondsInADay ) {
            ContentValues movieContentValues = new ContentValues();
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 209110 + i);
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Batman v Superman: Dawn of Justice");
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Fearing the actions of a god-like Super Hero left unchecked, " +
                    "Gotham City’s own formidable, forceful vigilante takes on Metropolis’s most revered, modern-day savior");
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Batman v Superman: Dawn of Justice");
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_ADULT, 0);
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/cGOPbv9wA5gEejkUN892JrveARt.jpg");
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, "/vsjBeMPZtyB7yNsYY56XYxifaQZ.jpg");
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 5.56);
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 2922);
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 43.33);
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, currentTestDate);
            movieContentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, 120);
            returnContentValues[i] = movieContentValues;
        }
        return returnContentValues;
    }

    public void testBulkInsert() {
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, bulkInsertContentValues);
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MovieEntry.COLUMN_RELEASE_DATE + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
