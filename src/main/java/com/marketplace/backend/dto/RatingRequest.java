package com.marketplace.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for submitting a seller rating.
 * <p>
 * Encapsulates the data required to rate a seller based on a specific listing.
 * The rating must be between 1 and 5, and the listing ID is mandatory.
 * An optional review text can be provided to give detailed feedback.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class RatingRequest {

    /**
     * The unique identifier of the listing for which the seller is being rated.
     * <p>
     * This field is mandatory and must not be null.
     * The seller is determined from the listing's owner.
     * </p>
     */
    @NotNull
    private Long listingId;

    /**
     * The numerical rating score given to the seller.
     * <p>
     * Must be between 1 and 5 inclusive. A higher score indicates better
     * satisfaction with the transaction and seller communication.
     * </p>
     */
    @Min(1)
    @Max(5)
    private int rating;

    /**
     * An optional textual review providing additional feedback about the seller.
     * <p>
     * Cannot exceed 1000 characters. May be null or empty if the user
     * chooses not to provide a written review.
     * </p>
     */
    @Size(max = 1000)
    private String reviewText;

    /**
     * Returns the listing ID.
     *
     * @return the listing ID
     */
    public Long getListingId() {
        return listingId;
    }

    /**
     * Sets the listing ID.
     *
     * @param listingId the listing ID to set
     */
    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    /**
     * Returns the rating score.
     *
     * @return the rating (1-5)
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating score.
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
}