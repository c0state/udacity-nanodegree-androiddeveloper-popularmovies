package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Movie implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("original_title")
    public String original_title;

    @SerializedName("poster_path")
    public String poster_path;

    @SerializedName("overview")
    public String overview;

    @SerializedName("release_date")
    public Date release_date;

    @SerializedName("vote_average")
    public double vote_average;

    @SerializedName("runtime")
    public int runtime;
}
