package com.integrador.rocket.roadmap.models.ai.dto;

import com.integrador.rocket.roadmap.models.ai.AiInteraction;
import com.integrador.rocket.roadmap.models.ai.AiPurpose;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public record AiList(
        Long id,
        AiPurpose purpose,
        Long relatedRoadmapId,
        LocalDateTime createdAt,
        Long user_id
) {
    public AiList(AiInteraction aiInteraction) {
        this(
                aiInteraction.getId(),
                aiInteraction.getPurpose(),
                aiInteraction.getRelatedRoadmapId(),
                aiInteraction.getCreatedAt(),
                aiInteraction.getUser().getId()
        );
    }

}
