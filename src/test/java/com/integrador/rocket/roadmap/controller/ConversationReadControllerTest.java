package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.models.conversations.*;
import com.integrador.rocket.roadmap.models.messages.Message;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
import org.junit.jupiter.api.*;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversationReadControllerTest {
    ConversationReadStateRepository states = mock(ConversationReadStateRepository.class);
    ConversationRepository conversations = mock(ConversationRepository.class);
    MessageRepository messages = mock(MessageRepository.class);
    UserRepository users = mock(UserRepository.class);
    ConversationReadController controller = new ConversationReadController(states, conversations, messages, users);
    User user = mock(User.class);
    Conversation conversation = mock(Conversation.class);
    Message message = mock(Message.class);
    @BeforeEach void setup() {
        when(user.getId()).thenReturn(1L);
        when(users.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(conversation.getId()).thenReturn(2L);
        when(conversation.getParticipants()).thenReturn(List.of(user));
        when(conversations.findById(2L)).thenReturn(Optional.of(conversation));
        when(message.getId()).thenReturn(100L);
        when(message.getConversation()).thenReturn(conversation);
        when(messages.findById(100L)).thenReturn(Optional.of(message));
    }
    @Test void persistsPerUserAndNeverMovesBackwards() {
        var state = new ConversationReadState(user, conversation);
        state.markRead(200);
        when(states.findByUserIdAndConversationId(1L, 2L)).thenReturn(Optional.of(state));
        assertEquals(200L, controller.mark(user, 2L, new ConversationReadController.ReadRequest(100L)).lastReadMessageId());
        verify(states).save(state);
    }
    @Test void firstReadCreatesPersistentState() {
        when(states.findByUserIdAndConversationId(1L, 2L)).thenReturn(Optional.empty());
        assertEquals(100L, controller.mark(user, 2L, new ConversationReadController.ReadRequest(100L)).lastReadMessageId());
        verify(states).save(any(ConversationReadState.class));
    }
    @Test void outsidersCannotMarkMessagesRead() {
        when(conversation.getParticipants()).thenReturn(List.of());
        assertThrows(AccessDeniedException.class, () -> controller.mark(user, 2L, new ConversationReadController.ReadRequest(100L)));
        verifyNoInteractions(states);
    }
    @Test void rejectsMessageFromAnotherConversation() {
        var other = mock(Conversation.class);
        when(other.getId()).thenReturn(3L);
        when(message.getConversation()).thenReturn(other);
        assertThrows(IllegalArgumentException.class, () -> controller.mark(user, 2L, new ConversationReadController.ReadRequest(100L)));
        verifyNoInteractions(states);
    }
    @Test void listOnlyReturnsAuthenticatedUsersMarkers() {
        var state = new ConversationReadState(user, conversation);
        state.markRead(100);
        when(states.findAllByUserId(1L)).thenReturn(List.of(state));
        assertEquals(List.of(new ConversationReadController.ReadState(2L, 100L)), controller.list(user));
        verify(states).findAllByUserId(1L);
    }
}
