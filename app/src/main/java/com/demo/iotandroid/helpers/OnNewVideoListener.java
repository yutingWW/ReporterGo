package com.demo.iotandroid.helpers;


import com.demo.iotandroid.models.Video;

import java.util.List;

public interface OnNewVideoListener {
    void callback(List<Video> videos);
}