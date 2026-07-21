package com.marketplace.backend.repository;

import com.marketplace.backend.entity.Conversation;
import com.marketplace.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderBySentAtAsc(Conversation conversation);
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);
}
