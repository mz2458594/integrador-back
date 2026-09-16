package com.integrador.rocket.roadmap.models.stepresources.dto;

import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepList;
import com.integrador.rocket.roadmap.models.stepresources.StepResource;
import jakarta.persistence.*;


public record StepResourcesDetail(
        Long id,
        String title,
        String url,
        String resourceType,
        Long roadmapStepId
) {
    public StepResourcesDetail(StepResource stepResource){
        this (
                stepResource.getId(),
                stepResource.getTitle(),
                stepResource.getUrl(),
                stepResource.getResourceType(),
                stepResource.getRoadmapStep().getId()
        );
    }
}
