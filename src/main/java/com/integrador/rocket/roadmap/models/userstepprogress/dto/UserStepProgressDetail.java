package com.integrador.rocket.roadmap.models.userstepprogress.dto;

import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public record UserStepProgressDetail(
        Long id,

        String status,

        LocalDateTime completedAt,

        Long userId,

        Long roadmapStepId
) {
    public UserStepProgressDetail(UserStepProgress userStepProgress) {
        this(
                userStepProgress.getId(),
                userStepProgress.getStatus(),
                userStepProgress.getCompletedAt(),
                userStepProgress.getUser().getId(),
                userStepProgress.getRoadmapStep().getId()

        );
    }

}
