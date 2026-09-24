package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.conversations.ConversationType;
import com.integrador.rocket.roadmap.models.messages.Message;
import com.integrador.rocket.roadmap.models.messages.dto.MessageDetail;
import com.integrador.rocket.roadmap.models.messages.dto.MessageList;
import com.integrador.rocket.roadmap.models.messages.dto.MessageRegister;
import com.integrador.rocket.roadmap.models.messages.dto.MessageUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.ConversationRepository;
import com.integrador.rocket.roadmap.repositories.MessageRepository;
import com.integrador.rocket.roadmap.services.AiService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/message")
@SecurityRequirement(name = "bearer-key")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private AiService aiService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public ResponseEntity<Page<MessageList>> listarMensajes(@PageableDefault(size = 10) Pageable pageable) {
        var messages = messageRepository.findAll(pageable).map(MessageList::new);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation/{id}")
    public ResponseEntity<Page<MessageList>> listarMensajesPorConversacionId(@PathVariable Long id, @PageableDefault(size = 10) Pageable pageable) {
        var messages = messageRepository.findAllByConversationId(pageable, id).map(MessageList::new);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDetail> detalleMensaje(@PathVariable Long id) {
        var message = messageRepository.getReferenceById(id);
        return ResponseEntity.ok(new MessageDetail(message));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<MessageDetail> crearMensaje(@AuthenticationPrincipal User user, @RequestBody @Valid MessageRegister messageRegister, UriComponentsBuilder uriComponentsBuilder) {

        var conversation = conversationRepository.getReferenceById(messageRegister.conversation_id());

        if (!conversation.getParticipants().contains(user)){
            throw new AccessDeniedException("No perteneces a esta conversación");
        }

        var message = messageRepository.save(new Message(messageRegister.content(), conversation, user));

        if (conversation.getType() == ConversationType.AI_AGENT){
            String aiReply = aiService.generarRespuesta(conversation, messageRegister.content(), user.getId());
            messageRepository.save(new Message(aiReply, conversation, null));
        }

        messagingTemplate.convertAndSend("/topic/conversations" + conversation.getId(), new MessageDetail(message));

        var uri = uriComponentsBuilder.path("/message/{id}").buildAndExpand(message.getId()).toUri();

        return ResponseEntity.created(uri).body(new MessageDetail(message));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<MessageDetail> actualizarMensaje(@RequestBody MessageUpdate messageUpdate, @PathVariable Long id) {

        // Añadir regla de 1 min para editar el mensaje
        var message = messageRepository.getReferenceById(id);
        if (message.getConversation().getType() == ConversationType.AI_AGENT){
            throw new AccessDeniedException("No se puede editar el mensaje en conversaciones con el Agente IA");
        }
        message.actualizar(messageUpdate);
        return ResponseEntity.ok(new MessageDetail(message));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarMensaje(@PathVariable Long id) {
        messageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
