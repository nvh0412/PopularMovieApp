package com.vagabond.popularmovie.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.vagabond.popularmovie.BuildConfig;
import com.vagabond.popularmovie.data.MovieContract;
import com.vagabond.popularmovie.model.Movie;
import com.vagabond.popularmovie.model.MovieData;

import java.util.List;
import java.util.Vector;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by HoaNV on 8/23/16.
 */
public class MovieService extends IntentService {
    public static final String MOVIE_ORDER_EXTRA = "ORDER_TYPE";
    private static final String LOG_TAG = MovieService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MovieService() {
        super("MovieService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String orderType = intent.getStringExtra(MOVIE_ORDER_EXTRA);
        updateMovies(orderType);
    }

    private void updateMovies(String orderType) {
        MovieDBService movieDBService = WebService.getMovieDBService();
        movieDBService.getMovieData(orderType, BuildConfig.MOVIE_DB_API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<MovieData, List<Movie>>() {
                    @Override
                    public List<Movie> call(MovieData movieData) {
                        return movieData.getResults();
                    }
                })
                .subscribe(
                        new Action1<List<Movie>>() {
                            @Override
                            public void call(List<Movie> movieList) {
                                Vector<ContentValues> cvVector = new Vector<>(movieList.size());

                                for (Movie movie : movieList) {
                                    ContentValues cv = new ContentValues();
                                    cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                                    cv.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                                    cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
                                    cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                                    cv.put(MovieContract.MovieEntry.COLUMN_ADULT, movie.getAdult());
                                    cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
                                    cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
                                    cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
                                    cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
                                    cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                                    cv.put(MovieContract.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
                                    cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                                    cvVector.add(cv);
                                }

                                int inserted = 0;
                                if (movieList.size() > 0) {
                                    ContentValues[] cvArray = new ContentValues[cvVector.size()];
                                    cvVector.toArray(cvArray);
                                    inserted = getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                                }

                                Log.d(LOG_TAG, "Sync Data complete. " + inserted + " inserted.");
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                Log.e(LOG_TAG, "Error: Can't sync data from API", e);
                                handleError(e);
                            }
                        }
                );
    }

    private void handleError(Throwable e) {
        Log.e(LOG_TAG, "Can't fetch movie list", e);
        Toast.makeText(this, "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_LONG).show();
    }

    static public class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, MovieService.class);
            sendIntent.putExtra(MovieService.MOVIE_ORDER_EXTRA, intent.getStringExtra(MovieService.MOVIE_ORDER_EXTRA));
            context.startService(sendIntent);
        }
    }

}
