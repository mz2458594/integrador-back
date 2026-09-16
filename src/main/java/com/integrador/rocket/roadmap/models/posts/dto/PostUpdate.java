package com.integrador.rocket.roadmap.models.posts.dto;

import jakarta.validation.constraints.NotBlank;

public record PostUpdate(
        String title,
        String content
) {
}
