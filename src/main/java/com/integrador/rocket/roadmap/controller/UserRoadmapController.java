package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.userroadmaps.UserRoadmap;
import com.integrador.rocket.roadmap.models.userroadmaps.dto.UserRoadmapDetail;
import com.integrador.rocket.roadmap.models.userroadmaps.dto.UserRoadmapList;
import com.integrador.rocket.roadmap.models.userroadmaps.dto.UserRoadmapRegister;
import com.integrador.rocket.roadmap.models.userroadmaps.dto.UserRoadmapUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.RoadmapRepository;
import com.integrador.rocket.roadmap.repositories.UserRoadmapRepository;
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
@RequestMapping("/user-roadmap")
public class UserRoadmapController {

    @Autowired
    private UserRoadmapRepository userRoadmapRepository;

    @Autowired
    private RoadmapRepository roadmapRepository;

    @GetMapping
    public ResponseEntity<Page<UserRoadmapList>> listarUserRoadmaps(@PageableDefault(size = 10) Pageable pageable) {
        var userRoadmapLists = userRoadmapRepository.findAll(pageable).map(UserRoadmapList::new);
        return ResponseEntity.ok(userRoadmapLists);
    }

    @GetMapping("/roadmap/{id}")
    public ResponseEntity<Page<UserRoadmapDetail>> listarUserRoadmapPorRoadmapId(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        var tags = userRoadmapRepository.findAllByRoadmapId(pageable, id).map(UserRoadmapDetail::new);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRoadmapDetail> detalleUserRoadmap(@PathVariable Long id) {
        var userRoadmap = userRoadmapRepository.getReferenceById(id);
        return ResponseEntity.ok(new UserRoadmapDetail(userRoadmap));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<UserRoadmapDetail> crearUserRoadmap(@AuthenticationPrincipal User user, @RequestBody @Valid UserRoadmapRegister userRoadmapRegister, UriComponentsBuilder uriComponentsBuilder) {

        var roadmap = roadmapRepository.getReferenceById(userRoadmapRegister.roadmapId());
        var userRoadmap = userRoadmapRepository.save(new UserRoadmap(userRoadmapRegister, user, roadmap));

        var uri = uriComponentsBuilder.path("/user-roadmap/{id}").buildAndExpand(userRoadmap.getId()).toUri();

        return ResponseEntity.created(uri).body(new UserRoadmapDetail(userRoadmap));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<UserRoadmapDetail> actualizarUserRoadmap(@RequestBody UserRoadmapUpdate userRoadmapUpdate, @PathVariable Long id) {

        var userRoadmap = userRoadmapRepository.getReferenceById(id);

        userRoadmap.actualizar(userRoadmapUpdate);

        return ResponseEntity.ok(new UserRoadmapDetail(userRoadmap));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarUserRoadmap(@PathVariable Long id) {
        userRoadmapRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
