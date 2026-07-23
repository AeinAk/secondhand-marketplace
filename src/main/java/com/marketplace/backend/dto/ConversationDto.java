package com.marketplace.backend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for conversation details.
 * <p>
 * Represents a conversation between a buyer and a seller related to a specific listing.
 * Contains information about both participants, the associated listing, the
 * conversation's creation time, and the list of messages exchanged.
 * This DTO is used when retrieving conversations and their message history.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class ConversationDto {

    /**
     * The timestamp of the most recent message in the conversation.
     * <p>
     * Used to display the latest activity time in the conversation list.
     * May be {@code null} if no messages have been sent yet.
     * </p>
     */
    private LocalDateTime lastMessageTime;

    /**
     * The unique identifier of the conversation.
     */
    private Long id;

    /**
     * The unique identifier of the listing associated with this conversation.
     */
    private Long listingId;

    /**
     * The title of the associated listing.
     */
    private String listingTitle;

    /**
     * The unique identifier of the buyer participating in this conversation.
     */
    private Long buyerId;

    /**
     * The username of the buyer participating in this conversation.
     */
    private String buyerUsername;

    /**
     * The unique identifier of the seller participating in this conversation.
     */
    private Long sellerId;

    /**
     * The username of the seller participating in this conversation.
     */
    private String sellerUsername;

    /**
     * The timestamp when the conversation was initially created.
     */
    private LocalDateTime createdAt;

    /**
     * The list of messages exchanged in this conversation.
     * <p>
     * Messages are ordered by sent time (oldest first).
     * May be empty if no messages have been sent yet.
     * </p>
     */
    private List<MessageDto> messages = new ArrayList<>();

    /**
     * Returns the unique identifier of the conversation.
     *
     * @return the conversation ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the timestamp of the most recent message.
     *
     * @return the last message time, or {@code null} if no messages exist
     */
    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    /**
     * Sets the timestamp of the most recent message.
     *
     * @param lastMessageTime the last message time to set
     */
    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
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
     * Returns the listing ID associated with this conversation.
     *
     * @return the listing ID
     */
    public Long getListingId() {
        return listingId;
    }

    /**
     * Sets the listing ID associated with this conversation.
     *
     * @param listingId the listing ID to set
     */
    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    /**
     * Returns the title of the associated listing.
     *
     * @return the listing title
     */
    public String getListingTitle() {
        return listingTitle;
    }

    /**
     * Sets the title of the associated listing.
     *
     * @param listingTitle the listing title to set
     */
    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
    }

    /**
     * Returns the buyer's user ID.
     *
     * @return the buyer ID
     */
    public Long getBuyerId() {
        return buyerId;
    }

    /**
     * Sets the buyer's user ID.
     *
     * @param buyerId the buyer ID to set
     */
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    /**
     * Returns the buyer's username.
     *
     * @return the buyer username
     */
    public String getBuyerUsername() {
        return buyerUsername;
    }

    /**
     * Sets the buyer's username.
     *
     * @param buyerUsername the buyer username to set
     */
    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    /**
     * Returns the seller's user ID.
     *
     * @return the seller ID
     */
    public Long getSellerId() {
        return sellerId;
    }

    /**
     * Sets the seller's user ID.
     *
     * @param sellerId the seller ID to set
     */
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * Returns the seller's username.
     *
     * @return the seller username
     */
    public String getSellerUsername() {
        return sellerUsername;
    }

    /**
     * Sets the seller's username.
     *
     * @param sellerUsername the seller username to set
     */
    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
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

    /**
     * Returns the list of messages in this conversation.
     *
     * @return the list of {@link MessageDto} objects, ordered by sent time (oldest first)
     */
    public List<MessageDto> getMessages() {
        return messages;
    }

    /**
     * Sets the list of messages in this conversation.
     *
     * @param messages the list of {@link MessageDto} objects to set
     */
    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }
}