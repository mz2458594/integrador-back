package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoadmapStepsRepository extends JpaRepository<RoadmapStep, Long> {
    Page<RoadmapStep> findAllByRoadmapId(Pageable pageable, Long id);
}
