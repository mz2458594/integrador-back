package com.integrador.rocket.roadmap.models.posts.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostUpdate(
        String title,
        String content,
        List<Long> tagIds
) {
}
