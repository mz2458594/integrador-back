package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.userroadmaps.UserRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoadmapRepository extends JpaRepository<UserRoadmap, Long> {
}
