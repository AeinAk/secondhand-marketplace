package com.marketplace.backend.dto;

import com.marketplace.backend.entity.UserRole;

/**
 * Data Transfer Object for user account information.
 * <p>
 * Represents a user's public profile data, including identification, contact details,
 * role, and account status. This DTO is used for transferring user information
 * between the backend and frontend, particularly in administrative contexts
 * where user management is required.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class UserDto {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The username of the user.
     * <p>
     * This is the unique login name for the user account.
     * </p>
     */
    private String username;

    /**
     * The email address of the user.
     * <p>
     * Must be a valid email format and is used for communication and account recovery.
     * </p>
     */
    private String email;

    /**
     * The full name of the user (optional).
     * <p>
     * May be null or empty if not provided during registration.
     * </p>
     */
    private String fullName;

    /**
     * The phone number of the user (optional).
     * <p>
     * May be null or empty if not provided during registration.
     * </p>
     */
    private String phone;

    /**
     * The role assigned to the user.
     * <p>
     * Possible values: {@code USER} or {@code ADMIN}. Determines the user's
     * permissions and access level within the system.
     * </p>
     */
    private UserRole role;

    /**
     * Indicates whether the user account is blocked.
     * <p>
     * Blocked users cannot log in or perform any operations. This flag is
     * managed by administrators.
     * </p>
     */
    private boolean blocked;

    /**
     * Returns the user's unique identifier.
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param id the user ID to set
     */
    public void setId(Long id) {
        this.id = id;
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
     * Returns the email address.
     *
     * @return the email
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
     * Returns whether the user account is blocked.
     *
     * @return {@code true} if the account is blocked, {@code false} otherwise
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Sets the blocked status of the user account.
     *
     * @param blocked {@code true} to block the account, {@code false} to unblock
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}