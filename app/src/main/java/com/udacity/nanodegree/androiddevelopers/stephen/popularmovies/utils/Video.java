package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Video implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("key")
    public String key;

    @SerializedName("name")
    public String name;

    @SerializedName("site")
    public String site;

    @SerializedName("size")
    public int size;
}
