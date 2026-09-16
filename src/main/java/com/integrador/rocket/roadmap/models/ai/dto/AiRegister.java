package com.integrador.rocket.roadmap.models.ai.dto;

import com.integrador.rocket.roadmap.models.ai.AiPurpose;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AiRegister(
        @NotNull AiPurpose purpose,
        Long relatedRoadmapId,
        @NotNull Long user_id
) {
}
