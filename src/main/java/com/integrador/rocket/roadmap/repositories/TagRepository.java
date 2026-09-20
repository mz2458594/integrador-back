package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Page<Tag> findAllByPostsId(Pageable pageable, Long id);
}
