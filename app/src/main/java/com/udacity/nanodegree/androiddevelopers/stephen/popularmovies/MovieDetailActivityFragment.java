package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Movie;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.MoviesApiHelpers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    private void getMovieRuntime(final Movie targetMovie) {
        final TextView runtimeText = (TextView)getView().findViewById(R.id.id_movie_detail_runtime);

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    Movie movie = MoviesApiHelpers.getMoviesService().getMovieDetails(
                            targetMovie.id, BuildConfig.THEMOVIESDB_API_KEY3).execute().body();
                    return movie.runtime;
                } catch (IOException ioe) {
                    Log.e("MovieDetail", "Error calling API to get movie detail for movie ID " + targetMovie.id);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                runtimeText.setText(Integer.toString(integer));
                super.onPostExecute(integer);
            }
        }.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Movie movie = (Movie)getActivity().getIntent().getSerializableExtra(getString(R.string.movie_detail_object));

        TextView detailTitleText = (TextView)view.findViewById(R.id.id_movie_detail_title);
        detailTitleText.setText(movie.original_title);

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        ImageView moviePosterView = (ImageView)view.findViewById(R.id.id_movie_detail_image);
        moviePosterView.setLayoutParams(new LinearLayout.LayoutParams((int)(size.x/2*0.9),
                (int)(size.x/2*0.9)*277/185));
        moviePosterView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // TODO: const out the image width
        Picasso.with(view.getContext())
                .load(MoviesApiHelpers.getMoviesImageBaseUrl(185) + "/" + movie.poster_path)
                .into(moviePosterView);

        TextView yearText = (TextView)view.findViewById(R.id.id_movie_detail_year);
        Calendar cal = Calendar.getInstance();
        cal.setTime(movie.release_date);
        yearText.setText(String.format(Locale.getDefault(), "%4d", cal.get(Calendar.YEAR)));

        getMovieRuntime(movie);

        TextView synopsisText = (TextView)view.findViewById(R.id.id_movie_detail_synopsis);
        synopsisText.setText(movie.overview);

        TextView ratingText = (TextView)view.findViewById(R.id.id_movie_detail_rating);
        ratingText.setText(String.format(Locale.getDefault(), "%1.1f", movie.vote_average));
    }
}
