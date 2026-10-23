package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoadmapStepsRepository extends JpaRepository<RoadmapStep, Long> {
    java.util.List<RoadmapStep> findAllByRoadmapIdOrderByOrderIndexAscIdAsc(Long roadmapId);
    Page<RoadmapStep> findAllByRoadmapId(Pageable pageable, Long id);
}
