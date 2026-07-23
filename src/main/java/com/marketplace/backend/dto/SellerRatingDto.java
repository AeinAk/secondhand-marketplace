package com.marketplace.backend.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for seller rating details.
 * <p>
 * Represents a rating submitted by a reviewer for a seller based on a specific listing.
 * Contains information about the seller, reviewer, associated listing, rating score,
 * optional review text, and creation timestamp. This DTO is used to transfer rating
 * data between the backend and frontend, including both individual ratings and lists
 * of ratings for a seller.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class SellerRatingDto {

    /**
     * The unique identifier of the rating.
     */
    private Long id;

    /**
     * The unique identifier of the seller being rated.
     */
    private Long sellerId;

    /**
     * The username of the seller being rated.
     */
    private String sellerUsername;

    /**
     * The unique identifier of the user who submitted the rating.
     */
    private Long reviewerId;

    /**
     * The username of the user who submitted the rating.
     */
    private String reviewerUsername;

    /**
     * The unique identifier of the listing associated with this rating.
     */
    private Long listingId;

    /**
     * The title of the listing associated with this rating.
     */
    private String listingTitle;

    /**
     * The numerical rating score (1-5) given to the seller.
     */
    private int rating;

    /**
     * The optional textual review provided by the reviewer.
     * <p>
     * May be null or empty if no written feedback was given.
     * </p>
     */
    private String reviewText;

    /**
     * The timestamp when the rating was created.
     */
    private LocalDateTime createdAt;

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
     * Returns the seller's user ID.
     *
     * @return the seller ID
     */
    public Long getSellerId() {
        return sellerId;
    }

    /**
     * Sets the seller's user ID.
     *
     * @param sellerId the seller ID to set
     */
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * Returns the seller's username.
     *
     * @return the seller username
     */
    public String getSellerUsername() {
        return sellerUsername;
    }

    /**
     * Sets the seller's username.
     *
     * @param sellerUsername the seller username to set
     */
    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    /**
     * Returns the reviewer's user ID.
     *
     * @return the reviewer ID
     */
    public Long getReviewerId() {
        return reviewerId;
    }

    /**
     * Sets the reviewer's user ID.
     *
     * @param reviewerId the reviewer ID to set
     */
    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    /**
     * Returns the reviewer's username.
     *
     * @return the reviewer username
     */
    public String getReviewerUsername() {
        return reviewerUsername;
    }

    /**
     * Sets the reviewer's username.
     *
     * @param reviewerUsername the reviewer username to set
     */
    public void setReviewerUsername(String reviewerUsername) {
        this.reviewerUsername = reviewerUsername;
    }

    /**
     * Returns the listing ID associated with this rating.
     *
     * @return the listing ID
     */
    public Long getListingId() {
        return listingId;
    }

    /**
     * Sets the listing ID associated with this rating.
     *
     * @param listingId the listing ID to set
     */
    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    /**
     * Returns the title of the associated listing.
     *
     * @return the listing title
     */
    public String getListingTitle() {
        return listingTitle;
    }

    /**
     * Sets the title of the associated listing.
     *
     * @param listingTitle the listing title to set
     */
    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
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