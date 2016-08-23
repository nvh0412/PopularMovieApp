package com.vagabond.popularmovie.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.vagabond.popularmovie.BuildConfig;
import com.vagabond.popularmovie.R;
import com.vagabond.popularmovie.data.MovieContract;
import com.vagabond.popularmovie.model.Movie;
import com.vagabond.popularmovie.model.MovieData;
import com.vagabond.popularmovie.services.MovieDBService;
import com.vagabond.popularmovie.services.WebService;

import java.util.List;
import java.util.Vector;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by HoaNV on 8/23/16.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL = 30;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Start synchronizing");
        MovieDBService movieDBService = WebService.getMovieDBService();
        movieDBService.getMovieData(getContext().getString(R.string.pref_order_popular), BuildConfig.MOVIE_DB_API_KEY)
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
                                Log.d(LOG_TAG, "Sync popular movie");
                                movieListHanlder(movieList);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                Log.e(LOG_TAG, "Error: Can't sync data from API", e);
                            }
                        }
                );

        movieDBService.getMovieData(getContext().getString(R.string.pref_order_toprated), BuildConfig.MOVIE_DB_API_KEY)
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
                                Log.d(LOG_TAG, "Sync top rated movie");
                                movieListHanlder(movieList);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable e) {
                                Log.e(LOG_TAG, "Error: Can't sync data from API", e);
                            }
                        }
                );
    }

    private void movieListHanlder(List<Movie> movieList) {
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
            inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "Sync Data complete. " + inserted + " inserted.");
    }

    public static Account getSyncAccount(Context context) {
        Log.d(LOG_TAG, "MovieSyncAdapter - getSyncAccount: " + context);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
