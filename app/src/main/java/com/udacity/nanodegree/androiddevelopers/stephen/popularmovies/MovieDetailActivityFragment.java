package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Movie;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.MoviesApiHelpers;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Reviews;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Video;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Videos;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import io.realm.Realm;

public class MovieDetailActivityFragment extends Fragment {
    private Movie mMovie;

    // butterknife bindings
    @BindView(R.id.id_movie_detail_title) TextView detailTitleText;
    @BindView(R.id.id_movie_detail_runtime) TextView runtimeText;
    @BindView(R.id.id_movie_detail_video_list) ExpandableHeightListView videosView;
    @BindView(R.id.id_movie_detail_review_list) ExpandableHeightListView reviewsView;
    @BindView(R.id.id_movie_detail_fav_button) MaterialFavoriteButton favoriteButton;
    @BindView(R.id.id_movie_detail_image) ImageView moviePosterView;
    @BindView(R.id.id_movie_detail_year) TextView yearText;
    @BindView(R.id.id_movie_detail_synopsis) TextView synopsisText;
    @BindView(R.id.id_movie_detail_rating) TextView ratingText;
    Unbinder unbinder;

    public MovieDetailActivityFragment() {
    }

    private void getMovieRuntime(final Movie targetMovie) {
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
                if (getActivity() == null) {
                    return;
                }

                runtimeText.setText(Integer.toString(integer) + " " + getString(R.string.pref_movies_runtime_suffix));
                super.onPostExecute(integer);
            }
        }.execute();
    }


    @OnItemClick(R.id.id_movie_detail_video_list)
    void videosListItemClickHandler(AdapterView<?> adapterView, View view, int i, long l) {
        // set up click listener on video listview to play video via intent
        Video item = (Video)adapterView.getAdapter().getItem(i);

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.youtube_video_url_prefix) + item.key));
        startActivity(intent);
    }

    private void setUpVideosListHandling() {
        // set up videos listadapter and attach to videos listview
        final MovieDetailActivityFragmentVideosAdapter videosAdapter =
                new MovieDetailActivityFragmentVideosAdapter(getContext(),
                        R.layout.fragment_movie_detail_video_listitem);
        videosView.setAdapter(videosAdapter);

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
                // if we have at least one video, enable share menu, but make sure activity is alive
                if (getActivity() == null) {
                    return;
                }

                videosAdapter.addAll(videos.listOfVideos);
                videosAdapter.notifyDataSetChanged();

                getActivity().invalidateOptionsMenu();
            }
        }.execute();

        videosView.setExpanded(true);
    }

    private void setUpReviewsListHandling() {
        // set up reviews adapter and attach to reviews list
        final MovieDetailActivityFragmentReviewsAdapter reviewsAdapter =
                new MovieDetailActivityFragmentReviewsAdapter(getContext(),
                        R.layout.fragment_movie_detail_review_listitem);
        reviewsView.setAdapter(reviewsAdapter);

        // load reviews into reviews adapter
        new AsyncTask<Void, Void, Reviews>() {
            @Override
            protected Reviews doInBackground(Void... voids) {
                try {
                    return MoviesApiHelpers.getMoviesService()
                            .getMovieReviews(mMovie.id, BuildConfig.THEMOVIESDB_API_KEY3)
                            .execute().body();
                } catch (IOException ioe) {
                    Log.e(getClass().getSimpleName(),
                            "Error invoking Api to get movie reviews for movie id " + mMovie.id);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Reviews reviews) {
                if (getActivity() == null) {
                    return;
                }

                reviewsAdapter.addAll(reviews.listOfReviews);
                reviewsAdapter.notifyDataSetChanged();
            }
        }.execute();

        reviewsView.setExpanded(true);
    }

    private void setUpFavoritesHandling() {
        // load favorites handler
        Realm realm = Realm.getDefaultInstance();
        Movie favoriteMovie = realm.where(Movie.class).equalTo("id", mMovie.id).findFirst();
        favoriteButton.setFavorite(favoriteMovie != null, false);

        favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        Realm realm = Realm.getDefaultInstance();

                        if (favorite) {
                            if (realm.where(Movie.class).equalTo("id", mMovie.id)
                                    .findAll().isEmpty()) {
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // add share menu only if a video exists
        Video firstVideo = videosView.getAdapter().getCount() > 0
                ? (Video)videosView.getAdapter().getItem(0)
                : null;

        if (firstVideo != null) {
            MenuItem item = menu.findItem(R.id.id_menu_action_share);
            ShareActionProvider shareActionProvider =
                    (ShareActionProvider)MenuItemCompat.getActionProvider(item);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.youtube_video_url_prefix) + firstVideo.key);
            shareActionProvider.setShareIntent(shareIntent);
        }
        else {
            menu.removeItem(R.id.id_menu_action_share);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        detailTitleText.setText(mMovie.original_title);

        int width = Integer.valueOf(getString(R.string.pref_movies_poster_width));
        int height = Integer.valueOf(getString(R.string.pref_movies_poster_height));
        double posterScalePercentage = Double.valueOf(getString(R.string.pref_movies_poster_scale_perc));
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        moviePosterView.setLayoutParams(
                new LinearLayout.LayoutParams((int)(size.x/2*posterScalePercentage),
                (int)(size.x/2*posterScalePercentage)*height/width));
        moviePosterView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(view.getContext())
                .load(MoviesApiHelpers.getMoviesImageBaseUrl(width) + "/" + mMovie.poster_path)
                .into(moviePosterView);

        Calendar cal = Calendar.getInstance();
        cal.setTime(mMovie.release_date);
        yearText.setText(String.format(Locale.getDefault(), "%4d", cal.get(Calendar.YEAR)));

        getMovieRuntime(mMovie);

        synopsisText.setText(mMovie.overview);

        ratingText.setText(String.format(Locale.getDefault(), "%1.1f", mMovie.vote_average));

        setUpVideosListHandling();
        setUpReviewsListHandling();
        setUpFavoritesHandling();
    }
}
