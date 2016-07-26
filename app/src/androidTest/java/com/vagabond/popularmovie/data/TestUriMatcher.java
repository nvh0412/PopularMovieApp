package com.vagabond.popularmovie.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.vagabond.popularmovie.MovieProvider;

/**
 * Created by HoaNV on 7/26/16.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 209112L;
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_MOVIE_ID_DIR = MovieContract.MovieEntry.buildMovieWithMovieId(TEST_MOVIE_ID);

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_MOVIE_ID_DIR), MovieProvider.MOVIE_WITH_MOVIE_ID);
    }
}
