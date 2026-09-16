package com.integrador.rocket.roadmap.models.roadmapsteps.dto;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapList;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.stepresources.StepResource;
import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesDetail;
import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesList;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;
import com.integrador.rocket.roadmap.models.userstepprogress.dto.UserStepProgressDetail;
import jakarta.persistence.*;

import java.util.List;

public record RoadmapStepDetail(
        Long id,

        String title,

        String description,

        int orderIndex,

        Long roadmapId,

        List<StepResourcesDetail> stepResources,

        List<UserStepProgressDetail> userStepProgresses
) {

    public RoadmapStepDetail(RoadmapStep roadmapStep) {
        this(
                roadmapStep.getId(),
                roadmapStep.getTitle(),
                roadmapStep.getDescription(),
                roadmapStep.getOrderIndex(),
                roadmapStep.getRoadmap().getId(),
                roadmapStep.getStepResources().stream().map(StepResourcesDetail::new).toList(),
                roadmapStep.getUserStepProgresses().stream().map(UserStepProgressDetail::new).toList()
        );
    }

}
