package com.marketplace.backend.repository;

import com.marketplace.backend.entity.Favorite;
import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserAndListing(User user, Listing listing);
    boolean existsByUserIdAndListingId(Long userId, Long listingId);
}
