package com.integrador.rocket.roadmap.models.tags.dto;

import jakarta.validation.constraints.NotBlank;

public record TagRegister(
        @NotBlank String name
) {
}
