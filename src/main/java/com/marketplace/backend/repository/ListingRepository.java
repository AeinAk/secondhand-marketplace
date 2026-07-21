package com.marketplace.backend.repository;

import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.ListingStatus;
import com.marketplace.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    List<Listing> findByStatus(ListingStatus status);
    List<Listing> findBySeller(User seller);
    List<Listing> findBySellerId(Long sellerId);
}
