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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MainActivityFragmentImageAdapter imageAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set imageview adapter on gridview to display images in grid
        View mainFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView)mainFragmentView.findViewById(R.id.id_movies_grid);
        gridview.setAdapter(imageAdapter = new MainActivityFragmentImageAdapter(getContext(), gridview));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
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
        });

        return mainFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        GridView gridview = (GridView)getActivity().findViewById(R.id.id_movies_grid);
        gridview.setAdapter(imageAdapter = new MainActivityFragmentImageAdapter(getContext(), gridview));
    }
}
