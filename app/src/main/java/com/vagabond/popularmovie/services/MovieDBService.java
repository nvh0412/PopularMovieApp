package com.vagabond.popularmovie.services;

import com.vagabond.popularmovie.model.Constant;
import com.vagabond.popularmovie.model.Movie;
import com.vagabond.popularmovie.model.MovieData;
import com.vagabond.popularmovie.model.ReviewData;
import com.vagabond.popularmovie.model.TrailerData;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by HoaNV on 7/20/16.
 */
public interface MovieDBService {

    @GET("movie/{order}")
    Observable<MovieData> getMovieData(@Path("order") String order, @Query(Constant.API_KEY) String apiKey);

    @GET("movie/{id}")
    Observable<Movie> getMovieDetail(@Path("id") long id, @Query(Constant.API_KEY) String apiKey);

    @GET("movie/{id}/videos")
    Observable<TrailerData> getTrailersByMovie(@Path("id") String movieId, @Query(Constant.API_KEY) String apiKey);

    @GET("movie/{id}/reviews")
    Observable<ReviewData> getReviewsByMovie(@Path("id") String movieId, @Query(Constant.API_KEY) String apiKey);
}
