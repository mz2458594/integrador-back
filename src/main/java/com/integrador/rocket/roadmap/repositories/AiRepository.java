package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.ai.AiInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRepository extends JpaRepository<AiInteraction, Long> {
}
