package com.vagabond.popularmovie.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

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
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        // Have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("This means that the database has not been created correctly", c.moveToFirst());

        // Verify that tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while ( c.moveToNext() );

        assertTrue("Error: Your database was created without movie entry table",
            tableNameHashSet.isEmpty());

        // Do my table contain correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means that we are unable to query the database for table information",
            c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);

        int columnNameIndex = c.getColumnIndex("name");

        do {
            movieColumnHashSet.remove(c.getString(columnNameIndex));
        } while ( c.moveToNext() );

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
            movieColumnHashSet.isEmpty());

        db.close();
    }

    public void testMovieTable() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues cvMovieValue = TestUtilities.createMovieValues();

        // Insert ContentValues into database and get a row ID back
        long rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cvMovieValue);
        assertTrue(rowId != -1);

        // Query the database and receive a cursor back
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error: This means the database has not been created correctly", cursor.moveToFirst());

        TestUtilities.validateCursor("Error: The cursor did not match with content values", cursor, cvMovieValue);

        cursor.close();
        db.close();
    }
}
