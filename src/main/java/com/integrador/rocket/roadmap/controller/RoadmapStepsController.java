package com.integrador.rocket.roadmap.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/roadmap-step")
@SecurityRequirement(name = "bearer-key")
public class RoadmapStepsController {

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

        var roadmapStep = roadmapStepsRepository.save(new RoadmapStep(roadmapStepRegister, roadmap));

        var uri = uriComponentsBuilder.path("/roadmap-step/{id}").buildAndExpand(roadmapStep.getId()).toUri();

        return ResponseEntity.created(uri).body(new RoadmapStepDetail(roadmapStep));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<RoadmapStepDetail> actualizarRoadmapStep(@RequestBody RoadmapStepUpdate roadmapStepUpdate, @PathVariable Long id) {

        var roadmapStep = roadmapStepsRepository.getReferenceById(id);

        roadmapStep.actualizar(roadmapStepUpdate);

        return ResponseEntity.ok(new RoadmapStepDetail(roadmapStep));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarRoadmapStep(@PathVariable Long id) {
        roadmapStepsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
