package com.integrador.rocket.roadmap.models.roadmapsteps;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.stepresources.StepResource;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    private int orderIndex;

    @ManyToOne
    @JoinColumn(name = "roadmap_id")
    private Roadmap roadmap;

    @OneToMany(mappedBy = "roadmapStep", cascade = CascadeType.ALL)
    private List<StepResource> stepResources;

    @OneToMany(mappedBy = "roadmapStep", cascade = CascadeType.ALL)
    private List<UserStepProgress> userStepProgresses;

}
