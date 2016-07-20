package com.vagabond.popularmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vagabond.popularmovie.model.Constant;
import com.vagabond.popularmovie.model.MovieDetail;
import com.vagabond.popularmovie.services.WebService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HoaNV on 7/19/16.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private Long mMovieId;
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

        Intent intent = getActivity().getIntent();
        Log.d(LOG_TAG, "" + intent.getLongExtra(Intent.EXTRA_TEXT, 0));
        if (intent != null && intent.hasExtra(Constant.EXTRA_MOVIEID)) {
            mMovieId = intent.getLongExtra(Constant.EXTRA_MOVIEID, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    public void onStart() {
        super.onStart();
        WebService.getMovieDBService().getMovieDetail(mMovieId, BuildConfig.MOVIE_DB_API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieDetail -> updateView(movieDetail),
                        e -> handleError(e));
    }

    private void handleError(Throwable e) {
        Log.e(LOG_TAG, "Error of fetching movie detail" ,e);
    }

    private void updateView(MovieDetail movieDetail) {
        if (movieDetail != null) {
            Picasso.with(getActivity()).load(Constant.MOVIEDB_IMAGE_PATH + movieDetail.getPosterPath()).into(posterImageView);
            titleTV.setText(movieDetail.getTitle());
            releaseYearTV.setText(String.valueOf(MovieUtils.getReadableReleaseYear(movieDetail.getReleaseDate())));
            voteAverageTV.setText(String.format("%.1f/10.0",movieDetail.getVoteAverage()));
            durationTV.setText(String.valueOf(movieDetail.getRuntime()));
            overviewTV.setText(movieDetail.getOverview());
        }
    }
}
