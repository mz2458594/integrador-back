package com.integrador.rocket.roadmap.models.messages.dto;

import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MessageRegister(
        @NotBlank String content,
        @NotNull Long conversation_id
//        boolean isFromAgent
) {
}
