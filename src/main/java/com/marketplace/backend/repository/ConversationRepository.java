package com.marketplace.backend.repository;

import com.marketplace.backend.entity.Conversation;
import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByListingAndBuyer(Listing listing, User buyer);
    List<Conversation> findByBuyerIdOrSellerIdOrderByCreatedAtDesc(Long buyerId, Long sellerId);
}
