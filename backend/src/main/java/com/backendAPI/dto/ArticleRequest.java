package com.backendAPI.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArticleRequest {
    @NotBlank
    private String title;
    private String intro;
    private String text;

    public String getTitle() {
        return title;
    }
    public String getIntro() {
        return intro;
    }
    public String getText() {
        return text;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setIntro(String intro) {
        this.intro = intro;
    }
    public void setText(String text) {
        this.text = text;
    }
}