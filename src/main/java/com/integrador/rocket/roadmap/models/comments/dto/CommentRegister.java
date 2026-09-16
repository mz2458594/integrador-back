package com.integrador.rocket.roadmap.models.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CommentRegister(
        @NotNull Long post_id,
        @NotNull Long user_id,
        @NotBlank String content
) {
}
