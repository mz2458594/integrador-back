package com.integrador.rocket.roadmap.models.userstepprogress.dto;

import com.integrador.rocket.roadmap.models.userstepprogress.Status;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UserStepProgressRegister(

        @NotNull Status status,

// EL USERID LO AGARRAS DEL TOKEN
//        Long userId,

        @NotNull Long roadmapStepId
) {
}
