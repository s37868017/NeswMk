package com.newsMk.dto;

public class ArticleRequest {
    private String title;
    private String intro;
    private String text;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}