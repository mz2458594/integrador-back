package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.roadmaps.RoadmapLike;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/roadmap")
public class RoadmapEngagementController {
    private final RoadmapRepository roadmaps;
    private final RoadmapLikeRepository likes;
    public RoadmapEngagementController(RoadmapRepository roadmaps, RoadmapLikeRepository likes) {
        this.roadmaps = roadmaps; this.likes = likes;
    }
    public record Engagement(long likesCount, boolean likedByCurrentUser) {}
    @GetMapping("/popular")
    @Transactional(readOnly = true)
    public List<RoadmapLikeRepository.PopularRoadmap> popular() {
        return likes.popularRoadmaps(PageRequest.of(0, 3));
    }
    @GetMapping("/top-creators")
    @Transactional(readOnly = true)
    public List<RoadmapLikeRepository.PopularCreator> creators() {
        return likes.popularCreators(PageRequest.of(0, 5));
    }
    @GetMapping("/{id}/like")
    @Transactional(readOnly = true)
    public Engagement engagement(@PathVariable Long id, @AuthenticationPrincipal User user) {
        var roadmap = roadmaps.findById(id).orElseThrow(() -> new EntityNotFoundException("Roadmap no encontrado"));
        if (!roadmap.isPublic() && !roadmap.getUser().getId().equals(user.getId()))
            throw new AccessDeniedException("Este roadmap es privado");
        return new Engagement(likes.countByRoadmapId(id), likes.existsByRoadmapIdAndUserId(id, user.getId()));
    }
    @PostMapping("/{id}/like")
    @Transactional
    public Engagement like(@PathVariable Long id, @AuthenticationPrincipal User user) {
        requirePublic(id);
        if (!likes.existsByRoadmapIdAndUserId(id, user.getId())) likes.saveAndFlush(new RoadmapLike(roadmaps.getReferenceById(id), user));
        return new Engagement(likes.countByRoadmapId(id), true);
    }
    @DeleteMapping("/{id}/like")
    @Transactional
    public Engagement unlike(@PathVariable Long id, @AuthenticationPrincipal User user) {
        requirePublic(id);
        likes.deleteByRoadmapIdAndUserId(id, user.getId());
        likes.flush();
        return new Engagement(likes.countByRoadmapId(id), false);
    }
    private Roadmap requirePublic(Long id) {
        var roadmap = roadmaps.findByIdForUpdate(id).orElseThrow(() -> new EntityNotFoundException("Roadmap no encontrado"));
        if (!roadmap.isPublic()) throw new AccessDeniedException("Solo puedes dar me gusta a roadmaps públicos");
        return roadmap;
    }
}
