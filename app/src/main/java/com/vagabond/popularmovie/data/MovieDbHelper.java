package com.vagabond.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HoaNV on 7/25/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final StringBuilder SQL_CREATE_MOVIE_TABLE = new StringBuilder("CREATE TABLE ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.TABLE_NAME).append(" (");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_TITLE).append(" TEXT NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE).append(" TEXT NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_OVERVIEW).append(" TEXT NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_ADULT).append(" INTEGER NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_VOTE_COUNT).append(" INTEGER NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE).append(" REAL NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_POPULARITY).append(" REAL NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_RUNTIME).append(" INTEGER NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_RELEASE_DATE).append(" INTEGER NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_POSTER_PATH).append(" TEXT NOT NULL, ");
        SQL_CREATE_MOVIE_TABLE.append(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH).append(" TEXT NOT NULL )");

        db.execSQL(SQL_CREATE_MOVIE_TABLE.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
