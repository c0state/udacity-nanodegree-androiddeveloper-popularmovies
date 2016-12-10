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
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Movies;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.MoviesApi;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.MoviesApiHelpers;

import java.io.IOException;

public class MainActivityFragmentImageAdapter extends BaseAdapter {
    private Context mContext;
    private Movies mMovies;
    private GridView mGridView;

    public MainActivityFragmentImageAdapter(Context c, GridView g) {
        mContext = c;
        mGridView = g;
        getMovies();
    }

    private void getMovies() {
        final MoviesApi moviesApi = MoviesApiHelpers.getMoviesService();
        final ListAdapter thisAdapter = this;

        new AsyncTask<Void, Void, Movies>() {
            @Override
            protected Movies doInBackground(Void... params) {
                try {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                    String sortPrefKey = mContext.getResources().getString(R.string.pref_movies_sort_order_key);
                    String sortPrefDefault = mContext.getResources().getString(R.string.pref_movies_sort_order_default);
                    String moviesSortOrder = prefs.getString(sortPrefKey, sortPrefDefault);

                    return moviesSortOrder.equals(sortPrefDefault)
                            ? moviesApi.getPopularMovies(BuildConfig.THEMOVIESDB_API_KEY3, 1).execute().body()
                            : moviesApi.getTopRatedMovies(BuildConfig.THEMOVIESDB_API_KEY3, 1).execute().body();
                }
                catch (IOException ioe) {
                    // TODO: pull error prefix from const strings xml
                    Log.e("PopularMovies", "IO error calling popular movies API!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Movies movies) {
                mMovies = movies;
                mGridView.setAdapter(thisAdapter);
            }
        }.execute();
    }

    public int getCount() {
        return mMovies != null ? mMovies.listOfMovies.size() : 0;
    }

    public Object getItem(int position) {
        return mMovies != null ? mMovies.listOfMovies.get(position) : null;
    }

    public long getItemId(int position) {
        // TODO: is this needed?
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: const out the width/height
        int width = 185;
        int height = 277;

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mGridView.getColumnWidth(),
                    mGridView.getColumnWidth()*height/width));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        // TODO: set up actual image via another asynctask
        String posterPath = MoviesApiHelpers.getMoviesImageBaseUrl(width) + "/" + mMovies.listOfMovies.get(position).poster_path;
        Picasso.with(mContext).load(posterPath).into(imageView);
        return imageView;
    }
}
