package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.dto.VocationalQuestionDetail;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.dto.VocationalQuestionList;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.dto.VocationalQuestionRegister;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.dto.VocationalQuestionUpdate;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionRegister;
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

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/vocational-question")
@SecurityRequirement(name = "bearer-key")
@RestController
public class VocacionalQuestionController {

    @Autowired
    private VocationalQuestionRepository vocationalQuestionRepository;

    @Autowired
    private VocationalOptionRepository vocationalOptionRepository;


    @GetMapping
    public ResponseEntity<Page<VocationalQuestionList>> listarVocationalQuestions(@PageableDefault(size = 10) Pageable pageable) {
        var vocationalQuestionLists = vocationalQuestionRepository.findAll(pageable).map(VocationalQuestionList::new);
        return ResponseEntity.ok(vocationalQuestionLists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VocationalQuestionDetail> detalleVocationalQuestion(@PathVariable Long id) {
        var vocationalQuestion = vocationalQuestionRepository.getReferenceById(id);
        return ResponseEntity.ok(new VocationalQuestionDetail(vocationalQuestion, vocationalQuestion.getVocationalOptions()));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<VocationalQuestionDetail> crearVocationalQuestion(@AuthenticationPrincipal User user, @RequestBody @Valid VocationalQuestionRegister vocationalQuestionRegister, UriComponentsBuilder uriComponentsBuilder) {

        var vocationalQuestion = vocationalQuestionRepository.save(new VocationalQuestion(vocationalQuestionRegister));

        List<VocationalOption> vocationalOptions = new ArrayList<>();

        if (vocationalQuestionRegister.vocationalOptionRegisters() != null) {
            for (VocationalOptionRegister v: vocationalQuestionRegister.vocationalOptionRegisters()){
                var option = vocationalOptionRepository.save(new VocationalOption(v, vocationalQuestion));
                vocationalOptions.add(option);
            }
        }

        var uri = uriComponentsBuilder.path("/vocational-question/{id}").buildAndExpand(vocationalQuestion.getId()).toUri();

        return ResponseEntity.created(uri).body(new VocationalQuestionDetail(vocationalQuestion, vocationalOptions));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<VocationalQuestionDetail> actualizarVocationalQuestion(@RequestBody VocationalQuestionUpdate vocationalQuestionUpdate, @PathVariable Long id) {

        var vocationalQuestion = vocationalQuestionRepository.getReferenceById(id);

        vocationalQuestion.actualizar(vocationalQuestionUpdate);

        return ResponseEntity.ok(new VocationalQuestionDetail(vocationalQuestion, vocationalQuestion.getVocationalOptions()));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarVocationalQuestion(@PathVariable Long id) {
        vocationalQuestionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
