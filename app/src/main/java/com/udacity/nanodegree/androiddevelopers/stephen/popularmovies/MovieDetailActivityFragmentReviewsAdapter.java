package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Review;

import butterknife.BindView;
import butterknife.ButterKnife;

class MovieDetailActivityFragmentReviewsAdapter extends ArrayAdapter<Review> {
    private Context mContext;
    private int layoutResourceId;

    MovieDetailActivityFragmentReviewsAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
    }

    @NonNull
    public View getView(int position, View reviewRowView, ViewGroup parent) {
        ReviewHolder reviewHolder;
        if (reviewRowView == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            reviewRowView = inflater.inflate(layoutResourceId, parent, false);

            reviewHolder = new ReviewHolder(reviewRowView);

            reviewRowView.setTag(reviewHolder);
        } else {
            reviewHolder = (ReviewHolder)reviewRowView.getTag();
        }

        Review review = getItem(position);
        reviewHolder.reviewName.setText(review.author);
        reviewHolder.reviewText.setText(review.content);

        return reviewRowView;
    }

    static class ReviewHolder
    {
        // butterknife
        @BindView(R.id.id_review_name) TextView reviewName;
        @BindView(R.id.id_review_text) TextView reviewText;

        ReviewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
