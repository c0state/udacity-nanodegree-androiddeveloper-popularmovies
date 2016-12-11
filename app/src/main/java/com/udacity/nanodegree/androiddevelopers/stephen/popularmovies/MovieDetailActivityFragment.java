package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Movie;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.MoviesApiHelpers;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Reviews;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Video;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Videos;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    private Movie mMovie;

    public MovieDetailActivityFragment() {
    }

    private void getMovieRuntime(final Movie targetMovie) {
        final TextView runtimeText = (TextView)getView().findViewById(R.id.id_movie_detail_runtime);

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    Movie movieDetail = MoviesApiHelpers.getMoviesService().getMovieDetails(
                            targetMovie.id, BuildConfig.THEMOVIESDB_API_KEY3).execute().body();
                    return movieDetail.runtime;
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
        mMovie = (Movie)getActivity().getIntent().getSerializableExtra(getString(R.string.movie_detail_object));
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        TextView detailTitleText = (TextView)view.findViewById(R.id.id_movie_detail_title);
        detailTitleText.setText(mMovie.original_title);

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        ImageView moviePosterView = (ImageView)view.findViewById(R.id.id_movie_detail_image);
        moviePosterView.setLayoutParams(new LinearLayout.LayoutParams((int)(size.x/2*0.9),
                (int)(size.x/2*0.9)*277/185));
        moviePosterView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // TODO: const out the image width
        Picasso.with(view.getContext())
                .load(MoviesApiHelpers.getMoviesImageBaseUrl(185) + "/" + mMovie.poster_path)
                .into(moviePosterView);

        TextView yearText = (TextView)view.findViewById(R.id.id_movie_detail_year);
        Calendar cal = Calendar.getInstance();
        cal.setTime(mMovie.release_date);
        yearText.setText(String.format(Locale.getDefault(), "%4d", cal.get(Calendar.YEAR)));

        getMovieRuntime(mMovie);

        TextView synopsisText = (TextView)view.findViewById(R.id.id_movie_detail_synopsis);
        synopsisText.setText(mMovie.overview);

        TextView ratingText = (TextView)view.findViewById(R.id.id_movie_detail_rating);
        ratingText.setText(String.format(Locale.getDefault(), "%1.1f", mMovie.vote_average));

        final MovieDetailActivityFragmentVideosAdapter videosAdapter = new MovieDetailActivityFragmentVideosAdapter(
                getContext(),
                R.layout.fragment_movie_detail_video_listitem);
        final ListView videoListView = (ListView)view.findViewById(R.id.id_movie_detail_video_list);
        videoListView.setAdapter(videosAdapter);

        // set up click listener on video list view
        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Video item = videosAdapter.getItem(i);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + item.key));
                startActivity(intent);
            }
        });

        new AsyncTask<Void, Void, Videos>() {
            @Override
            protected Videos doInBackground(Void... voids) {
                try {
                    return MoviesApiHelpers.getMoviesService()
                            .getMovieVideos(mMovie.id, BuildConfig.THEMOVIESDB_API_KEY3)
                            .execute().body();
                } catch (IOException ioe) {
                    // TODO: const out error class name
                    Log.e("DetailActivity", "Error invoking Api to get movie videos for movie id " + mMovie.id);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Videos videos) {
                videosAdapter.addAll(videos.listOfVideos);
                videosAdapter.notifyDataSetChanged();
                // prevent scrolling in video listview
                MoviesApiHelpers.setListViewHeightBasedOnChildren(videoListView);
            }
        }.execute();

        // set up reviews adapter
        final MovieDetailActivityFragmentReviewsAdapter reviewsAdapter = new MovieDetailActivityFragmentReviewsAdapter(
                getContext(),
                R.layout.fragment_movie_detail_review_listitem);
        final ListView reviewListView = (ListView)view.findViewById(R.id.id_movie_detail_review_list);
        reviewListView.setAdapter(reviewsAdapter);

        // load reviews into reviews listview
        new AsyncTask<Void, Void, Reviews>() {
            @Override
            protected Reviews doInBackground(Void... voids) {
                try {
                    return MoviesApiHelpers.getMoviesService()
                            .getMovieReviews(mMovie.id, BuildConfig.THEMOVIESDB_API_KEY3)
                            .execute().body();
                } catch (IOException ioe) {
                    // TODO: const out error class name
                    Log.e("DetailActivity", "Error invoking Api to get movie reviews for movie id " + mMovie.id);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Reviews reviews) {
                reviewsAdapter.addAll(reviews.listOfReviews);
                reviewsAdapter.notifyDataSetChanged();
                // prevent scrolling in review listview
                MoviesApiHelpers.setListViewHeightBasedOnChildren(reviewListView);
            }
        }.execute();

        // load favorites handler
        MaterialFavoriteButton favoriteButton = (MaterialFavoriteButton)view.findViewById(R.id.id_movie_detail_fav_button);
        favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                    }
                });
    }
}
