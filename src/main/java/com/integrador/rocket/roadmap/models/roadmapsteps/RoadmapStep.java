package com.integrador.rocket.roadmap.models.roadmapsteps;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepRegister;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepUpdate;
import com.integrador.rocket.roadmap.models.stepresources.StepResource;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "roadmap_steps")
public class RoadmapStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    @OneToMany(mappedBy = "roadmapStep", cascade = CascadeType.ALL)
    private List<StepResource> stepResources = new ArrayList<>();

    @OneToMany(mappedBy = "roadmapStep", cascade = CascadeType.ALL)
    private List<UserStepProgress> userStepProgresses = new ArrayList<>();

    public RoadmapStep(@Valid RoadmapStepRegister roadmapStepRegister, Roadmap roadmap) {
        this.title = roadmapStepRegister.title();
        this.description = roadmapStepRegister.description();
        this.orderIndex = roadmapStepRegister.orderIndex();
        this.roadmap = roadmap;
    }

    public void actualizar(RoadmapStepUpdate roadmapStepUpdate) {
        if (roadmapStepUpdate.title() != null){
            this.title = roadmapStepUpdate.title();
        }

        if (roadmapStepUpdate.description() != null){
            this.description = roadmapStepUpdate.description();
        }

        if (roadmapStepUpdate.orderIndex() != null){
            this.orderIndex = roadmapStepUpdate.orderIndex();
        }
    }
}
