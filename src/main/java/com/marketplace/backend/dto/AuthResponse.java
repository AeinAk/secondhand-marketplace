package com.marketplace.backend.dto;

import com.marketplace.backend.entity.UserRole;

public class AuthResponse {

    private String token;
    private Long userId;
    private String username;
    private UserRole role;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long userId, String username, UserRole role, String message) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
