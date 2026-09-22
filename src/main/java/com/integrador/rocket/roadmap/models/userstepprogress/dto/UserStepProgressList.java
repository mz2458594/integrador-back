package com.integrador.rocket.roadmap.models.userstepprogress.dto;

import com.integrador.rocket.roadmap.models.userstepprogress.Status;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;

import java.time.LocalDateTime;

public record UserStepProgressList(
        Long id,

        Status status,

        LocalDateTime completedAt,

        Long userId,

        Long roadmapStepId
) {
    public UserStepProgressList(UserStepProgress userStepProgress) {
        this(
                userStepProgress.getId(),
                userStepProgress.getStatus(),
                userStepProgress.getCompletedAt(),
                userStepProgress.getUser().getId(),
                userStepProgress.getRoadmapStep().getId()

        );
    }
}
