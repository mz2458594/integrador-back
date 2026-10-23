package com.integrador.rocket.roadmap.models.roadmaps;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name="roadmap_connections", uniqueConstraints=@UniqueConstraint(name="uk_roadmap_connection",columnNames={"source_step_id","target_step_id"}))
@Getter @NoArgsConstructor
public class RoadmapConnection {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="roadmap_id") private Roadmap roadmap;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="source_step_id") private RoadmapStep source;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="target_step_id") private RoadmapStep target;
    public RoadmapConnection(Roadmap roadmap, RoadmapStep source, RoadmapStep target) {
        this.roadmap=roadmap; this.source=source; this.target=target;
    }
}
