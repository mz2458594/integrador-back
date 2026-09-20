package com.integrador.rocket.roadmap.models.ai.dto;

import com.integrador.rocket.roadmap.models.ai.AiPurpose;
import jakarta.validation.constraints.NotNull;

public record AiUpdate(
        AiPurpose purpose,
        Long relatedRoadmapId
) {
}
