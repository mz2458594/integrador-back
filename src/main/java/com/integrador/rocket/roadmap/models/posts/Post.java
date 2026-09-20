package com.integrador.rocket.roadmap.models.posts;

import com.integrador.rocket.roadmap.models.comments.Comment;
import com.integrador.rocket.roadmap.models.posts.dto.PostRegister;
import com.integrador.rocket.roadmap.models.posts.dto.PostUpdate;
import com.integrador.rocket.roadmap.models.tags.Tag;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Integer views = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"), //clave foránea de esta entidad
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    public Post(@Valid PostRegister postRegister, User user, List<Tag> tags) {
        this.title = postRegister.title();
        this.content = postRegister.content();
        this.user = user;
        //VALIDAR LO DE TAGS
        this.tags = tags;

    }


    public void actualizar(PostUpdate postUpdate, List<Tag> tags) {
        if (postUpdate.title() != null){
            this.title = postUpdate.title();
        }
        if (postUpdate.content() != null){
            this.content = postUpdate.content();
        }

        if (tags != null){
            this.tags = tags;
        }
    }
}
