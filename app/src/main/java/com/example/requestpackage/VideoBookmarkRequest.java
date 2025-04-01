package com.example.requestpackage;

public class VideoBookmarkRequest {
    private int userId;
    private int videoId;

    public VideoBookmarkRequest(int userId, int videoId) {
        this.userId = userId;
        this.videoId = videoId;
    }
}
