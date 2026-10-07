package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.posts.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewRepository extends JpaRepository<PostView, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
