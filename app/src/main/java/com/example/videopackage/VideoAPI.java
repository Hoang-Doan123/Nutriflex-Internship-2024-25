package com.example.videopackage;

import com.example.requestpackage.VideoBookmarkRequest;
import com.example.requestpackage.VideoCommentRequest;
import com.example.requestpackage.VideoLikeRequest;
import com.example.responsepackage.VideoBookmarkResponse;
import com.example.responsepackage.VideoCommentResponse;
import com.example.responsepackage.VideoLikeResponse;
import com.example.videopackage.videoComment.VideoComment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VideoAPI {
    @GET("videos/search")
    Call<List<Video>> getVideos(@Query("originKey") String originKey);

    @POST("videos/reactions")
    Call<VideoLikeResponse> addLike(@Body VideoLikeRequest request);

    @POST("videos/bookmarks")
    Call<VideoBookmarkResponse> addBookmark(@Body VideoBookmarkRequest request);

    @POST("videos/comments")
    Call<VideoCommentResponse> addComment(@Body VideoCommentRequest request);

    @GET("videos/reactions")
    Call<List<VideoLikeResponse>> getLike(@Query("user") int user, @Query("videoId") int videoId);

    @GET("videos/comments")
    Call<List<VideoComment>> getComments(@Query("videoId") int videoId);

    @GET("videos/bookmarks")
    Call<List<VideoBookmarkResponse>> getBookmark(@Query("user") int user, @Query("videoId") int videoId);

    @DELETE("videos/reactions")
    Call<Void> deleteLike(@Query("user") int user, @Query("videoId") int videoId);

    @DELETE("videos/bookmarks")
    Call<Void> deleteBookmark(@Query("user") int user, @Query("videoId") int videoId);
}
