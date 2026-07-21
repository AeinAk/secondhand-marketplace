package com.marketplace.backend.dto;

import com.marketplace.backend.entity.ReviewDecision;
import jakarta.validation.constraints.NotNull;

public class AdminReviewRequest {

    @NotNull
    private ReviewDecision decision;
    private String comment;

    public ReviewDecision getDecision() {
        return decision;
    }

    public void setDecision(ReviewDecision decision) {
        this.decision = decision;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
