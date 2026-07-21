package com.marketplace.ui.session;

import com.marketplace.backend.dto.AuthResponse;
import com.marketplace.backend.entity.UserRole;

import org.springframework.stereotype.Component;

@Component
public class UserSession {

    private String token;
    private Long userId;
    private String username;
    private UserRole role;

    public void applyAuth(AuthResponse response) {
        this.token = response.getToken();
        this.userId = response.getUserId();
        this.username = response.getUsername();
        this.role = response.getRole();
    }

    public void clear() {
        token = null;
        userId = null;
        username = null;
        role = null;
    }

    public boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }
}
