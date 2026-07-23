package com.marketplace.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Entity representing a user's favorite listing.
 * <p>
 * This entity tracks which listings a user has marked as favorites for quick
 * access and reference. A user can favorite a listing only once, enforced by
 * the unique constraint on the combination of user and listing. This allows
 * users to build a personalized collection of interesting items.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "listing_id"}))
public class Favorite {

    /**
     * The unique identifier of the favorite record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who has marked the listing as a favorite.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * Each favorite belongs to exactly one user.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The listing that has been marked as a favorite.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * Each favorite points to exactly one listing.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    /**
     * The timestamp when the favorite was created.
     * <p>
     * Automatically set to the current time before the entity is persisted.
     * Used to display the order in which favorites were added.
     * </p>
     */
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
     * Returns the unique identifier of the favorite record.
     *
     * @return the favorite ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the favorite record.
     *
     * @param id the favorite ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user who owns this favorite.
     *
     * @return the {@link User} entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who owns this favorite.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the listing that is marked as a favorite.
     *
     * @return the {@link Listing} entity
     */
    public Listing getListing() {
        return listing;
    }

    /**
     * Sets the listing that is marked as a favorite.
     *
     * @param listing the listing to set
     */
    public void setListing(Listing listing) {
        this.listing = listing;
    }

    /**
     * Returns the timestamp when the favorite was created.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the favorite was created.
     *
     * @param createdAt the creation time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}