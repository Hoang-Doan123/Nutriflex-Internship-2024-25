package com.example.newspackage;

import com.example.requestpackage.ArticleBookmarkRequest;
import com.example.requestpackage.ArticleCommentRequest;
import com.example.requestpackage.ArticleLikeRequest;
import com.example.requestpackage.VideoLikeRequest;
import com.example.responsepackage.ArticleBookmarkResponse;
import com.example.responsepackage.ArticleCommentResponse;
import com.example.responsepackage.ArticleLikeResponse;
import com.example.responsepackage.VideoLikeResponse;
import com.example.videopackage.videoComment.VideoComment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NewsAPI {
    @GET("articles")
    Call<List<SmallNews>> getNews();

    @GET("articles/search")
    Call<List<SmallNews>> getNews(@Query("originKey") String originKey);

    @POST("articles/reactions")
    Call<VideoLikeResponse> addLike(@Body VideoLikeRequest request);

    @POST("articles/bookmarks")
    Call<ArticleBookmarkResponse> addBookmark(@Body ArticleBookmarkRequest request);

    @POST("articles/comments")
    Call<ArticleCommentResponse> addComment(@Body ArticleCommentRequest request);

    @GET("articles/reactions")
    Call<ArticleLikeResponse> getLike(@Query("user") int user, @Query("articleId") int articleId);


    @GET("articles/bookmarks")
    Call<ArticleBookmarkResponse> getBookmark(@Query("user") int user, @Query("articled") int articleId);

    @DELETE("articles/reactions")
    Call<Void> deleteLike(@Query("user") int user, @Query("articleId") int articleId);

    @DELETE("articles/bookmarks")
    Call<Void> deleteBookmark(@Query("user") int user, @Query("articleId") int articleId);
}
