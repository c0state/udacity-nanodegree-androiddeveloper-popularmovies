package com.udacity.nanodegree.androiddevelopers.stephen.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.nanodegree.androiddevelopers.stephen.popularmovies.utils.Video;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivityFragmentVideosAdapter extends ArrayAdapter<Video> {
    private Context mContext;
    private int layoutResourceId;

    public MovieDetailActivityFragmentVideosAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VideoHolder videoHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            videoHolder = new VideoHolder(row);
            row.setTag(videoHolder);
        } else {
            videoHolder = (VideoHolder)row.getTag();
        }

        Video video = getItem(position);
        videoHolder.videoText.setText(video.name);

        return row;
    }

    static class VideoHolder
    {
        @BindView(R.id.id_trailer_image)
        ImageView videoPlayIcon;
        @BindView(R.id.id_trailer_text)
        TextView videoText;

        VideoHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
