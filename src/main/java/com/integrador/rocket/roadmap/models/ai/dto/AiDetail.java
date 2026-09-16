package com.integrador.rocket.roadmap.models.ai.dto;

import com.integrador.rocket.roadmap.models.ai.AiInteraction;
import com.integrador.rocket.roadmap.models.ai.AiPurpose;

import java.time.LocalDateTime;

public record AiDetail(
        AiPurpose purpose,
        Long relatedRoadmapId,
        LocalDateTime createdAt,
        Long user_id
) {
    public AiDetail(AiInteraction aiInteraction) {
        this(
                aiInteraction.getPurpose(),
                aiInteraction.getRelatedRoadmapId(),
                aiInteraction.getCreatedAt(),
                aiInteraction.getUser().getId()
        );
    }
}
