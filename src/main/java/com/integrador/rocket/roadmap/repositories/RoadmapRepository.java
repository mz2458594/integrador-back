package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    Page<Roadmap> findAllByUserId(Pageable pageable, Long userId);
}
