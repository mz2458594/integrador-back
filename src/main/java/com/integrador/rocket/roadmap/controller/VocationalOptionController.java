package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionDetail;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionList;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionRegister;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionUpdate;
import com.integrador.rocket.roadmap.repositories.VocationalOptionRepository;
import com.integrador.rocket.roadmap.repositories.VocationalQuestionRepository;
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
@RequestMapping("/vocational-option")
public class VocationalOptionController {
    @Autowired
    private VocationalQuestionRepository vocationalQuestionRepository;

    @Autowired
    private VocationalOptionRepository vocationalOptionRepository;


    @GetMapping
    public ResponseEntity<Page<VocationalOptionList>> listarVocationalOptions(@PageableDefault(size = 10) Pageable pageable) {
        var vocationalOptionLists = vocationalOptionRepository.findAll(pageable).map(VocationalOptionList::new);
        return ResponseEntity.ok(vocationalOptionLists);
    }

    @GetMapping("/vocational-question/{id}")
    public ResponseEntity<Page<VocationalOptionList>> listarVocationalOptionPorQuestionId(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        var vocationalOptionLists = vocationalOptionRepository.findAllByQuestionId(pageable, id).map(VocationalOptionList::new);
        return ResponseEntity.ok(vocationalOptionLists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VocationalOptionDetail> detalleVocationalOption(@PathVariable Long id) {
        var vocationalOption = vocationalOptionRepository.getReferenceById(id);
        return ResponseEntity.ok(new VocationalOptionDetail(vocationalOption));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<VocationalOptionDetail> crearVocationalOption(@AuthenticationPrincipal User user, @RequestBody @Valid VocationalOptionRegister vocationalOptionRegister, UriComponentsBuilder uriComponentsBuilder) {


        var vocationalQuestion = vocationalQuestionRepository.getReferenceById(vocationalOptionRegister.questionId());
        var vocationalOption = vocationalOptionRepository.save(new VocationalOption(vocationalOptionRegister, vocationalQuestion));

        var uri = uriComponentsBuilder.path("/vocational-option/{id}").buildAndExpand(vocationalOption.getId()).toUri();

        return ResponseEntity.created(uri).body(new VocationalOptionDetail(vocationalOption));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<VocationalOptionDetail> actualizarVocationalOption(@RequestBody VocationalOptionUpdate vocationalOptionUpdate, @PathVariable Long id) {

        var vocationalOption = vocationalOptionRepository.getReferenceById(id);

        vocationalOption.actualizar(vocationalOptionUpdate);

        return ResponseEntity.ok(new VocationalOptionDetail(vocationalOption));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarVocationalOption(@PathVariable Long id) {
        vocationalOptionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
