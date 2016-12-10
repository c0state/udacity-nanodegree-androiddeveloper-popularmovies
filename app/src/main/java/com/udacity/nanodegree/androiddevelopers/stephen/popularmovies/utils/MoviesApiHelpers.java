package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesApiHelpers {
    public static final String baseUrl = "https://api.themoviedb.org";
    public static final String imageBaseUrlPrefix = "http://image.tmdb.org/t/p";

    public static String getMoviesImageBaseUrl(int width) {
        return imageBaseUrlPrefix + "/w" + width;
    }

    public static MoviesApi getMoviesService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MoviesApi.class);
    }
}
