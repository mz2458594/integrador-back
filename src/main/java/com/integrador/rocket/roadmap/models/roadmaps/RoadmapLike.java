package com.integrador.rocket.roadmap.models.roadmaps;

import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "roadmap_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"roadmap_id", "user_id"}))
public class RoadmapLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    public RoadmapLike(Roadmap roadmap, User user) { this.roadmap = roadmap; this.user = user; }
}
