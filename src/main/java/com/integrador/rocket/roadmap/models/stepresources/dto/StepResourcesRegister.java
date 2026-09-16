package com.integrador.rocket.roadmap.models.stepresources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StepResourcesRegister(
        @NotBlank String title,
        @NotBlank String url,
        @NotBlank String resourceType,
        @NotNull Long roadmapStepId
) {
}
