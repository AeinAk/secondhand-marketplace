package com.marketplace.backend.service;

import com.marketplace.backend.dto.ConversationDto;
import com.marketplace.backend.dto.MessageDto;
import com.marketplace.backend.entity.Conversation;
import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.Message;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.ConversationRepository;
import com.marketplace.backend.repository.ListingRepository;
import com.marketplace.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing conversations and messages.
 * <p>
 * Provides business logic for the chat system, including creating conversations,
 * sending messages, retrieving conversations, and managing access control.
 * Conversations are created between a buyer and a seller for a specific listing.
 * Users can only access conversations they are participants in.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ListingRepository listingRepository;
    private final UserService userService;

    /**
     * Constructs a ConversationService with the required repositories and services.
     *
     * @param conversationRepository the repository for conversation data access
     * @param messageRepository      the repository for message data access
     * @param listingRepository      the repository for listing data access
     * @param userService            the service for user-related operations
     */
    public ConversationService(ConversationRepository conversationRepository,
                               MessageRepository messageRepository,
                               ListingRepository listingRepository,
                               UserService userService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.listingRepository = listingRepository;
        this.userService = userService;
    }

    /**
     * Retrieves all conversations for the currently authenticated user.
     * <p>
     * Returns conversations where the user is either the buyer or the seller.
     * Results are ordered by creation date in descending order (most recent first).
     * Each conversation includes the last message timestamp for display purposes.
     * </p>
     *
     * @return a list of {@link ConversationDto} objects representing the user's conversations
     */
    public List<ConversationDto> getMyConversations() {
        User current = userService.getCurrentUser();
        return conversationRepository.findByBuyerIdOrSellerIdOrderByCreatedAtDesc(current.getId(), current.getId())
                .stream()
                .map(this::toDtoWithoutMessages)
                .toList();
    }

    /**
     * Retrieves a specific conversation by its ID with all messages.
     * <p>
     * The user must be a participant (either buyer or seller) in the conversation,
     * otherwise access is denied. Returns the conversation with all messages
     * ordered chronologically.
     * </p>
     *
     * @param id the unique identifier of the conversation
     * @return a {@link ConversationDto} containing the conversation details and all messages
     * @throws BusinessException if the conversation does not exist or the user is not a participant
     */
    public ConversationDto getConversation(Long id) {
        Conversation conversation = getAccessibleConversation(id);
        return toDtoWithMessages(conversation);
    }

    /**
     * Starts a new conversation for a given listing.
     * <p>
     * The authenticated user becomes the buyer, and the listing owner becomes the seller.
     * If a conversation already exists between the buyer and seller for this listing,
     * the existing conversation is returned instead. Users cannot start a conversation
     * with themselves.
     * </p>
     *
     * @param listingId the unique identifier of the listing
     * @return the existing or newly created {@link ConversationDto}
     * @throws BusinessException if the listing does not exist or the user is the seller
     */
    @Transactional
    public ConversationDto startConversation(Long listingId) {
        User buyer = userService.getCurrentUser();
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new BusinessException("Listing not found"));
        if (listing.getSeller().getId().equals(buyer.getId())) {
            throw new BusinessException("You cannot start a conversation with yourself");
        }

        Conversation conversation = conversationRepository.findByListingAndBuyer(listing, buyer)
                .orElseGet(() -> {
                    Conversation created = new Conversation();
                    created.setListing(listing);
                    created.setBuyer(buyer);
                    created.setSeller(listing.getSeller());
                    return conversationRepository.save(created);
                });
        return toDtoWithoutMessages(conversation);
    }

    /**
     * Sends a new message within a conversation.
     * <p>
     * The user must be a participant in the conversation. The message content
     * cannot be empty. The message is saved with the current timestamp and a
     * read status of false.
     * </p>
     *
     * @param conversationId the unique identifier of the conversation
     * @param content        the text content of the message
     * @return a {@link MessageDto} containing the saved message details
     * @throws BusinessException if the conversation does not exist, the user is not a participant,
     *         or the message content is empty
     */
    @Transactional
    public MessageDto sendMessage(Long conversationId, String content) {
        Conversation conversation = getAccessibleConversation(conversationId);
        User sender = userService.getCurrentUser();

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        messageRepository.save(message);
        return toMessageDto(message);
    }

    /**
     * Validates and retrieves a conversation that the current user has access to.
     * <p>
     * Checks that the conversation exists and that the current user is either
     * the buyer or the seller. If not, access is denied.
     * </p>
     *
     * @param id the unique identifier of the conversation
     * @return the {@link Conversation} entity
     * @throws BusinessException if the conversation does not exist or access is denied
     */
    private Conversation getAccessibleConversation(Long id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Conversation not found"));
        User current = userService.getCurrentUser();
        if (!conversation.getBuyer().getId().equals(current.getId())
                && !conversation.getSeller().getId().equals(current.getId())) {
            throw new BusinessException("Access denied");
        }
        return conversation;
    }

    /**
     * Converts a Conversation entity to a DTO without including messages.
     * <p>
     * This method is used for conversation list views where messages are not needed.
     * The last message timestamp is extracted from the most recent message in the
     * conversation to display the latest activity time.
     * </p>
     *
     * @param conversation the Conversation entity to convert
     * @return a {@link ConversationDto} containing conversation metadata
     */
    private ConversationDto toDtoWithoutMessages(Conversation conversation) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setListingId(conversation.getListing().getId());
        dto.setListingTitle(conversation.getListing().getTitle());
        dto.setBuyerId(conversation.getBuyer().getId());
        dto.setBuyerUsername(conversation.getBuyer().getUsername());
        dto.setSellerId(conversation.getSeller().getId());
        dto.setSellerUsername(conversation.getSeller().getUsername());
        dto.setCreatedAt(conversation.getCreatedAt());
        List<Message> messages = messageRepository.findByConversationIdOrderBySentAtAsc(conversation.getId());
        if (!messages.isEmpty()) {
            dto.setLastMessageTime(messages.get(messages.size() - 1).getSentAt());
        }
        return dto;
    }

    /**
     * Converts a Conversation entity to a DTO including all messages.
     * <p>
     * This method is used for detailed conversation views where all messages
     * need to be displayed. The messages are ordered chronologically (oldest first).
     * </p>
     *
     * @param conversation the Conversation entity to convert
     * @return a {@link ConversationDto} containing conversation metadata and messages
     */
    private ConversationDto toDtoWithMessages(Conversation conversation) {
        ConversationDto dto = toDtoWithoutMessages(conversation);
        dto.setMessages(messageRepository.findByConversationIdOrderBySentAtAsc(conversation.getId()).stream()
                .map(this::toMessageDto)
                .toList());
        return dto;
    }

    /**
     * Converts a Message entity to a MessageDto.
     * <p>
     * This method maps all message fields to a DTO for safe data transfer
     * to the frontend, including sender information and timestamps.
     * </p>
     *
     * @param message the Message entity to convert
     * @return a {@link MessageDto} containing message details
     */
    private MessageDto toMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderUsername(message.getSender().getUsername());
        dto.setContent(message.getContent());
        dto.setReadFlag(message.isReadFlag());
        dto.setSentAt(message.getSentAt());
        return dto;
    }
}