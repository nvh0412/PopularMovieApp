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
            assertEquals("Value " + expectedValue + " did not match with the expected value" + expectedValue + ". " + error,
                    expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues() {
        ContentValues movieContentValues = new ContentValues();
        return movieContentValues;
    }
}
