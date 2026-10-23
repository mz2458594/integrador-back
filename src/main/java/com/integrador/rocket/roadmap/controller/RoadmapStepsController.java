package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepDetail;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepList;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepRegister;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.RoadmapRepository;
import com.integrador.rocket.roadmap.repositories.RoadmapStepsRepository;
import com.integrador.rocket.roadmap.repositories.UserRepository;
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
@RequestMapping("/roadmap-step")
public class RoadmapStepsController {

    @Autowired
    private com.integrador.rocket.roadmap.services.RoadmapGraphService graphService;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoadmapStepsRepository roadmapStepsRepository;


    @GetMapping
    public ResponseEntity<Page<RoadmapStepList>> listarRoadmapsSteps(@PageableDefault(size = 10) Pageable pageable) {
        var roadmapSteps = roadmapStepsRepository.findAll(pageable).map(RoadmapStepList::new);
        return ResponseEntity.ok(roadmapSteps);
    }

    @GetMapping("/roadmap/{id}")
    public ResponseEntity<Page<RoadmapStepList>> listarRoadmapStepsPorRoadmapId(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        var roadmapSteps = roadmapStepsRepository.findAllByRoadmapId(pageable, id).map(RoadmapStepList::new);
        return ResponseEntity.ok(roadmapSteps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoadmapStepDetail> detalleRoadmapStep(@PathVariable Long id) {
        var roadmapStep = roadmapStepsRepository.getReferenceById(id);
        return ResponseEntity.ok(new RoadmapStepDetail(roadmapStep));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<RoadmapStepDetail> crearRoadmapStep(@AuthenticationPrincipal User user, @RequestBody @Valid RoadmapStepRegister roadmapStepRegister, UriComponentsBuilder uriComponentsBuilder) {

        var roadmap = roadmapRepository.getReferenceById(roadmapStepRegister.roadmapId());

        requireOwner(roadmap, user);
        var roadmapStep = roadmapStepsRepository.save(new RoadmapStep(roadmapStepRegister, roadmap));

        var uri = uriComponentsBuilder.path("/roadmap-step/{id}").buildAndExpand(roadmapStep.getId()).toUri();

        return ResponseEntity.created(uri).body(new RoadmapStepDetail(roadmapStep));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<RoadmapStepDetail> actualizarRoadmapStep(@AuthenticationPrincipal User user, @RequestBody RoadmapStepUpdate roadmapStepUpdate, @PathVariable Long id) {

        var roadmapStep = roadmapStepsRepository.getReferenceById(id);

        requireOwner(roadmapStep.getRoadmap(), user);
        if ((roadmapStepUpdate.title() != null && roadmapStepUpdate.title().isBlank())
                || (roadmapStepUpdate.description() != null && roadmapStepUpdate.description().isBlank())) {
            throw new jakarta.validation.ValidationException("El titulo y la descripcion no pueden estar vacios");
        }
        roadmapStep.actualizar(roadmapStepUpdate);

        return ResponseEntity.ok(new RoadmapStepDetail(roadmapStep));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarRoadmapStep(@AuthenticationPrincipal User user, @PathVariable Long id) {
        var step = roadmapStepsRepository.getReferenceById(id);
        requireOwner(step.getRoadmap(), user);
        graphService.removeStep(step);
        roadmapStepsRepository.delete(step);
        return ResponseEntity.noContent().build();
    }

    private void requireOwner(Roadmap roadmap, User user) {
        if (user == null || roadmap.getUser() == null || !roadmap.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Solo el propietario puede modificar los pasos");
        }
    }
}
