package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
