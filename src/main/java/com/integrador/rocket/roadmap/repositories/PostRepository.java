package com.integrador.rocket.roadmap.repositories;


import com.integrador.rocket.roadmap.models.posts.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByUserId(Pageable pageable, Long id);

}
