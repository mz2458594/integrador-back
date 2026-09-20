package com.integrador.rocket.roadmap.models.roadmaps.dto;

import com.integrador.rocket.roadmap.models.roadmaps.CreationSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoadmapRegister(
        @NotBlank String title,

        @NotBlank String description,

        @NotBlank String category,

        @NotNull boolean isPublic,

        @NotNull CreationSource createdVia
) {
}
