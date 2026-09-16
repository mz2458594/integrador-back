package com.integrador.rocket.roadmap.models.comments.dto;

import com.integrador.rocket.roadmap.models.comments.Comment;

import java.time.LocalDateTime;

public record CommentDetail(
        Long post_id,
        Long user_id,
        String content,
        LocalDateTime createdAt
) {
    public CommentDetail(Comment comment) {
        this(
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
