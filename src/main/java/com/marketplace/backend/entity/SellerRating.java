package com.marketplace.backend.entity;

import jakarta.persistence.Column;
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
 * Entity representing a rating given to a seller by a buyer.
 * <p>
 * This entity captures feedback from buyers about their experience with a seller
 * based on a specific transaction (listing). Each rating includes a numerical
 * score (1-5) and an optional text review. The unique constraint on
 * {@code (reviewer_id, listing_id)} ensures that a buyer can rate a seller only
 * once per listing, preventing duplicate ratings.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "seller_ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "listing_id"}))
public class SellerRating {

    /**
     * The unique identifier of the rating record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The seller being rated.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    /**
     * The buyer (reviewer) who submitted the rating.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    /**
     * The listing associated with this rating.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * The combination of reviewer and listing must be unique.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    /**
     * The numerical rating score (1-5).
     * <p>
     * A higher score indicates better satisfaction with the seller.
     * This field is required and must be between 1 and 5.
     * </p>
     */
    @Column(nullable = false)
    private int rating;

    /**
     * Optional textual review provided by the buyer.
     * <p>
     * Provides detailed feedback about the seller. Maximum length is 1000 characters.
     * May be null or empty if no review text was given.
     * </p>
     */
    @Column(length = 1000)
    private String reviewText;

    /**
     * The timestamp when the rating was created.
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
     * Returns the unique identifier of the rating.
     *
     * @return the rating ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the rating.
     *
     * @param id the rating ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the seller being rated.
     *
     * @return the seller {@link User} entity
     */
    public User getSeller() {
        return seller;
    }

    /**
     * Sets the seller being rated.
     *
     * @param seller the seller to set
     */
    public void setSeller(User seller) {
        this.seller = seller;
    }

    /**
     * Returns the buyer who submitted the rating.
     *
     * @return the reviewer {@link User} entity
     */
    public User getReviewer() {
        return reviewer;
    }

    /**
     * Sets the buyer who submitted the rating.
     *
     * @param reviewer the reviewer to set
     */
    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    /**
     * Returns the listing associated with this rating.
     *
     * @return the {@link Listing} entity
     */
    public Listing getListing() {
        return listing;
    }

    /**
     * Sets the listing associated with this rating.
     *
     * @param listing the listing to set
     */
    public void setListing(Listing listing) {
        this.listing = listing;
    }

    /**
     * Returns the numerical rating score.
     *
     * @return the rating (1-5)
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the numerical rating score.
     *
     * @param rating the rating to set (1-5)
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Returns the optional review text.
     *
     * @return the review text, or null if not provided
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Sets the optional review text.
     *
     * @param reviewText the review text to set
     */
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    /**
     * Returns the timestamp when the rating was created.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the rating was created.
     *
     * @param createdAt the creation time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}