package com.marketplace.backend.dto;

import com.marketplace.backend.entity.UserRole;

/**
 * Response payload for authentication operations (login and registration).
 * <p>
 * Contains the JWT token for subsequent authenticated requests, along with
 * user identification details and a descriptive message about the operation
 * result. This DTO is returned by both {@code /api/auth/login} and
 * {@code /api/auth/register} endpoints.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class AuthResponse {

    /**
     * The JWT token to be used for authenticated requests.
     * <p>
     * Must be included in the {@code Authorization} header as
     * {@code Bearer <token>} for all protected endpoints.
     * </p>
     */
    private String token;

    /**
     * The unique identifier of the authenticated user.
     */
    private Long userId;

    /**
     * The username of the authenticated user.
     */
    private String username;

    /**
     * The role of the authenticated user (USER or ADMIN).
     */
    private UserRole role;

    /**
     * A descriptive message about the authentication operation result.
     * <p>
     * Examples: "Login successful", "Registration successful", or error messages.
     * </p>
     */
    private String message;

    /**
     * Default constructor for deserialization.
     */
    public AuthResponse() {
    }

    /**
     * Constructs an AuthResponse with all fields.
     *
     * @param token    the JWT token for the authenticated session
     * @param userId   the user's unique identifier
     * @param username the user's username
     * @param role     the user's role (USER or ADMIN)
     * @param message  a descriptive message about the operation
     */
    public AuthResponse(String token, Long userId, String username, UserRole role, String message) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    /**
     * Returns the JWT token.
     *
     * @return the token string
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the JWT token.
     *
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Returns the user's unique identifier.
     *
     * @return the user ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param userId the user ID to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's role.
     *
     * @return the {@link UserRole} (USER or ADMIN)
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param role the role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Returns the descriptive message.
     *
     * @return the message string
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the descriptive message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}