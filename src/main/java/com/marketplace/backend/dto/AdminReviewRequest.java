package com.marketplace.backend.dto;

import com.marketplace.backend.entity.ReviewDecision;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for admin review requests.
 * <p>
 * Encapsulates the data required for an administrator to review a pending listing.
 * Contains the decision (APPROVED or REJECTED) and an optional comment explaining
 * the decision, particularly useful when rejecting a listing.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class AdminReviewRequest {

    /**
     * The administrative decision on the listing review.
     * <p>
     * Must be either {@code APPROVED} or {@code REJECTED}. This field is required.
     * </p>
     */
    @NotNull
    private ReviewDecision decision;

    /**
     * An optional comment providing additional context or reasoning for the decision.
     * <p>
     * Typically used to explain why a listing was rejected, or to provide feedback
     * to the seller. This field is not required and can be null or empty.
     * </p>
     */
    private String comment;

    /**
     * Returns the administrative decision.
     *
     * @return the {@link ReviewDecision} (APPROVED or REJECTED)
     */
    public ReviewDecision getDecision() {
        return decision;
    }

    /**
     * Sets the administrative decision.
     *
     * @param decision the decision to set (APPROVED or REJECTED)
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
     * @param comment the comment to set (may be null or empty)
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}