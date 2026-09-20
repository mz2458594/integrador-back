package com.integrador.rocket.roadmap.models.stepresources.dto;

import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepList;
import com.integrador.rocket.roadmap.models.stepresources.StepResource;

public record StepResourcesList(
        Long id,
        String title,
        String url,
        String resourceType,
        Long roadmapStepId
) {
    public StepResourcesList(StepResource stepResource) {
        this(
                stepResource.getId(),
                stepResource.getTitle(),
                stepResource.getUrl(),
                stepResource.getResourceType(),
                stepResource.getRoadmapStep().getId()

        );
    }
}
