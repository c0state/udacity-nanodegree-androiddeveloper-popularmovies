package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.GeneralHelpers;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Movie;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.MoviesApiHelpers;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Reviews;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Video;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Videos;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;

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
                runtimeText.setText(Integer.toString(integer) + " " + getString(R.string.pref_movies_runtime_suffix));
                super.onPostExecute(integer);
            }
        }.execute();
    }

    private void setUpVideosListHandling() {
        // set up videos listadapter and attach to videos listview
        final MovieDetailActivityFragmentVideosAdapter videosAdapter = new MovieDetailActivityFragmentVideosAdapter(
                getContext(),
                R.layout.fragment_movie_detail_video_listitem);
        final ListView videoListView = (ListView)getView().findViewById(R.id.id_movie_detail_video_list);
        videoListView.setAdapter(videosAdapter);

        // set up click listener on video listview to play video via intent
        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Video item = videosAdapter.getItem(i);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + item.key));
                startActivity(intent);
            }
        });

        // get videos for this movie and add to videos listadapter
        new AsyncTask<Void, Void, Videos>() {
            @Override
            protected Videos doInBackground(Void... voids) {
                try {
                    return MoviesApiHelpers.getMoviesService()
                            .getMovieVideos(mMovie.id, BuildConfig.THEMOVIESDB_API_KEY3)
                            .execute().body();
                } catch (IOException ioe) {
                    Log.e(getClass().getSimpleName(), "Error invoking Api to get movie videos for movie id " + mMovie.id);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Videos videos) {
                videosAdapter.addAll(videos.listOfVideos);
                videosAdapter.notifyDataSetChanged();
                // prevent scrolling in video listview
                GeneralHelpers.setListViewHeightBasedOnChildren(videoListView);
            }
        }.execute();
    }

    private void setUpReviewsListHandling() {
        // set up reviews adapter and attach to reviews list
        final MovieDetailActivityFragmentReviewsAdapter reviewsAdapter = new MovieDetailActivityFragmentReviewsAdapter(
                getContext(),
                R.layout.fragment_movie_detail_review_listitem);
        final ListView reviewListView = (ListView) getView().findViewById(R.id.id_movie_detail_review_list);
        reviewListView.setAdapter(reviewsAdapter);

        // load reviews into reviews adapter
        new AsyncTask<Void, Void, Reviews>() {
            @Override
            protected Reviews doInBackground(Void... voids) {
                try {
                    return MoviesApiHelpers.getMoviesService()
                            .getMovieReviews(mMovie.id, BuildConfig.THEMOVIESDB_API_KEY3)
                            .execute().body();
                } catch (IOException ioe) {
                    Log.e(getClass().getSimpleName(), "Error invoking Api to get movie reviews for movie id " + mMovie.id);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Reviews reviews) {
                reviewsAdapter.addAll(reviews.listOfReviews);
                reviewsAdapter.notifyDataSetChanged();
                // prevent scrolling in review listview
                GeneralHelpers.setListViewHeightBasedOnChildren(reviewListView);
            }
        }.execute();
    }

    private void setUpFavoritesHandling() {
        // load favorites handler
        MaterialFavoriteButton favoriteButton = (MaterialFavoriteButton)getView().findViewById(R.id.id_movie_detail_fav_button);

        Realm realm = Realm.getDefaultInstance();
        Movie favoriteMovie = realm.where(Movie.class).equalTo("id", mMovie.id).findFirst();
        favoriteButton.setFavorite(favoriteMovie != null, false);

        favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        Realm realm = Realm.getDefaultInstance();

                        if (favorite) {
                            if (realm.where(Movie.class).equalTo("id", mMovie.id).findAll().isEmpty()) {
                                // add this movie to favorites
                                realm.beginTransaction();
                                realm.copyToRealm(mMovie);
                                realm.commitTransaction();
                            }
                        } else {
                            // remove movie from favorites
                            realm.beginTransaction();
                            realm.where(Movie.class)
                                    .equalTo("id", mMovie.id)
                                    .findAll()
                                    .deleteAllFromRealm();
                            realm.commitTransaction();
                        }
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // get movie object from parent activity that triggered this activity
        mMovie = (Movie)getActivity().getIntent().getSerializableExtra(getString(R.string.movie_detail_object));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        TextView detailTitleText = (TextView)view.findViewById(R.id.id_movie_detail_title);
        detailTitleText.setText(mMovie.original_title);

        int width = Integer.valueOf(getString(R.string.pref_movies_poster_width));
        int height = Integer.valueOf(getString(R.string.pref_movies_poster_height));
        double posterScalePerc = Double.valueOf(getString(R.string.pref_movies_poster_scale_perc));
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        final ImageView moviePosterView = (ImageView)view.findViewById(R.id.id_movie_detail_image);
        moviePosterView.setLayoutParams(new LinearLayout.LayoutParams((int)(size.x/2*posterScalePerc),
                (int)(size.x/2*posterScalePerc)*height/width));
        moviePosterView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(view.getContext())
                .load(MoviesApiHelpers.getMoviesImageBaseUrl(width) + "/" + mMovie.poster_path)
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

        setUpVideosListHandling();
        setUpReviewsListHandling();
        setUpFavoritesHandling();
    }
}
