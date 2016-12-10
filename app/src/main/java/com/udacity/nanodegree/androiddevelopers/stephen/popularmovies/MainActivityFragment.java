package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView)mainFragmentView.findViewById(R.id.id_movies_grid);
        gridview.setAdapter(new MainActivityFragmentImageAdapter(getContext(), gridview));

        return mainFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        GridView gridview = (GridView)getActivity().findViewById(R.id.id_movies_grid);
        gridview.setAdapter(new MainActivityFragmentImageAdapter(getContext(), gridview));
    }
}
