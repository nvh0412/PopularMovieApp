package com.vagabond.popularmovie.services;

import com.vagabond.popularmovie.model.Constant;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

;

/*
 * Created by HoaNV on 7/20/16.
 */
public class WebService {

    public static MovieDBService getMovieDBService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.MOVIEDB_PATH)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MovieDBService.class);
    }

}
