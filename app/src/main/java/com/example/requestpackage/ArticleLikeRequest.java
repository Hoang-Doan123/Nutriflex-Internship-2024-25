package com.example.requestpackage;

public class ArticleLikeRequest {
    private int userId;
    private int articleId;

    public ArticleLikeRequest(int userId, int articleId) {
        this.userId = userId;
        this.articleId = articleId;
    }
}
