package com.integrador.rocket.roadmap.models.userstepprogress.dto;

import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;

import java.time.LocalDateTime;

public record UserStepProgressList(
        Long id,

        String status,

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
