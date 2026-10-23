package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapDetail;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapList;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapRegister;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.services.VocationalAccessService;
import com.integrador.rocket.roadmap.repositories.RoadmapRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/roadmap")
public class RoadmapController {

    @Autowired
    private com.integrador.rocket.roadmap.services.RoadmapGraphService graphService;

    @Autowired
    private VocationalAccessService vocationalAccessService;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @GetMapping
    public ResponseEntity<Page<RoadmapList>> listarRoadmaps(@PageableDefault(size = 10) Pageable pageable) {
        var roadmaps = roadmapRepository.findAll(pageable).map(RoadmapList::new);
        return ResponseEntity.ok(roadmaps);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<RoadmapList>> listarRoadmapPorUsuarioId(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pageable) {
        var roadmaps = roadmapRepository.findAllByUserId(pageable, user.getId()).map(RoadmapList::new);
        return ResponseEntity.ok(roadmaps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoadmapDetail> detalleRoadmap(@PathVariable Long id) {
        var roadmap = roadmapRepository.getReferenceById(id);
        return ResponseEntity.ok(new RoadmapDetail(roadmap));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<RoadmapDetail> crearRoadmap(@AuthenticationPrincipal User user, @RequestBody @Valid RoadmapRegister roadmapRegister, UriComponentsBuilder uriComponentsBuilder) {

        vocationalAccessService.requireCompletedTest(user.getId());
        var message = roadmapRepository.save(new Roadmap(roadmapRegister, user));

        if (roadmapRegister.connections() != null) graphService.initial(message,roadmapRegister.connections());

        var uri = uriComponentsBuilder.path("/roadmap/{id}").buildAndExpand(message.getId()).toUri();

        return ResponseEntity.created(uri).body(new RoadmapDetail(message));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<RoadmapDetail> actualizarRoadmap(@AuthenticationPrincipal User user, @RequestBody RoadmapUpdate roadmapUpdate, @PathVariable Long id) {

        var roadmap = roadmapRepository.getReferenceById(id);

        requireOwner(roadmap, user);
        roadmap.actualizar(roadmapUpdate);

        return ResponseEntity.ok(new RoadmapDetail(roadmap));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarRoadmap(@AuthenticationPrincipal User user, @PathVariable Long id) {
        var roadmap = roadmapRepository.getReferenceById(id);
        requireOwner(roadmap, user);
        graphService.removeRoadmap(id);
        roadmapRepository.delete(roadmap);
        return ResponseEntity.noContent().build();
    }

    private void requireOwner(Roadmap roadmap, User user) {
        if (user == null || roadmap.getUser() == null || !roadmap.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Solo el propietario puede modificar este roadmap");
        }
    }
}
