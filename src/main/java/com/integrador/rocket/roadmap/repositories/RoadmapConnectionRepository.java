package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.roadmaps.RoadmapConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoadmapConnectionRepository extends JpaRepository<RoadmapConnection,Long> {
    List<RoadmapConnection> findAllByRoadmapIdOrderByIdAsc(Long roadmapId);
    List<RoadmapConnection> findAllBySourceIdOrTargetId(Long sourceId,Long targetId);

    default void removeByRoadmapId(Long roadmapId) {
        deleteAll(findAllByRoadmapIdOrderByIdAsc(roadmapId));
        flush();
    }
    default void removeByStepId(Long stepId) {
        deleteAll(findAllBySourceIdOrTargetId(stepId,stepId));
        flush();
    }
}
