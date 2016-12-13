package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Movie extends RealmObject implements Parcelable {
    @PrimaryKey
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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(original_title);
        out.writeString(poster_path);
        out.writeString(overview);
        out.writeLong(release_date.getTime());
        out.writeDouble(vote_average);
        out.writeInt(runtime);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie() {
    }

    private Movie(Parcel in) {
        id = in.readInt();
        original_title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        release_date = new Date(in.readLong());
        vote_average = in.readDouble();
        runtime = in.readInt();
    }
}
