package com.integrador.rocket.roadmap.models.conversations.dto;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.conversations.ConversationType;
import com.integrador.rocket.roadmap.models.messages.dto.MessageDetail;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationList(
        Long id,
        ConversationType type,
        LocalDateTime createdAt
) {
    public ConversationList(Conversation conversation) {
        this(
                conversation.getId(),
                conversation.getType(),
                conversation.getCreatedAt()
        );
    }
}
