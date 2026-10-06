package com.integrador.rocket.roadmap.models.conversations.dto;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.conversations.ConversationType;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationList(
        Long id,
        ConversationType type,
        LocalDateTime createdAt,
        List<ConversationParticipant> participants
) {
    public ConversationList(Conversation conversation) {
        this(
                conversation.getId(),
                conversation.getType(),
                conversation.getCreatedAt(),
                conversation.getParticipants().stream()
                        .map(user -> new ConversationParticipant(user.getId(), user.getName()))
                        .toList()
        );
    }
}
