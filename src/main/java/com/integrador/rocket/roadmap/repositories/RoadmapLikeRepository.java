package com.integrador.rocket.roadmap.repositories;

import com.integrador.rocket.roadmap.models.roadmaps.RoadmapLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RoadmapLikeRepository extends JpaRepository<RoadmapLike, Long> {
    long countByRoadmapId(Long roadmapId);
    boolean existsByRoadmapIdAndUserId(Long roadmapId, Long userId);
    void deleteByRoadmapIdAndUserId(Long roadmapId, Long userId);
    interface PopularRoadmap {
        Long getId(); String getTitle(); Long getLikesCount();
    }
    interface PopularCreator {
        Long getUserId(); String getName(); Long getLikesCount();
    }
    @Query("select r.id as id, r.title as title, count(l) as likesCount from RoadmapLike l join l.roadmap r where r.isPublic = true and r.user.isActive = true group by r.id, r.title order by count(l) desc, r.id asc")
    List<PopularRoadmap> popularRoadmaps(Pageable pageable);
    @Query("select u.id as userId, u.name as name, count(l) as likesCount from RoadmapLike l join l.roadmap r join r.user u where r.isPublic = true and u.isActive = true group by u.id, u.name order by count(l) desc, u.id asc")
    List<PopularCreator> popularCreators(Pageable pageable);
}
