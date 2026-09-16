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
// EL REGISTRO DEL USUARIO ID VA EN EL SERVICE
        // EL ID DEL USUARIO ESTARA EN EL @AuthenticationPrincipal EN EL CONTROLLER
//        Long userId
) {
}
