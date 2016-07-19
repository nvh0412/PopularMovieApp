package com.vagabond.popularmovie;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public DetailFragment() {
        // Required empty public constructor
    }

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
        new FetchMovieDetailAsyncTask().execute(mMovieId);
    }

    public class FetchMovieDetailAsyncTask extends AsyncTask<Long, Void, MovieDetail> {
        private final String LOG_TAG = FetchMovieDetailAsyncTask.class.getSimpleName();

        @Override
        protected MovieDetail doInBackground(Long... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            final String MOVIEDB_BASE_URL = Constant.MOVIEDB_PATH;
            final String APPID_PARAM = "api_key";

            try {
                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(String.valueOf(params[0]))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                movieJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            MovieDetail movieDetail = null;
            try {
                movieDetail = getMovieDetailFromJson(movieJsonStr);
                Log.v(LOG_TAG, movieDetail.toString());
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing json", e);
            }

            return movieDetail;
        }

        @Override
        protected void onPostExecute(MovieDetail movieDetail) {
            super.onPostExecute(movieDetail);
            if (movieDetail != null) {
                Picasso.with(getActivity()).load(Constant.MOVIEDB_IMAGE_PATH + movieDetail.getPosterPath()).into(posterImageView);
                titleTV.setText(movieDetail.getTitle());
                releaseYearTV.setText(String.valueOf(movieDetail.getReleaseYear()));
                voteAverageTV.setText(String.format("%.1f/10.0",movieDetail.getVoteAverage()));
                durationTV.setText(String.valueOf(movieDetail.getRuntime()));
                overviewTV.setText(movieDetail.getOverview());
            }
        }
    }

    private MovieDetail getMovieDetailFromJson(String movieJsonStr) throws JSONException {
        MovieDetail movieDetail = null;
        if (movieJsonStr != null) {
            JSONObject movieJsonObj = new JSONObject(movieJsonStr);
            movieDetail = new MovieDetail();
            movieDetail.setTitle(movieJsonObj.getString("title"));
            movieDetail.setPosterPath(movieJsonObj.getString("poster_path"));
            movieDetail.setOverview(movieJsonObj.getString("overview"));
            movieDetail.setRuntime(movieJsonObj.getInt("runtime"));
            movieDetail.setVoteAverage(movieJsonObj.getLong("vote_average"));
            movieDetail.setReleaseYear(MovieUtils.getReadableReleaseYear(movieJsonObj.getString("release_date")));
        }

        return movieDetail;
    }
}
