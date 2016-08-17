package com.vagabond.popularmovie;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vagabond.popularmovie.data.MovieContract;
import com.vagabond.popularmovie.model.Constant;

/**
 * Created by HoaNV on 7/19/16.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String[] MOVIES_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_ADULT,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RUNTIME
    };
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_ORIGINAL_TITLE = 3;
    static final int COL_ADULT = 4;
    static final int COL_POSTER_PATH = 5;
    static final int COL_BACKDROP_PATH = 6;
    static final int COL_POPULARITY = 7;
    static final int COL_RELEASE_DATE = 8;
    static final int COL_VOTE_AVERAGE = 9;
    static final int COL_OVERVIEW = 10;
    static final int COL_RUNTIME = 11;
    private static final int DETAIL_LOADER = 0;
    public static final String DETAIL_URI = "DETAIL_URI";

    private Uri mUriData;
    private ImageView posterImageView;
    private TextView titleTV;
    private TextView releaseYearTV;
    private TextView durationTV;
    private TextView voteAverageTV;
    private TextView overviewTV;

    public DetailFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle argument = getArguments();
        if (null != argument) {
            mUriData = argument.getParcelable(DETAIL_URI);
        } else {
            mUriData = getActivity().getIntent().getData();
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        posterImageView = (ImageView) rootView.findViewById(R.id.posterImg);
        releaseYearTV = (TextView) rootView.findViewById(R.id.releaseYear);
        durationTV = (TextView) rootView.findViewById(R.id.runtime);
        voteAverageTV = (TextView) rootView.findViewById(R.id.voteAverage);
        overviewTV = (TextView) rootView.findViewById(R.id.overview);
        titleTV = (TextView) rootView.findViewById(R.id.title);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUriData != null) {
            return new CursorLoader(getActivity(), mUriData, MOVIES_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String posterPath = data.getString(COL_POSTER_PATH);
            Picasso.with(getActivity()).load(Constant.MOVIEDB_IMAGE_PATH + posterPath).into(posterImageView);

            String title = data.getString(COL_MOVIE_TITLE);
            titleTV.setText(title);

//            releaseYearTV.setText(String.valueOf(MovieUtils.getReadableReleaseYear(movieDetail.getReleaseDate())));

            Double voteAverage = data.getDouble(COL_VOTE_AVERAGE);
            voteAverageTV.setText(String.format("%.1f/10.0", voteAverage));

            int runtime = data.getInt(COL_RUNTIME);
            durationTV.setText(String.valueOf(runtime));

            String overview = data.getString(COL_OVERVIEW);
            overviewTV.setText(overview);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
