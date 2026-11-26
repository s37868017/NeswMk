package com.newsMk.dto;

import java.util.Set;

public class UserResponse {
    private Long id;
    private String login;
    private String email;
    private Set<String> roles;
    private Long articlesCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Long getArticlesCount() {
        return articlesCount;
    }
}