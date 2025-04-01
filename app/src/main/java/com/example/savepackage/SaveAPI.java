package com.example.savepackage;

import com.example.newspackage.SmallNews;
import com.example.videopackage.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SaveAPI {
    @GET("videos/bookmarks/getVideos")
    Call<List<Video>> getVideos(@Query("user") int user);

    @GET("articles/bookmarks/getArticles")
    Call<List<SmallNews>> getArticles(@Query("user") int user);
}

