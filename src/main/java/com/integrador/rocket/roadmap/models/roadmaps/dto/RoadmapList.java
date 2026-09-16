package com.integrador.rocket.roadmap.models.roadmaps.dto;

import com.integrador.rocket.roadmap.models.roadmaps.CreationSource;
import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;

import java.time.LocalDateTime;

public record RoadmapList(
        Long id,

        String title,

        String description,

        String category,

        boolean isPublic,

        CreationSource createdVia,

        LocalDateTime createdAt,

        Long userId
) {
    public RoadmapList(Roadmap roadmap) {
        this(
                roadmap.getId(),
                roadmap.getTitle(),
                roadmap.getDescription(),
                roadmap.getCategory(),
                roadmap.isPublic(),
                roadmap.getCreatedVia(),
                roadmap.getCreatedAt(),
                roadmap.getUser().getId()
        );
    }

}
