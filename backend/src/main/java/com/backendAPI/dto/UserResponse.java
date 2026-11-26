package com.backendAPI.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String login;
    private String email;
    private Set<String> roles;
    private Long articlesCount;

    public Long getId() {
        return id;
    }
    public String getLogin() {
        return login;
    }
    public String getEmail() {
        return email;
    }
    public Set<String> getRoles() {
        return roles;
    }
    public Long getArticlesCount() {
        return articlesCount;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    public void setArticlesCount(Long articlesCount) {
        this.articlesCount = articlesCount;
    }
}
