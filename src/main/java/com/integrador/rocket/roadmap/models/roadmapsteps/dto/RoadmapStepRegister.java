package com.integrador.rocket.roadmap.models.roadmapsteps.dto;

import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesDetail;
import com.integrador.rocket.roadmap.models.userstepprogress.dto.UserStepProgressDetail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RoadmapStepRegister(

        @NotBlank String title,

        @NotBlank String description,

        @NotNull int orderIndex,

        @NotNull Long roadmapId,

        Double x,

        Double y


) {
    // Keep existing Java callers compatible with the previous signature.
    public RoadmapStepRegister(String title, String description, int orderIndex, Long roadmapId) {
        this(title, description, orderIndex, roadmapId, null, null);
    }

}
