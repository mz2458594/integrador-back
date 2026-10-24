package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.conversations.ConversationReadState;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RestController
@RequestMapping("/conversation")
public class ConversationReadController {
    private final ConversationReadStateRepository states;
    private final ConversationRepository conversations;
    private final MessageRepository messages;
    private final UserRepository users;

    public ConversationReadController(ConversationReadStateRepository states, ConversationRepository conversations,
                                      MessageRepository messages, UserRepository users) {
        this.states = states;
        this.conversations = conversations;
        this.messages = messages;
        this.users = users;
    }

    public record ReadState(Long conversationId, Long lastReadMessageId) {}
    public record ReadRequest(@NotNull @Positive Long lastReadMessageId) {}

    @GetMapping("/read-state")
    @Transactional(readOnly = true)
    public List<ReadState> list(@AuthenticationPrincipal User user) {
        return states.findAllByUserId(user.getId()).stream()
            .filter(state -> state.getConversation().getParticipants().stream().anyMatch(p -> p.getId().equals(user.getId())))
            .map(state -> new ReadState(state.getConversation().getId(), state.getLastReadMessageId())).toList();
    }

    @PutMapping("/{id}/read")
    @Transactional
    public ReadState mark(@AuthenticationPrincipal User user, @PathVariable Long id,
                          @RequestBody @Valid ReadRequest request) {
        // Serializes first inserts and concurrent updates from multiple devices for this user.
        var lockedUser = users.findByIdForUpdate(user.getId()).orElseThrow(EntityNotFoundException::new);
        var conversation = conversations.findById(id).orElseThrow(EntityNotFoundException::new);
        if (conversation.getParticipants().stream().noneMatch(p -> p.getId().equals(user.getId()))) {
            throw new AccessDeniedException("No perteneces a esta conversacion");
        }
        var message = messages.findById(request.lastReadMessageId()).orElseThrow(EntityNotFoundException::new);
        if (!message.getConversation().getId().equals(id)) {
            throw new IllegalArgumentException("El mensaje no pertenece a esta conversacion");
        }
        var state = states.findByUserIdAndConversationId(user.getId(), id)
            .orElseGet(() -> new ConversationReadState(lockedUser, conversation));
        state.markRead(message.getId());
        states.save(state);
        return new ReadState(id, state.getLastReadMessageId());
    }
}
