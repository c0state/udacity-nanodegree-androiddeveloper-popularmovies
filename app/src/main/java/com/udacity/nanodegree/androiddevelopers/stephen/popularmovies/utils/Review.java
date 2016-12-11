package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Review implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("author")
    public String author;

    @SerializedName("content")
    public String content;

    @SerializedName("url")
    public String url;
}
