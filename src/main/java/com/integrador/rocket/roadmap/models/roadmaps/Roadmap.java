package com.integrador.rocket.roadmap.models.roadmaps;

import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapRegister;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapUpdate;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.userroadmaps.UserRoadmap;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "roadmap")
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String category;

    private boolean isPublic;

    @Enumerated(EnumType.STRING)
    private CreationSource createdVia;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL)
    private List<RoadmapStep> roadmapSteps;

    @OneToMany(mappedBy = "roadmap", cascade = CascadeType.ALL)
    private List<UserRoadmap> userRoadmaps;

    public Roadmap(@Valid RoadmapRegister roadmapRegister, User user) {
        this.title = roadmapRegister.title();
        this.description = roadmapRegister.description();
        this.category = roadmapRegister.category();
        this.isPublic = roadmapRegister.isPublic();
        this.createdVia = roadmapRegister.createdVia();
        this.user = user;

        // REVISAR LO DE ROADMAPSTEPS Y STEPRESOURCE
        // PARA Q EN UN MISMO CONTROLADOR SE PUEDA SUBIR
    }

    public void actualizar(RoadmapUpdate roadmapUpdate) {

        if (roadmapUpdate.title() != null) {
            this.title = roadmapUpdate.title();
        }

        if (roadmapUpdate.description() != null) {
            this.description = roadmapUpdate.description();
        }

        this.isPublic = roadmapUpdate.isPublic();


    }
}
