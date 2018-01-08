package com.demo.iotandroid.view.activities;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.demo.iotandroid.helpers.FirebaseVideoHelper;
import com.demo.iotandroid.models.Video;
import com.demo.iotandroid.view.adapters.VideoAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import com.demo.iotandroid.helpers.OnNewVideoListener;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    FloatingActionButton buttonNewVideo;
    FirebaseVideoHelper mFirebaseHelper;
    ListView mListView;
    VideoAdapter mAdapter;
    List<Video> mVideos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.demo.iotandroid.R.layout.activity_main);
        buttonNewVideo = (FloatingActionButton) findViewById(com.demo.iotandroid.R.id.floatingActionButton);
        mListView = (ListView) findViewById(com.demo.iotandroid.R.id.listView);

        buttonNewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });
        mFirebaseHelper = new FirebaseVideoHelper(this, MainActivity.this);
        mFirebaseHelper.getAllVideos(new OnNewVideoListener() {
            @Override
            public void callback(List<Video> videos) {
                mVideos = videos;
                mAdapter = new VideoAdapter(MainActivity.this, new ArrayList<>(mVideos));
                mListView.setAdapter(mAdapter);
            }
        });
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            mFirebaseHelper.sendFile(intent.getData());
        }
    }
}
