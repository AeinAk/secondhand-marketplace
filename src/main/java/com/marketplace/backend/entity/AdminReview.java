package com.marketplace.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entity representing an administrative review of a listing.
 * <p>
 * Records the decision made by an administrator when reviewing a pending listing.
 * Each review is associated with a specific listing and admin user, and includes
 * the decision (APPROVED or REJECTED), an optional comment, and the timestamp
 * of when the review was conducted. This entity provides an audit trail for
 * all listing review activities.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "admin_reviews")
public class AdminReview {

    /**
     * The unique identifier of the admin review record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The listing that was reviewed.
     * <p>
     * This is a many-to-one relationship, as a listing can have multiple
     * review records (although typically only one is needed). The association
     * is loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    /**
     * The administrator who performed the review.
     * <p>
     * This is a many-to-one relationship, as an admin can review many listings.
     * The association is loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    /**
     * The decision made by the administrator.
     * <p>
     * Can be either {@code APPROVED} or {@code REJECTED}. Stored as a string
     * in the database using the {@code ReviewDecision} enum.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewDecision decision;

    /**
     * An optional comment providing reasoning for the decision.
     * <p>
     * Typically used to explain why a listing was rejected, or to provide
     * feedback to the seller. Maximum length is 500 characters.
     * </p>
     */
    @Column(length = 500)
    private String comment;

    /**
     * The timestamp when the review was conducted.
     * <p>
     * Automatically set to the current time before the entity is persisted.
     * </p>
     */
    @Column(nullable = false)
    private LocalDateTime reviewedAt;

    /**
     * Initializes the {@code reviewedAt} timestamp before the entity is persisted.
     * <p>
     * This method is automatically called by JPA before the entity is saved
     * to the database, ensuring that the review timestamp is always set.
     * </p>
     */
    @PrePersist
    void onCreate() {
        reviewedAt = LocalDateTime.now();
    }

    /**
     * Returns the unique identifier of the admin review.
     *
     * @return the review ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the admin review.
     *
     * @param id the review ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the listing that was reviewed.
     *
     * @return the {@link Listing} entity
     */
    public Listing getListing() {
        return listing;
    }

    /**
     * Sets the listing that was reviewed.
     *
     * @param listing the listing to set
     */
    public void setListing(Listing listing) {
        this.listing = listing;
    }

    /**
     * Returns the administrator who performed the review.
     *
     * @return the admin {@link User} entity
     */
    public User getAdmin() {
        return admin;
    }

    /**
     * Sets the administrator who performed the review.
     *
     * @param admin the admin user to set
     */
    public void setAdmin(User admin) {
        this.admin = admin;
    }

    /**
     * Returns the review decision.
     *
     * @return the {@link ReviewDecision} (APPROVED or REJECTED)
     */
    public ReviewDecision getDecision() {
        return decision;
    }

    /**
     * Sets the review decision.
     *
     * @param decision the decision to set
     */
    public void setDecision(ReviewDecision decision) {
        this.decision = decision;
    }

    /**
     * Returns the optional comment.
     *
     * @return the comment, or null if none was provided
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the optional comment.
     *
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns the timestamp when the review was conducted.
     *
     * @return the review timestamp
     */
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    /**
     * Sets the timestamp when the review was conducted.
     *
     * @param reviewedAt the timestamp to set
     */
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}