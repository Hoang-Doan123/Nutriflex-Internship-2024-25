package com.example.videopackage.videoComment;

public class VideoComment {
    private int id;
    private String content;
    private int user;
    private String username;
    private int videoId;
    private String createdTime;
    private int likeNum;

    public VideoComment(
            int id,
            String content,
            int user,
            String username,
            int videoId,
            String createdTime,
            int likeNum
    ) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.videoId = videoId;
        this.createdTime = createdTime;
        this.likeNum = likeNum;
    }

    public int getId() {
        return id;
    }

    public int getVideoId() {
        return videoId;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public int getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }
}
