package com.vagabond.popularmovie;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vagabond.popularmovie.data.MovieContract;
import com.vagabond.popularmovie.sync.MovieSyncAdapter;

import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 1;
    private MovieAdapter mMovieAdapter;
    private String mOrderType;
    private boolean isFavourite;
    static final int COL_MOVIE_ID = 1;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if (args != null) {
            mOrderType = args.getString("ORDER_TYPE");
            isFavourite = args.getBoolean("FILTER_TYPE");
        }
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

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (null != cursor && cursor.moveToPosition(position)) {
                    ((Callback)getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieWithMovieId(cursor.getLong(COL_MOVIE_ID)));
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            updateMovie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (isFavourite) {
            SharedPreferences sp = getActivity().getSharedPreferences("FAV_PREFS", 0);
            Set<String> keySet = sp.getAll().keySet();
            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI, null,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " IN (" + makeQueryMovieID(keySet) + ")",
                    null,
                    null);
        } else {
            String order = getString(R.string.pref_order_popular).equals(mOrderType) ? "popularity" : "vote_average";
            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI, null, null, null, order + " DESC");
        }
    }

    private void updateMovie() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    private String makeQueryMovieID(Set<String> keySet) {
        StringBuilder str = new StringBuilder();
        for (String key : keySet) {
            str.append(key);
            str.append(",");
        }
        if (str.length() > 0) {
            str.deleteCharAt(str.length() - 1);
        }
        return str.toString();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    public interface Callback {
        void onItemSelected(Uri movieIdUri);
    }
}
