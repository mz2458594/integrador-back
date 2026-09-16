package com.integrador.rocket.roadmap.models.userroadmaps.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRoadmapUpdate(
        @NotBlank String status
) {
}
