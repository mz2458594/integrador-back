package com.integrador.rocket.roadmap.models.roadmapsteps.dto;

import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapList;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.stepresources.StepResource;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;

import java.util.List;

public record RoadmapStepList(
        Long id,

        String title,

        String description,

        int orderIndex,

        Long roadmapId
) {

    public RoadmapStepList(RoadmapStep roadmapStep) {
        this(
                roadmapStep.getId(),
                roadmapStep.getTitle(),
                roadmapStep.getDescription(),
                roadmapStep.getOrderIndex(),
                roadmapStep.getRoadmap().getId()
        );
    }
}
