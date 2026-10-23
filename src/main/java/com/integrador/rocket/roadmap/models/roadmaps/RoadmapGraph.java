package com.integrador.rocket.roadmap.models.roadmaps;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name="roadmap_graphs") @Getter @NoArgsConstructor
public class RoadmapGraph {
    @Id @Column(name="roadmap_id") private Long roadmapId;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="roadmap_id",insertable=false,updatable=false) private Roadmap roadmap;
    @Column(nullable=false) private long revision;
    public RoadmapGraph(Roadmap roadmap) { this.roadmap = roadmap; this.roadmapId = roadmap.getId(); }
    public void advance() { revision++; }
}
