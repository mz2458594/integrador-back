package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.messages.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByConversationId(Pageable page, Long conversationId);
}
