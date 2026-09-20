package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import com.integrador.rocket.roadmap.models.vocationaltestresults.dto.VocationalTestResultDetail;
import com.integrador.rocket.roadmap.models.vocationaltestresults.dto.VocationalTestResultList;
import com.integrador.rocket.roadmap.models.vocationaltestresults.dto.VocationalTestResultRegister;
import com.integrador.rocket.roadmap.models.vocationaltestresults.dto.VocationalTestResultUpdate;
import com.integrador.rocket.roadmap.repositories.VocationalOptionRepository;
import com.integrador.rocket.roadmap.repositories.VocationalTestResultRepository;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/vocational-test-result")
@SecurityRequirement(name = "bearer-key")
public class VocationalTestResultController {

    @Autowired
    private VocationalOptionRepository vocationalOptionRepository;

    @Autowired
    private VocationalTestResultRepository vocationalTestResultRepository;

    @GetMapping
    public ResponseEntity<Page<VocationalTestResultList>> listarVocationalResults(@PageableDefault(size = 10) Pageable pageable) {
        var vocationalTestResultList = vocationalTestResultRepository.findAll(pageable).map(VocationalTestResultList::new);
        return ResponseEntity.ok(vocationalTestResultList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VocationalTestResultDetail> detalleVocationalResult(@PathVariable Long id) {
        var vocationalTestResult = vocationalTestResultRepository.getReferenceById(id);
        return ResponseEntity.ok(new VocationalTestResultDetail(vocationalTestResult));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<VocationalTestResultDetail> crearVocationalResult(@AuthenticationPrincipal User user, @RequestBody @Valid VocationalTestResultRegister vocationalTestResultRegister, UriComponentsBuilder uriComponentsBuilder) {

        List<VocationalOption> vocationalOptions = new ArrayList<>();
        if (vocationalTestResultRegister.vocacionalOptionIds() != null) {
            for (Long id : vocationalTestResultRegister.vocacionalOptionIds()) {
                var option = vocationalOptionRepository.getReferenceById(id);
                vocationalOptions.add(option);
            }
        }

        var vocationalTestResult = vocationalTestResultRepository.save(new VocationalTestResult(vocationalTestResultRegister, vocationalOptions, user));

        var uri = uriComponentsBuilder.path("/vocational-test-result/{id}").buildAndExpand(vocationalTestResult.getId()).toUri();

        return ResponseEntity.created(uri).body(new VocationalTestResultDetail(vocationalTestResult));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<VocationalTestResultDetail> actualizarVocationalResult(@RequestBody VocationalTestResultUpdate vocationalTestResultUpdate, @PathVariable Long id) {

        var vocationalTestResult = vocationalTestResultRepository.getReferenceById(id);

        vocationalTestResult.actualizar(vocationalTestResult);

        return ResponseEntity.ok(new VocationalTestResultDetail(vocationalTestResult));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarVocationalResults(@PathVariable Long id) {
        vocationalTestResultRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
