package com.integrador.rocket.roadmap.models.userroadmaps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UserRoadmapRegister(

        @NotBlank String status,

//El user lo obtienes del controlador por medio del token
//        @NotNull Long userId,
        @NotNull Long roadmapId
) {
}
