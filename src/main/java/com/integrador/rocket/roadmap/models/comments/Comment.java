package com.integrador.rocket.roadmap.models.comments;

import com.integrador.rocket.roadmap.models.comments.dto.CommentRegister;
import com.integrador.rocket.roadmap.models.comments.dto.CommentUpdate;
import com.integrador.rocket.roadmap.models.posts.Post;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public Comment(@Valid CommentRegister commentRegister, User user, Post post) {
        this.user = user;
        this.content = commentRegister.content();
        this.post = post;
    }

    public void actualizar(CommentUpdate commentUpdate) {
        if (commentUpdate.content() != null){
            this.content = commentUpdate.content();
        }
    }
}
