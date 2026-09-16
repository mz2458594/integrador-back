package com.integrador.rocket.roadmap.models.roadmaps.dto;

import com.integrador.rocket.roadmap.models.roadmaps.CreationSource;
import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepDetail;
import com.integrador.rocket.roadmap.models.userroadmaps.UserRoadmap;
import com.integrador.rocket.roadmap.models.userroadmaps.dto.UserRoadmapDetail;

import java.time.LocalDateTime;
import java.util.List;

public record RoadmapDetail(
        Long id,

        String title,

        String description,

        String category,

        boolean isPublic,

        CreationSource createdVia,

        LocalDateTime createdAt,

        Long userId,

        List<RoadmapStepDetail> roadmapSteps,
        List<UserRoadmapDetail> userRoadmaps
) {
    public RoadmapDetail(Roadmap roadmap) {
        this(
                roadmap.getId(),
                roadmap.getTitle(),
                roadmap.getDescription(),
                roadmap.getCategory(),
                roadmap.isPublic(),
                roadmap.getCreatedVia(),
                roadmap.getCreatedAt(),
                roadmap.getUser().getId(),
                roadmap.getRoadmapSteps().stream().map(RoadmapStepDetail::new).toList(),
                roadmap.getUserRoadmaps().stream().map(UserRoadmapDetail::new).toList()
        );
    }
}
