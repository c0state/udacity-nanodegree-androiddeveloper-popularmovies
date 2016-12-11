package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Review;

public class MovieDetailActivityFragmentReviewsAdapter extends ArrayAdapter<Review> {
    private Context mContext;
    private int layoutResourceId;

    public MovieDetailActivityFragmentReviewsAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ReviewHolder reviewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            reviewHolder = new ReviewHolder();
            reviewHolder.reviewName = (TextView)row.findViewById(R.id.id_review_name);
            reviewHolder.reviewText = (TextView)row.findViewById(R.id.id_review_text);

            row.setTag(reviewHolder);
        } else {
            reviewHolder = (ReviewHolder)row.getTag();
        }

        Review review = getItem(position);
        reviewHolder.reviewName.setText(review.author);
        reviewHolder.reviewText.setText(review.content);

        return row;
    }

    public static class ReviewHolder
    {
        TextView reviewName;
        TextView reviewText;
    }
}
