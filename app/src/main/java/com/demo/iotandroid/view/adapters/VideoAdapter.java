package com.demo.iotandroid.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.iotandroid.models.Video;

import java.util.ArrayList;

import com.demo.iotandroid.R;

public class VideoAdapter extends ArrayAdapter<Video> {
    public VideoAdapter(Context context, ArrayList<Video> videos) {
        super(context, 0, videos);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Video video = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_list_item, parent, false);
        }

        ImageView miniature = (ImageView) convertView.findViewById(R.id.miniature);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView date = (TextView) convertView.findViewById(R.id.date);

        assert video != null;
        title.setText(video.getName());

        if (video.getDate() != null) {
            date.setText(video.getDate());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri videoUri = Uri.parse(video.getUrl());
                Intent intentPlayer = new Intent(Intent.ACTION_VIEW, videoUri);
                intentPlayer.setDataAndType(videoUri, "video/mp4");
                getContext().startActivity(intentPlayer);
            }
        });

        return convertView;
    }
}