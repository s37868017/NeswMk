package com.newsMk.dto;

public class AuthResponse {
    private String token;
    private String role;
    private Long id;

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}