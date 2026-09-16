package com.integrador.rocket.roadmap.models.roadmapsteps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoadmapStepUpdate(
        String title,

        String description,

        int orderIndex

) {
}
