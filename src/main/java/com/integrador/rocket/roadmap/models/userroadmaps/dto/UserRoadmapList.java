package com.integrador.rocket.roadmap.models.userroadmaps.dto;

import com.integrador.rocket.roadmap.models.userroadmaps.UserRoadmap;

import java.time.LocalDateTime;

public record UserRoadmapList(
        Long id,

        String status,

        LocalDateTime startedAt,

        Long userId,

        Long roadmapId
) {
    public UserRoadmapList(UserRoadmap userRoadmap) {
        this(
                userRoadmap.getId(),
                userRoadmap.getStatus(),
                userRoadmap.getStartedAt(),
                userRoadmap.getUser().getId(),
                userRoadmap.getRoadmap().getId()
        );
    }
}
