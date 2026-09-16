package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
}
