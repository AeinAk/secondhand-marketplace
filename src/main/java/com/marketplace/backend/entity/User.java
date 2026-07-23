package com.marketplace.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Entity representing a user account in the marketplace.
 * <p>
 * This is the core entity for user management, storing authentication credentials,
 * profile information, and account status. Each user has a unique username and email
 * address, and can be either a regular user (USER) or an administrator (ADMIN).
 * Users can be blocked by administrators to prevent access to the system.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    /**
     * The unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The username used for authentication.
     * <p>
     * Must be unique across all users, cannot be null, and has a maximum
     * length of 50 characters.
     * </p>
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * The email address of the user.
     * <p>
     * Must be unique across all users, cannot be null, and has a maximum
     * length of 100 characters. Used for communication and account recovery.
     * </p>
     */
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * The hashed password of the user.
     * <p>
     * Stored as a BCrypt hash. Cannot be null.
     * </p>
     */
    @Column(nullable = false)
    private String password;

    /**
     * The full name of the user (optional).
     * <p>
     * May be null or empty if not provided during registration.
     * Maximum length is 100 characters.
     * </p>
     */
    @Column(length = 100)
    private String fullName;

    /**
     * The phone number of the user (optional).
     * <p>
     * May be null or empty if not provided during registration.
     * Maximum length is 20 characters.
     * </p>
     */
    @Column(length = 20)
    private String phone;

    /**
     * The role assigned to the user.
     * <p>
     * Possible values: {@code USER} or {@code ADMIN}. Determines the user's
     * permissions and access level within the system. Defaults to USER.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    /**
     * Indicates whether the user account is blocked.
     * <p>
     * Blocked users cannot log in or perform any operations. Defaults to false.
     * </p>
     */
    @Column(nullable = false)
    private boolean blocked = false;

    /**
     * The timestamp when the user account was created.
     * <p>
     * Automatically set to the current time before the entity is persisted.
     * </p>
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Initializes the {@code createdAt} timestamp before the entity is persisted.
     * <p>
     * This method is automatically called by JPA before the entity is saved
     * to the database, ensuring that the creation timestamp is always set.
     * </p>
     */
    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Returns the unique identifier of the user.
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
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
     * Returns the hashed password.
     *
     * @return the password hash
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the hashed password.
     *
     * @param password the password hash to set
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

    /**
     * Returns the timestamp when the user account was created.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the user account was created.
     *
     * @param createdAt the creation time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}