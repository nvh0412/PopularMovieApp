package com.vagabond.popularmovie;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vagabond.popularmovie.model.Movie;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {


    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    private void updateMovies() {
        SharedPreferences sharePrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        new FetchMovieAsyncTask().execute();
    }

    public class FetchMovieAsyncTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected Movie[] doInBackground(String... params) {
            return new Movie[0];
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);
        }
    }

}
