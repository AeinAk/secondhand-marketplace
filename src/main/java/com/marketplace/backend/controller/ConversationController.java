package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ConversationDto;
import com.marketplace.backend.dto.MessageDto;
import com.marketplace.backend.service.ConversationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing conversations and messages.
 * <p>
 * Provides endpoints for retrieving conversations, starting new conversations,
 * and sending messages. All endpoints require user authentication and operate
 * on the currently logged-in user.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    /**
     * Constructs a {@code ConversationController} with the required conversation service.
     *
     * @param conversationService the service handling conversation and message logic
     */
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /**
     * Retrieves all conversations for the currently authenticated user.
     * <p>
     * Returns conversations where the user is either the buyer or the seller.
     * Each conversation includes the listing title, the other participant's details,
     * and the timestamp of the last message (if any).
     * </p>
     *
     * @return a list of {@link ConversationDto} objects representing the user's conversations,
     *         ordered by most recent activity
     */
    @GetMapping
    public List<ConversationDto> getMyConversations() {
        return conversationService.getMyConversations();
    }

    /**
     * Retrieves a specific conversation by its ID, including all messages.
     * <p>
     * The user must be a participant (either buyer or seller) in the conversation,
     * otherwise access is denied.
     * </p>
     *
     * @param id the unique identifier of the conversation
     * @return a {@link ConversationDto} containing the conversation details and all messages
     * @throws com.marketplace.backend.exception.BusinessException if the conversation does not exist
     *         or the user is not a participant
     */
    @GetMapping("/{id}")
    public ConversationDto getConversation(@PathVariable Long id) {
        return conversationService.getConversation(id);
    }

    /**
     * Initiates a new conversation for a given listing.
     * <p>
     * The authenticated user becomes the buyer, and the listing owner is the seller.
     * If a conversation already exists between the buyer and seller for this listing,
     * the existing conversation is returned instead.
     * </p>
     *
     * @param listingId the unique identifier of the listing
     * @return the existing or newly created {@link ConversationDto}
     * @throws com.marketplace.backend.exception.BusinessException if the listing does not exist
     *         or the user is trying to start a conversation with themselves
     */
    @PostMapping("/start/{listingId}")
    public ConversationDto startConversation(@PathVariable Long listingId) {
        return conversationService.startConversation(listingId);
    }

    /**
     * Sends a new message within a conversation.
     * <p>
     * The user must be a participant in the conversation. The message content
     * cannot be empty and will be stored with the current timestamp.
     * </p>
     *
     * @param id   the unique identifier of the conversation
     * @param body the request body containing the {@code "content"} field with the message text
     * @return a {@link MessageDto} containing the saved message details
     * @throws com.marketplace.backend.exception.BusinessException if the conversation does not exist,
     *         the user is not a participant, or the message content is empty
     */
    @PostMapping("/{id}/messages")
    public MessageDto sendMessage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return conversationService.sendMessage(id, body.get("content"));
    }
}