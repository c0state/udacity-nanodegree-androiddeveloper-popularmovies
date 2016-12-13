package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.GeneralHelpers;
import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;

public class MainActivityFragment extends Fragment {
    private MainActivityFragmentImageAdapter imageAdapter;

    // butterknife bindings
    @BindView(R.id.id_movies_grid)
    GridView gridView;
    private Unbinder unbinder;

    public MainActivityFragment() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnItemClick(R.id.id_movies_grid)
    void onMovieGridItemClick(AdapterView<?> parent, View v, int position, long id) {
        Movie clickedMovie = imageAdapter.movies().get(position);
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra(getString(R.string.movie_detail_object), clickedMovie);
        if (GeneralHelpers.isOnline(getContext())) {
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), "Network access required for movie details!",
                    Integer.valueOf(getString(R.string.toast_seconds_default))).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set imageview adapter on gridview to display images in grid
        View mainFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, mainFragmentView);
        gridView.setAdapter(imageAdapter = new MainActivityFragmentImageAdapter(getContext(), gridView));
        return mainFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        gridView.setAdapter(imageAdapter = new MainActivityFragmentImageAdapter(getContext(), gridView));
    }
}
