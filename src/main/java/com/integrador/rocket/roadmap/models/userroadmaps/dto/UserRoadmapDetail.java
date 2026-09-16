package com.integrador.rocket.roadmap.models.userroadmaps.dto;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.userroadmaps.UserRoadmap;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public record UserRoadmapDetail(


        Long id,

        String status,

        LocalDateTime startedAt,

        Long userId,

        Long roadmapId
) {

    public UserRoadmapDetail(UserRoadmap userRoadmap) {
        this(
                userRoadmap.getId(),
                userRoadmap.getStatus(),
                userRoadmap.getStartedAt(),
                userRoadmap.getUser().getId(),
                userRoadmap.getRoadmap().getId()
        );
    }
}
