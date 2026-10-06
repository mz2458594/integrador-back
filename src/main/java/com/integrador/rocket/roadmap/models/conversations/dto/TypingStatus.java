package com.integrador.rocket.roadmap.models.conversations.dto;

public record TypingStatus(
        Long conversationId,
        Long userId,
        String userName,
        boolean typing
) {
}
