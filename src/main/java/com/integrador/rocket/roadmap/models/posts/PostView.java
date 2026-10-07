package com.integrador.rocket.roadmap.models.posts;

import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "post_views",
        uniqueConstraints = @UniqueConstraint(name = "uk_post_view_user", columnNames = {"post_id", "user_id"})
)
@Getter
@NoArgsConstructor
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PostView(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
