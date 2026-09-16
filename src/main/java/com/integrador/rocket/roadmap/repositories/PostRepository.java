package com.integrador.rocket.roadmap.repositories;


import com.integrador.rocket.roadmap.models.posts.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
