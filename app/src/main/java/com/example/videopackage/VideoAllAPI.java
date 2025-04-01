package com.example.videopackage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VideoAllAPI {
    @GET("videos")
    Call<List<Video>> getVideos();
}
