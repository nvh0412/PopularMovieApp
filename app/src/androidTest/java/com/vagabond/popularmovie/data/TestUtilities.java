package com.vagabond.popularmovie.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by HoaNV on 7/25/16.
 */
public class TestUtilities extends AndroidTestCase {
    static final long TEST_DATE = 1419033600L;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> value : valueSet) {
            String columnName = value.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column " + columnName + " not found, " + error, idx == -1);
            String expectedValue = value.getValue().toString();
            assertEquals("Value " + expectedValue + " did not match with the expected value " + expectedValue + ". " + error,
                    expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues() {
        ContentValues movieContentValues = new ContentValues();
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 209112);
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
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, TEST_DATE);
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, 120);
        return movieContentValues;
    }
}
