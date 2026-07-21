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

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ListingRepository listingRepository;
    private final UserService userService;

    public ConversationService(ConversationRepository conversationRepository,
                               MessageRepository messageRepository,
                               ListingRepository listingRepository,
                               UserService userService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.listingRepository = listingRepository;
        this.userService = userService;
    }

    public List<ConversationDto> getMyConversations() {
        User current = userService.getCurrentUser();
        return conversationRepository.findByBuyerIdOrSellerIdOrderByCreatedAtDesc(current.getId(), current.getId())
                .stream()
                .map(this::toDtoWithoutMessages)
                .toList();
    }

    public ConversationDto getConversation(Long id) {
        Conversation conversation = getAccessibleConversation(id);
        return toDtoWithMessages(conversation);
    }

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
        return dto;
    }

    private ConversationDto toDtoWithMessages(Conversation conversation) {
        ConversationDto dto = toDtoWithoutMessages(conversation);
        dto.setMessages(messageRepository.findByConversationIdOrderBySentAtAsc(conversation.getId()).stream()
                .map(this::toMessageDto)
                .toList());
        return dto;
    }

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
