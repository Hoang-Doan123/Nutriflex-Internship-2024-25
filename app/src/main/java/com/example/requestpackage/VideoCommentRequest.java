package com.example.requestpackage;

public class VideoCommentRequest {
    private String content;
    private Integer parentId;
    private int videoId;
    private int user;

    public VideoCommentRequest(String content, Integer parentId, int videoId, int user) {
        this.content = content;
        this.parentId = parentId;
        this.videoId = videoId;
        this.user = user;
    }
}
