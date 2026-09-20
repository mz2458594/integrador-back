package com.integrador.rocket.roadmap.models.userroadmaps;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.userroadmaps.dto.UserRoadmapRegister;
import com.integrador.rocket.roadmap.models.userroadmaps.dto.UserRoadmapUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_roadmap", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "roadmap_id"}))
public class UserRoadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @CreatedDate
    private LocalDateTime startedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    public UserRoadmap(@Valid UserRoadmapRegister userRoadmapRegister, User user, Roadmap roadmap) {
        this.status = userRoadmapRegister.status();
        this.user = user;
        this.roadmap = roadmap;

    }

    public void actualizar(UserRoadmapUpdate userRoadmapUpdate) {

        this.status = userRoadmapUpdate.status();

    }
}
