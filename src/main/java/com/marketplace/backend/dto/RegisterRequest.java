package com.marketplace.backend.dto;

import com.marketplace.backend.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 * <p>
 * Encapsulates the required information for creating a new user account.
 * All fields are validated to ensure data integrity and security.
 * The username and email must be unique across the system, and the password
 * must meet minimum length requirements for security.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class RegisterRequest {

    /**
     * The desired username for the new account.
     * <p>
     * Must be between 3 and 50 characters long, and must not be blank.
     * This field is required and must be unique across all users.
     * </p>
     */
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    /**
     * The email address of the new user.
     * <p>
     * Must be a valid email format and must not be blank.
     * This field is required and must be unique across all users.
     * </p>
     */
    @NotBlank
    @Email
    private String email;

    /**
     * The password for the new account.
     * <p>
     * Must be between 6 and 100 characters long, and must not be blank.
     * This field is required and will be securely hashed before storage.
     * </p>
     */
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    /**
     * The full name of the user (optional).
     * <p>
     * This field is not required and can be left empty.
     * </p>
     */
    private String fullName;

    /**
     * The phone number of the user (optional).
     * <p>
     * This field is not required and can be left empty.
     * </p>
     */
    private String phone;

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
     * Returns the email address.
     *
     * @return the email string
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
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

    /**
     * Returns the full name.
     *
     * @return the full name, or null if not set
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name.
     *
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Returns the phone number.
     *
     * @return the phone number, or null if not set
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number.
     *
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
}