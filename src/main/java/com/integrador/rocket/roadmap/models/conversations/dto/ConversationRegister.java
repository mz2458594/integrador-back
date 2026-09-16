package com.integrador.rocket.roadmap.models.conversations.dto;

import com.integrador.rocket.roadmap.models.conversations.ConversationType;
import com.integrador.rocket.roadmap.models.messages.dto.MessageDetail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationRegister(
        @NotNull ConversationType type,
        @NotNull List<Long> participantIds
) {
}
