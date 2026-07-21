package com.marketplace.backend.repository;

import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
    List<ListingImage> findByListingOrderBySortOrderAsc(Listing listing);
    List<ListingImage> findByListingIdOrderBySortOrderAsc(Long listingId);
}
