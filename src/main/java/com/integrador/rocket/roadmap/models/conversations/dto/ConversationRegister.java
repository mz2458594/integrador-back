package com.integrador.rocket.roadmap.models.conversations.dto;

import com.integrador.rocket.roadmap.models.conversations.ConversationType;
import jakarta.validation.constraints.NotNull;


public record ConversationRegister(
        @NotNull ConversationType type,
        Long participantId
) {
}
