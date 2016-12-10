package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Movie {

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

    public Movie(int id, String title, Date year) {
        this.id = id;
        this.original_title = title;
        this.release_date = year;
    }
}
