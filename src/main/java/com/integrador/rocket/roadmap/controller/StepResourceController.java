package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.stepresources.StepResource;
import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesDetail;
import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesList;
import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesRegister;
import com.integrador.rocket.roadmap.models.stepresources.dto.StepResourcesUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.RoadmapStepsRepository;
import com.integrador.rocket.roadmap.repositories.StepResourcesRepository;
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
@RequestMapping("/step-resource")
@SecurityRequirement(name = "bearer-key")
public class StepResourceController {

    @Autowired
    private RoadmapStepsRepository roadmapStepsRepository;

    @Autowired
    private StepResourcesRepository stepResourcesRepository;


    @GetMapping
    public ResponseEntity<Page<StepResourcesList>> listarStepResources(@PageableDefault(size = 10) Pageable pageable) {
        var stepResourcesLists = stepResourcesRepository.findAll(pageable).map(StepResourcesList::new);
        return ResponseEntity.ok(stepResourcesLists);
    }

    @GetMapping("/roadmap-step/{id}")
    public ResponseEntity<Page<StepResourcesList>> listarStepsResourcesPorRoadmapStepId(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        var roadmapSteps = stepResourcesRepository.findAllByRoadmapStepId(pageable, id).map(StepResourcesList::new);
        return ResponseEntity.ok(roadmapSteps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StepResourcesDetail> detalleStepResource(@PathVariable Long id) {
        var roadmapStep = stepResourcesRepository.getReferenceById(id);
        return ResponseEntity.ok(new StepResourcesDetail(roadmapStep));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<StepResourcesDetail> crearStepResource(@AuthenticationPrincipal User user, @RequestBody @Valid StepResourcesRegister stepResourcesRegister, UriComponentsBuilder uriComponentsBuilder) {

        var roadmapStep = roadmapStepsRepository.getReferenceById(stepResourcesRegister.roadmapStepId());

        var stepResource = stepResourcesRepository.save(new StepResource(stepResourcesRegister, roadmapStep));

        var uri = uriComponentsBuilder.path("/step-resource/{id}").buildAndExpand(stepResource.getId()).toUri();

        return ResponseEntity.created(uri).body(new StepResourcesDetail(stepResource));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<StepResourcesDetail> actualizarStepResource(@RequestBody StepResourcesUpdate stepResourcesUpdate, @PathVariable Long id) {

        var stepResource = stepResourcesRepository.getReferenceById(id);

        stepResource.actualizar(stepResourcesUpdate);

        return ResponseEntity.ok(new StepResourcesDetail(stepResource));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarStepResource(@PathVariable Long id) {
        stepResourcesRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
