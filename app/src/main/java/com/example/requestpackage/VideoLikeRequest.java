package com.example.requestpackage;

public class VideoLikeRequest {
    private int userId;
    private int videoId;

    public VideoLikeRequest(int userId, int videoId) {
        this.userId = userId;
        this.videoId = videoId;
    }
}
