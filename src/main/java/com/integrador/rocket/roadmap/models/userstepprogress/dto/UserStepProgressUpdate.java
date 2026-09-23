package com.integrador.rocket.roadmap.models.userstepprogress.dto;

import com.integrador.rocket.roadmap.models.userstepprogress.Status;
import jakarta.validation.constraints.NotNull;

public record UserStepProgressUpdate(
        @NotNull Status status
) {
}
