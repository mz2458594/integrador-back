package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.comments.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
