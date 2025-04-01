package com.example.requestpackage;

public class ArticleCommentRequest {
    private String content;
    private Integer parentId;
    private int articleId;
    private int user;

    public ArticleCommentRequest(String content, Integer parentId, int articleId, int user) {
        this.content = content;
        this.parentId = parentId;
        this.articleId = articleId;
        this.user = user;
    }
}
