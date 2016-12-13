package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Movie;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.MoviesApi;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.MoviesApiHelpers;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;

public class MainActivityFragmentImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> mMovies;
    private GridView mGridView;

    public MainActivityFragmentImageAdapter(Context c, GridView g) {
        mContext = c;
        mGridView = g;
        getMovies();
    }

    public List<Movie> movies() {
        return mMovies;
    }

    private String getSortOrderPref() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortPrefKey = mContext.getResources().getString(R.string.pref_movies_sort_order_key);
        String sortPrefDefault = mContext.getResources().getString(R.string.pref_movies_sort_order_default);
        return prefs.getString(sortPrefKey, sortPrefDefault);
    }

    private void getMovies() {
        final MoviesApi moviesApi = MoviesApiHelpers.getMoviesService();
        final ListAdapter thisAdapter = this;

        new AsyncTask<Void, Void, List<Movie>>() {
            @Override
            protected List<Movie> doInBackground(Void... params) {
                try {
                    String moviesSortOrder = getSortOrderPref();

                    if (mContext.getResources().getString(R.string.pref_movies_sort_order_popular).equals(moviesSortOrder)) {
                        return moviesApi.getPopularMovies(BuildConfig.THEMOVIESDB_API_KEY3, 1).execute().body().listOfMovies;
                    }
                    else if (mContext.getResources().getString(R.string.pref_movies_sort_order_top_rated).equals(moviesSortOrder)) {
                        return moviesApi.getTopRatedMovies(BuildConfig.THEMOVIESDB_API_KEY3, 1).execute().body().listOfMovies;
                    }
                }
                catch (IOException ioe) {
                    Log.e(getClass().getSimpleName(), "IO error calling popular movies API!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Movie> movies) {
                mMovies = movies;

                String moviesSortOrder = getSortOrderPref();
                if (mMovies == null && mContext.getResources().getString(R.string.pref_movies_sort_order_favorites).equals(moviesSortOrder)) {
                    Realm realm = Realm.getDefaultInstance();
                    mMovies = realm.copyFromRealm(realm.where(Movie.class).findAll());
                }
                mGridView.setAdapter(thisAdapter);
            }
        }.execute();
    }

    public int getCount() {
        return mMovies != null ? mMovies.size() : 0;
    }

    public Object getItem(int position) {
        return mMovies != null ? mMovies.get(position) : null;
    }

    public long getItemId(int position) {
        // this must be implemented but we don't need it
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        String width = mContext.getResources().getString(R.string.pref_movies_poster_width);
        String height = mContext.getResources().getString(R.string.pref_movies_poster_height);
        if (convertView == null) {
            // if it's not recycled, reuse
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mGridView.getColumnWidth(),
                    mGridView.getColumnWidth()*Integer.valueOf(height)/Integer.valueOf(width)));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        String posterPath = MoviesApiHelpers.getMoviesImageBaseUrl(
                Integer.valueOf(width)) + "/" + mMovies.get(position).poster_path;
        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.drawable.poster_placeholder)
                .error(R.drawable.poster_placeholder)
                .into(imageView);
        return imageView;
    }
}
