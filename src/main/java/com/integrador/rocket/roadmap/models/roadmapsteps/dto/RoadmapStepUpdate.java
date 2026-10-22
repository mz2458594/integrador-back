package com.integrador.rocket.roadmap.models.roadmapsteps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoadmapStepUpdate(
        String title,

        String description,

        Integer orderIndex,

        Double x,

        Double y

) {
    // Keep existing Java callers compatible with the previous signature.
    public RoadmapStepUpdate(String title, String description, Integer orderIndex) {
        this(title, description, orderIndex, null, null);
    }

}
