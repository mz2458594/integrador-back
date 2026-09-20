package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.conversations.ConversationType;
import com.integrador.rocket.roadmap.models.conversations.dto.ConversationDetail;
import com.integrador.rocket.roadmap.models.conversations.dto.ConversationList;
import com.integrador.rocket.roadmap.models.conversations.dto.ConversationRegister;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.ConversationRepository;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<ConversationList>> listarConversaciones(@PageableDefault(size = 10) Pageable pageable) {
        var conversations = conversationRepository.findAll(pageable).map(ConversationList::new);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<ConversationList>> listarConversacionesPorUsuario(@AuthenticationPrincipal User user, @PageableDefault(size = 10) Pageable pageable){
        var conversations = conversationRepository.findAllByUserId(pageable, user.getId()).map(ConversationList::new);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDetail> detalleConversacion(@PathVariable Long id) {
        var comment = conversationRepository.getReferenceById(id);
        return ResponseEntity.ok(new ConversationDetail(comment));
    }


    @Transactional
    @PostMapping
    public ResponseEntity<ConversationDetail> crearConversacion(@AuthenticationPrincipal User user, @RequestBody @Valid ConversationRegister conversationRegister, UriComponentsBuilder uriComponentsBuilder) {

        List<User> participants = new ArrayList<>();
        participants.add(user);

        var exists = conversationRepository.findDirectConversationBetween(user.getId(), conversationRegister.participantId());

        if (exists.isPresent()) {
            return ResponseEntity.ok(new ConversationDetail(exists.get()));
        }

        if (conversationRegister.type() == ConversationType.DIRECT) {

            if (conversationRegister.participantId() == null){
                throw new IllegalArgumentException("Una conversación directa require de un participante");
            }

            if (conversationRegister.participantId().equals(user.getId())) {
                throw new IllegalArgumentException("No puedes crear una conversación contigo mismo");
            }

            var participant = userRepository.findById(conversationRegister.participantId()).orElseThrow(() -> new RuntimeException("No se encontro el usuario con este ID"));
            participants.add(participant);
        }

        var conversation = conversationRepository.save(new Conversation(conversationRegister, participants));

        var uri = uriComponentsBuilder.path("/conversation/{id}").buildAndExpand(conversation.getId()).toUri();

        return ResponseEntity.created(uri).body(new ConversationDetail(conversation));
    }

//    @Transactional
//    @PutMapping("/{id}")
//    public ResponseEntity<ConversationDetail> actualizarConversacion(@RequestBody ConversationUpdate conversationUpdate, @PathVariable Long id) {
//        var conversation = conversationRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la interacción con este ID"));
//        conversation.actualizar(conversationUpdate);
//        return ResponseEntity.ok(new ConversationDetail(conversation));
//    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarConversacion(@PathVariable Long id) {
        conversationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
