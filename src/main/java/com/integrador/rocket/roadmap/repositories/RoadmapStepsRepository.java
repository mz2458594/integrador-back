package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapStepsRepository extends JpaRepository<RoadmapStep, Long> {
}
