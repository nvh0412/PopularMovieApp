package com.vagabond.popularmovie;


import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.vagabond.popularmovie.data.MovieContract;
import com.vagabond.popularmovie.model.Movie;
import com.vagabond.popularmovie.model.MovieData;
import com.vagabond.popularmovie.services.MovieDBService;
import com.vagabond.popularmovie.services.WebService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 1;
    private MovieAdapter mMovieAdapter;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        GridView mGridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        mGridView.setAdapter(mMovieAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        SharedPreferences sharePrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String orderType = sharePrefs.getString(getString(R.string.pref_order_key), "popular");

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
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD");
                                try {
                                    cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, sdf.parse(movie.getReleaseDate()).getTime());
                                } catch (ParseException e) {
                                    Log.e(LOG_TAG, "Can't parse time", e);
                                }
                                cvVector.add(cv);
                            }

                            int inserted = 0;
                            if (movieList.size() > 0) {
                                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                                cvVector.toArray(cvArray);
                                inserted = getActivity().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
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
        Toast.makeText(getActivity(), "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String orderType = Utility.getPreferredOrderType(getActivity());
        String order = orderType.equals("popular") ? "popularity" : "vote_average";

        return new CursorLoader(getActivity(), MovieContract.MovieEntry.CONTENT_URI, null, null, null, order + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public void onOrderChange() {
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
}
