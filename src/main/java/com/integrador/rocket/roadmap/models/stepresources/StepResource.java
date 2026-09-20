package com.integrador.rocket.roadmap.models.stepresources;

import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesRegister;
import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesUpdate;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "step_resources")
public class StepResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;
    private String resourceType;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private RoadmapStep roadmapStep;

    public StepResource(@Valid StepResourcesRegister stepResourcesRegister, RoadmapStep roadmapStep) {
        this.title = stepResourcesRegister.title();
        this.url = stepResourcesRegister.url();
        this.resourceType = stepResourcesRegister.resourceType();
        this.roadmapStep = roadmapStep;
    }

    public void actualizar(StepResourcesUpdate stepResourcesUpdate) {
        if (stepResourcesUpdate.title() != null){
            this.title = stepResourcesUpdate.title();
        }

        if (stepResourcesUpdate.url() != null){
            this.url = stepResourcesUpdate.url();
        }

        if (stepResourcesUpdate.resourceType() != null){
            this.resourceType = stepResourcesUpdate.resourceType();
        }
    }
}
