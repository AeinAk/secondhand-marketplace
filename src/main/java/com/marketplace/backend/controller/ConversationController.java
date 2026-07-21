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

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public List<ConversationDto> getMyConversations() {
        return conversationService.getMyConversations();
    }

    @GetMapping("/{id}")
    public ConversationDto getConversation(@PathVariable Long id) {
        return conversationService.getConversation(id);
    }

    @PostMapping("/start/{listingId}")
    public ConversationDto startConversation(@PathVariable Long listingId) {
        return conversationService.startConversation(listingId);
    }

    @PostMapping("/{id}/messages")
    public MessageDto sendMessage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return conversationService.sendMessage(id, body.get("content"));
    }
}
