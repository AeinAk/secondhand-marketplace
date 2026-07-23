package com.marketplace.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for individual messages within a conversation.
 * <p>
 * Represents a single message exchanged between users in a conversation.
 * Contains information about the sender, content, timestamp, and read status.
 * This DTO is used for both sending and retrieving messages in the chat system.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class MessageDto {

    /**
     * The unique identifier of the message.
     */
    private Long id;

    /**
     * The unique identifier of the conversation this message belongs to.
     */
    private Long conversationId;

    /**
     * The unique identifier of the user who sent the message.
     */
    private Long senderId;

    /**
     * The username of the user who sent the message.
     */
    private String senderUsername;

    /**
     * The text content of the message.
     * <p>
     * Must not be blank and cannot exceed 2000 characters.
     * </p>
     */
    @NotBlank
    @Size(max = 2000)
    private String content;

    /**
     * Indicates whether the message has been read by the recipient.
     * <p>
     * This flag is used for implementing read receipts.
     * </p>
     */
    private boolean readFlag;

    /**
     * The timestamp when the message was sent.
     */
    private LocalDateTime sentAt;

    /**
     * Returns the unique identifier of the message.
     *
     * @return the message ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the message.
     *
     * @param id the message ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the conversation ID this message belongs to.
     *
     * @return the conversation ID
     */
    public Long getConversationId() {
        return conversationId;
    }

    /**
     * Sets the conversation ID this message belongs to.
     *
     * @param conversationId the conversation ID to set
     */
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * Returns the sender's user ID.
     *
     * @return the sender ID
     */
    public Long getSenderId() {
        return senderId;
    }

    /**
     * Sets the sender's user ID.
     *
     * @param senderId the sender ID to set
     */
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    /**
     * Returns the sender's username.
     *
     * @return the sender username
     */
    public String getSenderUsername() {
        return senderUsername;
    }

    /**
     * Sets the sender's username.
     *
     * @param senderUsername the sender username to set
     */
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    /**
     * Returns the message content.
     *
     * @return the content string
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the message content.
     *
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns whether the message has been read.
     *
     * @return {@code true} if the message is read, {@code false} otherwise
     */
    public boolean isReadFlag() {
        return readFlag;
    }

    /**
     * Sets whether the message has been read.
     *
     * @param readFlag {@code true} to mark as read, {@code false} otherwise
     */
    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }

    /**
     * Returns the timestamp when the message was sent.
     *
     * @return the sent time
     */
    public LocalDateTime getSentAt() {
        return sentAt;
    }

    /**
     * Sets the timestamp when the message was sent.
     *
     * @param sentAt the sent time to set
     */
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}