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

import java.time.LocalDateTime;

/**
 * Entity representing a message within a conversation.
 * <p>
 * A message is the basic unit of communication in the chat system. It belongs
 * to a specific conversation and is sent by a user. Each message contains text
 * content, a read status flag, and a timestamp. Messages are stored in order
 * of creation and displayed chronologically within their conversation.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "messages")
public class Message {

    /**
     * The unique identifier of the message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The conversation that this message belongs to.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /**
     * The user who sent the message.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * The text content of the message.
     * <p>
     * Cannot be null and has a maximum length of 2000 characters.
     * </p>
     */
    @Column(nullable = false, length = 2000)
    private String content;

    /**
     * Indicates whether the message has been read by the recipient.
     * <p>
     * Defaults to {@code false} when the message is created, and can be updated
     * when the recipient views the conversation.
     * </p>
     */
    @Column(nullable = false)
    private boolean readFlag = false;

    /**
     * The timestamp when the message was sent.
     * <p>
     * Automatically set to the current time before the entity is persisted.
     * </p>
     */
    @Column(nullable = false)
    private LocalDateTime sentAt;

    /**
     * Initializes the {@code sentAt} timestamp before the entity is persisted.
     * <p>
     * This method is automatically called by JPA before the entity is saved
     * to the database, ensuring that the send timestamp is always set.
     * </p>
     */
    @PrePersist
    void onCreate() {
        sentAt = LocalDateTime.now();
    }

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
     * Returns the conversation that this message belongs to.
     *
     * @return the {@link Conversation} entity
     */
    public Conversation getConversation() {
        return conversation;
    }

    /**
     * Sets the conversation that this message belongs to.
     *
     * @param conversation the conversation to set
     */
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    /**
     * Returns the sender of the message.
     *
     * @return the sender {@link User} entity
     */
    public User getSender() {
        return sender;
    }

    /**
     * Sets the sender of the message.
     *
     * @param sender the sender to set
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Returns the text content of the message.
     *
     * @return the content string
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the text content of the message.
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
     * @return the send time
     */
    public LocalDateTime getSentAt() {
        return sentAt;
    }

    /**
     * Sets the timestamp when the message was sent.
     *
     * @param sentAt the send time to set
     */
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}