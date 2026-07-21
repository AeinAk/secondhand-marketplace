package com.marketplace.backend.repository;

import com.marketplace.backend.entity.SellerRating;
import com.marketplace.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {
    List<SellerRating> findBySeller(User seller);
    List<SellerRating> findBySellerId(Long sellerId);
    Optional<SellerRating> findByReviewerIdAndListingId(Long reviewerId, Long listingId);
}
