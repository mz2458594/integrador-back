package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
