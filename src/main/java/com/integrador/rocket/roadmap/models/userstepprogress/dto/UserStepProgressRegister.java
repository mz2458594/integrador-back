package com.integrador.rocket.roadmap.models.userstepprogress.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UserStepProgressRegister(

        @NotBlank String status,

// EL USERID LO AGARRAS DEL TOKEN
//        Long userId,

        @NotNull Long roadmapStepId
) {
}
