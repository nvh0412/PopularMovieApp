package com.vagabond.popularmovie;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.vagabond.popularmovie.model.Constant;
import com.vagabond.popularmovie.model.MovieData;
import com.vagabond.popularmovie.services.MovieDBService;
import com.vagabond.popularmovie.services.WebService;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<>());

        GridView mGridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent mIntent = new Intent(getActivity(), DetailActivity.class);
            mIntent.putExtra(Constant.EXTRA_MOVIEID, mMovieAdapter.getItemId(position));
            startActivity(mIntent);
        });

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
        updateMovies();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            updateMovies();
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
                .map(MovieData::getResults)
                .subscribe(
                        results -> mMovieAdapter.addAll(results),
                        this::handleError
                );
    }

    private void handleError(Throwable e) {
        Log.e(LOG_TAG, "Can't fetch movie list", e);
        Toast.makeText(getActivity(), "Something went wrong, please check your internet connection and try again!", Toast.LENGTH_LONG).show();
    }
}
