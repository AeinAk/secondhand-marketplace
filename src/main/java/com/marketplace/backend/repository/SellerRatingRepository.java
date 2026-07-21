package com.marketplace.backend.repository;

import com.marketplace.backend.entity.SellerRating;
import com.marketplace.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerRatingRepository extends JpaRepository<SellerRating, Long> {
    List<SellerRating> findBySeller(User seller);
    List<SellerRating> findBySellerId(Long sellerId);
    Optional<SellerRating> findByReviewerIdAndListingId(Long reviewerId, Long listingId);
    @Query("SELECT AVG(r.rating) FROM SellerRating r WHERE r.seller.id = :sellerId")
    Double findAverageRatingBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(r) FROM SellerRating r WHERE r.seller.id = :sellerId")
    Long countRatingsBySellerId(@Param("sellerId") Long sellerId);
}
