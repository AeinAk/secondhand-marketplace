package com.marketplace.backend.repository;

import com.marketplace.backend.entity.AdminReview;
import com.marketplace.backend.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminReviewRepository extends JpaRepository<AdminReview, Long> {
    List<AdminReview> findByListing(Listing listing);
}
