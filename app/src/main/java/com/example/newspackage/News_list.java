package com.example.newspackage;

import android.widget.ImageView;

public class News_list {
    private Integer id;
    private String title;
    private String content;
    private ImageView contentImage;

    public News_list(Integer id, String title, String content){
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Integer getId(){
        return id;
    }
    public void setId(){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }
    public void setTitle(){
        this.title = title;
    }

    public String getContent(){
        return content;
    }
    public void setContent(){
        this.content = content;
    }
}
