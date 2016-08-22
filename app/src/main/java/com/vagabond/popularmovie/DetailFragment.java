package com.vagabond.popularmovie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vagabond.popularmovie.data.MovieContract;
import com.vagabond.popularmovie.model.Constant;
import com.vagabond.popularmovie.model.Review;
import com.vagabond.popularmovie.model.ReviewData;
import com.vagabond.popularmovie.model.Trailer;
import com.vagabond.popularmovie.model.TrailerData;
import com.vagabond.popularmovie.services.MovieDBService;
import com.vagabond.popularmovie.services.WebService;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by HoaNV on 7/19/16.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FAV_PREFS = "FAV_PREFS";
    public static final String DETAIL_URI = "DETAIL_URI";
    private static final int DETAIL_LOADER = 0;

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
    static final int COL_POSTER_PATH = 5;
    static final int COL_BACKDROP_PATH = 6;
    static final int COL_RELEASE_DATE = 8;
    static final int COL_VOTE_AVERAGE = 9;
    static final int COL_OVERVIEW = 10;
    public static final String MOVIE_ID = "MOVIE_ID";

    private Uri mUriData;
    private ImageView posterImageView;
    private TextView titleTV;
    private TextView releaseYearTV;
    private TextView voteAverageTV;
    private TextView overviewTV;
    private ImageView backDropImageView;
    private boolean isTwoPanel;
    private LinearLayout reviewLayout;
    private LinearLayout trailerLayout;

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
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        reviewLayout = (LinearLayout) rootView.findViewById(R.id.review_list_layout);
        trailerLayout = (LinearLayout) rootView.findViewById(R.id.trailer_list_layout);

        Bundle argument = getArguments();
        if (null != argument) {
            mUriData = argument.getParcelable(DETAIL_URI);
            this.isTwoPanel = true;
            backDropImageView = (ImageView) rootView.findViewById(R.id.backdrop);
        } else {
            mUriData = getActivity().getIntent().getData();
            this.isTwoPanel = false;
            backDropImageView = (ImageView) getActivity().findViewById(R.id.backdrop);
        }
        posterImageView = (ImageView) rootView.findViewById(R.id.posterImg);
        releaseYearTV = (TextView) rootView.findViewById(R.id.releaseYear);
        voteAverageTV = (TextView) rootView.findViewById(R.id.voteAverage);
        overviewTV = (TextView) rootView.findViewById(R.id.overview);
        titleTV = (TextView) rootView.findViewById(R.id.title);
        Button addFavouriteBtn = (Button) rootView.findViewById(R.id.fav_btn);

        if (addFavouriteBtn != null) {
            addFavouriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences favPref = getActivity().getSharedPreferences(FAV_PREFS, 0);
                    favPref.edit().putBoolean(MovieContract.MovieEntry.getMovieIdFromUri(mUriData), true).apply();
                }
            });
        }

        fetchMovieReviews(MovieContract.MovieEntry.getMovieIdFromUri(mUriData), WebService.getMovieDBService());
        fetchMovieTrailers(MovieContract.MovieEntry.getMovieIdFromUri(mUriData), WebService.getMovieDBService());
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

            if (!this.isTwoPanel) {
                String title = data.getString(COL_MOVIE_TITLE);
                CollapsingToolbarLayout collapsingToolbar =
                        (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(title);
            }

            String originalTitle = data.getString(COL_ORIGINAL_TITLE);
            titleTV.setText(originalTitle);

            releaseYearTV.setText(String.valueOf(MovieUtils.getReadableReleaseYear(data.getString(COL_RELEASE_DATE))));

            Double voteAverage = data.getDouble(COL_VOTE_AVERAGE);
            voteAverageTV.setText(String.format("%.1f/10.0", voteAverage));

            String overview = data.getString(COL_OVERVIEW);
            overviewTV.setText(overview);

            String backDropPath = data.getString(COL_BACKDROP_PATH);
            Picasso.with(getActivity()).load(Constant.MOVIEDB_IMAGE_PATH + backDropPath).into(backDropImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    private void fetchMovieTrailers(String movieId, MovieDBService movieDBService) {
        movieDBService.getTrailersByMovie(movieId, BuildConfig.MOVIE_DB_API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<TrailerData, List<Trailer>>() {
                    @Override
                    public List<Trailer> call(TrailerData trailerData) {
                        return trailerData.getResults();
                    }
                })
                .subscribe(
                        new Action1<List<Trailer>>() {
                            @Override
                            public void call(List<Trailer> trailers) {
                                if (trailers != null) {
                                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                                    for (final Trailer tl : trailers) {
                                        View tlLayout = inflater.inflate(R.layout.list_item_trailer, null, false);
                                        TextView trailteTitle = (TextView) tlLayout.findViewById(R.id.trailer_title);
                                        trailteTitle.setText(tl.getName());

                                        tlLayout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + tl.getKey())));
                                            }
                                        });

                                        trailerLayout.addView(tlLayout);
                                    }
                                }
                            }
                        }
                );
    }

    private void fetchMovieReviews(String movieId, MovieDBService movieDBService) {
        Log.d(LOG_TAG, "fetchMovieReviews with " + movieId);
        movieDBService.getReviewsByMovie(movieId, BuildConfig.MOVIE_DB_API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ReviewData, List<Review>>() {
                    @Override
                    public List<Review> call(ReviewData reviewData) {
                        return reviewData.getResults();
                    }
                })
                .subscribe(
                        new Action1<List<Review>>() {
                            @Override
                            public void call(List<Review> reviews) {
                                if (reviews != null) {
                                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                                    for (Review rv : reviews) {
                                        View rvLayout = inflater.inflate(R.layout.list_item_review, null, false);
                                        TextView authorTv = (TextView) rvLayout.findViewById(R.id.author_tv);
                                        authorTv.setText(rv.getAuthor());
                                        TextView descriptionTV = (TextView) rvLayout.findViewById(R.id.description_tv);
                                        descriptionTV.setText(rv.getContent());
                                        reviewLayout.addView(rvLayout);
                                    }
                                }
                            }
                        }
                );
    }

}
