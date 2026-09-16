package com.integrador.rocket.roadmap.models.stepresources.dto;

import jakarta.validation.constraints.NotBlank;

public record StepResourcesUpdate(
        String title,
        String url,
        String resourceType
) {
}
