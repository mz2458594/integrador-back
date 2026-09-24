package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.userstepprogress.UserStepProgress;
import com.integrador.rocket.roadmap.models.userstepprogress.dto.UserStepProgressDetail;
import com.integrador.rocket.roadmap.models.userstepprogress.dto.UserStepProgressList;
import com.integrador.rocket.roadmap.models.userstepprogress.dto.UserStepProgressRegister;
import com.integrador.rocket.roadmap.models.userstepprogress.dto.UserStepProgressUpdate;
import com.integrador.rocket.roadmap.repositories.*;
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
@RequestMapping("/user-step-progress")
public class UserStepProgressController {

    @Autowired
    private UserStepProgressRepository userStepProgressRepository;

    @Autowired
    private RoadmapStepsRepository roadmapStepsRepository;


    @GetMapping
    public ResponseEntity<Page<UserStepProgressList>> listarUserStepProgress(@PageableDefault(size = 10) Pageable pageable) {
        var userStepProgressLists = userStepProgressRepository.findAll(pageable).map(UserStepProgressList::new);
        return ResponseEntity.ok(userStepProgressLists);
    }

    @GetMapping("/roadmap-step/{id}")
    public ResponseEntity<Page<UserStepProgressList>> listarUserStepProgressPorUserRoadmapId(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        var userStepProgress = userStepProgressRepository.findAllByRoadmapStepId(pageable, id).map(UserStepProgressList::new);
        return ResponseEntity.ok(userStepProgress);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<UserStepProgressList>> listarUserStepProgressPorUserId(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        var userStepProgress = userStepProgressRepository.findAllByUserId(pageable, id).map(UserStepProgressList::new);
        return ResponseEntity.ok(userStepProgress);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStepProgressDetail> detalleUserStepProgressId(@PathVariable Long id) {
        var userStepProgress = userStepProgressRepository.getReferenceById(id);
        return ResponseEntity.ok(new UserStepProgressDetail(userStepProgress));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<UserStepProgressDetail> crearUserStepProgress(@AuthenticationPrincipal User user, @RequestBody @Valid UserStepProgressRegister userStepProgressRegister, UriComponentsBuilder uriComponentsBuilder) {

        var roadmapStep = roadmapStepsRepository.getReferenceById(userStepProgressRegister.roadmapStepId());

        var userStepProgress = userStepProgressRepository.save(new UserStepProgress(userStepProgressRegister, roadmapStep, user));

        var uri = uriComponentsBuilder.path("/user-step-progress/{id}").buildAndExpand(userStepProgress.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserStepProgressDetail(userStepProgress));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<UserStepProgressDetail> actualizarUserStepProgress(@RequestBody UserStepProgressUpdate userStepProgressUpdate, @PathVariable Long id) {

        var userStepProgress = userStepProgressRepository.getReferenceById(id);

        userStepProgress.actualizar(userStepProgressUpdate);

        return ResponseEntity.ok(new UserStepProgressDetail(userStepProgress));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarUserStepProgress(@PathVariable Long id) {
        userStepProgressRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
