package com.integrador.rocket.roadmap.models.comments.dto;

import com.integrador.rocket.roadmap.models.comments.Comment;
import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public record CommentList(
        Long id,
        Long post_id,
        Long user_id,
        String content,
        LocalDateTime createdAt
) {
    public CommentList (Comment comment){
        this(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
