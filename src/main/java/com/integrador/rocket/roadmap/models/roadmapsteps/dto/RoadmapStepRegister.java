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

        @NotNull Long roadmapId


) {
}
