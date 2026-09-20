package com.integrador.rocket.roadmap.models.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentUpdate(
        String content
) {
}
