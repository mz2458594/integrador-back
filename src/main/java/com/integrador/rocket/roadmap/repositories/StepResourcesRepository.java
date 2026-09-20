package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.stepresources.StepResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StepResourcesRepository extends JpaRepository<StepResource, Long> {
    Page<StepResource> findAllByRoadmapStepId(Pageable pageable, Long id);
}
