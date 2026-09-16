package com.integrador.rocket.roadmap.models.stepresources;

import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import jakarta.persistence.*;
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
}
