package com.example.newspackage;

public class SmallNews {
    private int id;
    private String title;
    private String date;
    private String description;
    private String content;
    private String urlToImage;
    private int commentNum;
    private int likeNum;
    private int bookmarkNum;


    public SmallNews(Integer id, String title, String urlToImage){
        this.id = id;
        this.title = title;
        this.urlToImage = urlToImage;
    }

    public SmallNews() {}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getBookmarkNum() {
        return bookmarkNum;
    }

    public void setBookmarkNum(int bookmarkNum) {
        this.bookmarkNum = bookmarkNum;
    }
}
