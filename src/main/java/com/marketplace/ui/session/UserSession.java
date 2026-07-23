package com.marketplace.ui.session;

import com.marketplace.backend.dto.AuthResponse;
import com.marketplace.backend.entity.UserRole;

import org.springframework.stereotype.Component;

/**
 * Manages the current user's session state within the client application.
 * Holds authentication credentials, user identity, and role information.
 * Acts as a single source of truth for the logged-in user's data.
 */
@Component
public class UserSession {

    /** The JWT authentication token used for authorized API requests. */
    private String token;

    /** The unique identifier of the logged-in user. */
    private Long userId;

    /** The username of the logged-in user. */
    private String username;

    /** The role (e.g., USER, ADMIN) assigned to the logged-in user. */
    private UserRole role;

    /**
     * Applies the authentication response by storing the token and user details
     * in the session.
     *
     * @param response the authentication response containing token and user data
     */
    public void applyAuth(AuthResponse response) {
        this.token = response.getToken();
        this.userId = response.getUserId();
        this.username = response.getUsername();
        this.role = response.getRole();
    }

    /** Clears all session data, effectively logging out the current user. */
    public void clear() {
        token = null;
        userId = null;
        username = null;
        role = null;
    }

    /**
     * Checks whether a user is currently logged in based on the presence of a
     * valid token.
     *
     * @return true if the user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    /**
     * Checks whether the currently logged-in user has administrative privileges.
     *
     * @return true if the user is an administrator, false otherwise
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Returns the JWT authentication token.
     *
     * @return the current token, or null if not logged in
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the ID of the currently logged-in user.
     *
     * @return the user ID, or null if not logged in
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Returns the username of the currently logged-in user.
     *
     * @return the username, or null if not logged in
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the role of the currently logged-in user.
     *
     * @return the user role, or null if not logged in
     */
    public UserRole getRole() {
        return role;
    }
}