package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from Roadmap r where r.id = :id")
    java.util.Optional<Roadmap> findByIdForUpdate(Long id);
    Page<Roadmap> findAllByUserId(Pageable pageable, Long userId);
}
