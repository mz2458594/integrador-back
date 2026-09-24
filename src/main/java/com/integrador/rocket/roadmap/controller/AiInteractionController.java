package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.ai.AiInteraction;
import com.integrador.rocket.roadmap.models.ai.dto.AiDetail;
import com.integrador.rocket.roadmap.models.ai.dto.AiList;
import com.integrador.rocket.roadmap.models.ai.dto.AiRegister;
import com.integrador.rocket.roadmap.models.ai.dto.AiUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.AiRepository;
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
@RequestMapping("/ai-interaction")
public class AiInteractionController {

    @Autowired
    private AiRepository aiRepository;

    @GetMapping
    public ResponseEntity<Page<AiList>> listarInteracciones(@PageableDefault(size = 10) Pageable pageable) {
        var aiInteractions = aiRepository.findAll(pageable).map(AiList::new);
        return ResponseEntity.ok(aiInteractions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AiDetail> detalleInteraccion(@PathVariable Long id) {
        var aiInteraction = aiRepository.getReferenceById(id);
        return ResponseEntity.ok(new AiDetail(aiInteraction));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<AiDetail> crearInteraccion(@AuthenticationPrincipal User user, @RequestBody @Valid AiRegister aiRegister, UriComponentsBuilder uriComponentsBuilder) {
        var aiInteraction = aiRepository.save(new AiInteraction(aiRegister, user));

        var uri = uriComponentsBuilder.path("/ai-interaction/{id}").buildAndExpand(aiInteraction.getId()).toUri();

        return ResponseEntity.created(uri).body(new AiDetail(aiInteraction));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<AiDetail> actualizarInteraccion(@RequestBody AiUpdate userUpdate, @PathVariable Long id) {
        var aiInteraction = aiRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la interacción con este ID"));
        aiInteraction.actualizar(userUpdate);
        return ResponseEntity.ok(new AiDetail(aiInteraction));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarInteracción(@PathVariable Long id) {
        aiRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
