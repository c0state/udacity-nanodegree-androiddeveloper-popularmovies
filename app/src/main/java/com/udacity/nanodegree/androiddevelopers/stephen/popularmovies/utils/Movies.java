package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movies {

    @SerializedName("results")
    public List<Movie> listOfMovies;
}
