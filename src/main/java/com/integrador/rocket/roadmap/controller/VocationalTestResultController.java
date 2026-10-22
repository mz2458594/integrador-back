package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import com.integrador.rocket.roadmap.models.vocationaltestresults.dto.*;
import com.integrador.rocket.roadmap.repositories.VocationalQuestionRepository;
import com.integrador.rocket.roadmap.repositories.VocationalTestResultRepository;
import com.integrador.rocket.roadmap.services.VocationalScoringService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/vocational-test-result")
@Transactional(readOnly = true)
public class VocationalTestResultController {
    private final VocationalQuestionRepository questions;
    private final VocationalTestResultRepository results;
    private final VocationalScoringService scoring;
    public VocationalTestResultController(VocationalQuestionRepository questions, VocationalTestResultRepository results, VocationalScoringService scoring) {
        this.questions = questions; this.results = results; this.scoring = scoring;
    }
    @GetMapping("/me")
    public ResponseEntity<VocationalTestResultDetail> current(@AuthenticationPrincipal User user) {
        return results.findByUserId(user.getId()).map(result -> ResponseEntity.ok(new VocationalTestResultDetail(result)))
            .orElseGet(() -> ResponseEntity.noContent().build());
    }
    @GetMapping
    public ResponseEntity<Page<VocationalTestResultList>> list(@AuthenticationPrincipal User user, Pageable pageable) {
        return ResponseEntity.ok(results.findAllByUserId(user.getId(), pageable).map(VocationalTestResultList::new));
    }
    @GetMapping("/{id}")
    public ResponseEntity<VocationalTestResultDetail> detail(@AuthenticationPrincipal User user, @PathVariable Long id) {
        var result = results.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        if (!result.getUser().getId().equals(user.getId())) throw new ResponseStatusException(FORBIDDEN);
        return ResponseEntity.ok(new VocationalTestResultDetail(result));
    }
    @PostMapping
    @Transactional
    public ResponseEntity<VocationalTestResultDetail> create(@AuthenticationPrincipal User user,
            @RequestBody @Valid VocationalTestResultRegister request, UriComponentsBuilder builder) {
        if (results.findByUserId(user.getId()).isPresent())
            throw new ResponseStatusException(CONFLICT, "Ya completaste el test vocacional.");
        var evaluation = scoring.evaluate(questions.findAll(Sort.by("id")), request.vocacionalOptionIds());
        var result = results.saveAndFlush(new VocationalTestResult(user, evaluation));
        return ResponseEntity.created(builder.path("/vocational-test-result/{id}").buildAndExpand(result.getId()).toUri())
            .body(new VocationalTestResultDetail(result));
    }
    // Results are immutable: no client may overwrite the calculated career or remove completion.
}
