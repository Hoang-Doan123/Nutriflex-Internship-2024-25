package com.example.requestpackage;

public class ArticleBookmarkRequest {
    private int userId;
    private int articleId;

    public ArticleBookmarkRequest(int userId, int articleId) {
        this.userId = userId;
        this.articleId = articleId;
    }
}
