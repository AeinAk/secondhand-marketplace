package com.marketplace.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login requests.
 * <p>
 * Encapsulates the credentials required for user authentication.
 * Both username and password are mandatory fields and are validated
 * to ensure they are not blank. This DTO is used by the
 * {@code /api/auth/login} endpoint to accept login credentials.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class LoginRequest {

    /**
     * The username of the user attempting to log in.
     * <p>
     * Must not be blank. This field is required for authentication.
     * </p>
     */
    @NotBlank
    private String username;

    /**
     * The password of the user attempting to log in.
     * <p>
     * Must not be blank. This field is required for authentication.
     * </p>
     */
    @NotBlank
    private String password;

    /**
     * Returns the username.
     *
     * @return the username string
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
     * Returns the password.
     *
     * @return the password string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}