package com.marketplace.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Entity representing a conversation between a buyer and a seller about a specific listing.
 * <p>
 * A conversation is created when a buyer expresses interest in a listing by sending
 * a message. It serves as a container for all messages exchanged between the two
 * parties regarding that particular listing. Each conversation is uniquely identified
 * by the combination of listing and buyer, ensuring that only one conversation exists
 * per buyer-listing pair.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "conversations", uniqueConstraints = @UniqueConstraint(columnNames = {"listing_id", "buyer_id"}))
public class Conversation {

    /**
     * The unique identifier of the conversation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The listing that this conversation relates to.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    /**
     * The buyer initiating or participating in the conversation.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    /**
     * The seller participating in the conversation.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    /**
     * The timestamp when the conversation was created.
     * <p>
     * Automatically set to the current time before the entity is persisted.
     * </p>
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Initializes the {@code createdAt} timestamp before the entity is persisted.
     * <p>
     * This method is automatically called by JPA before the entity is saved
     * to the database, ensuring that the creation timestamp is always set.
     * </p>
     */
    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Returns the unique identifier of the conversation.
     *
     * @return the conversation ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the conversation.
     *
     * @param id the conversation ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the listing associated with this conversation.
     *
     * @return the {@link Listing} entity
     */
    public Listing getListing() {
        return listing;
    }

    /**
     * Sets the listing associated with this conversation.
     *
     * @param listing the listing to set
     */
    public void setListing(Listing listing) {
        this.listing = listing;
    }

    /**
     * Returns the buyer participating in this conversation.
     *
     * @return the buyer {@link User} entity
     */
    public User getBuyer() {
        return buyer;
    }

    /**
     * Sets the buyer participating in this conversation.
     *
     * @param buyer the buyer to set
     */
    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    /**
     * Returns the seller participating in this conversation.
     *
     * @return the seller {@link User} entity
     */
    public User getSeller() {
        return seller;
    }

    /**
     * Sets the seller participating in this conversation.
     *
     * @param seller the seller to set
     */
    public void setSeller(User seller) {
        this.seller = seller;
    }

    /**
     * Returns the creation timestamp of the conversation.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the conversation.
     *
     * @param createdAt the creation time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}