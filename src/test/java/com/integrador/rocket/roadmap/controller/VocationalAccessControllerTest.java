package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.infra.exceptions.GestorDeErrores;
import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.conversations.ConversationType;
import com.integrador.rocket.roadmap.models.messages.Message;
import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import com.integrador.rocket.roadmap.repositories.*;
import com.integrador.rocket.roadmap.services.AiService;
import com.integrador.rocket.roadmap.services.VocationalAccessService;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VocationalAccessControllerTest {
    private MockMvc mvc;
    private User user;
    private VocationalTestResultRepository results;
    private RoadmapRepository roadmaps;
    private ConversationRepository conversations;
    private MessageRepository messages;
    private AiRepository interactions;
    private SimpMessagingTemplate socket;
    private AiService ai;
    private Conversation conversation;

    @BeforeEach
    void setup() {
        user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        results = mock(VocationalTestResultRepository.class);
        when(results.findByUserId(7L)).thenReturn(Optional.empty());
        var access = new VocationalAccessService(results);
        roadmaps = mock(RoadmapRepository.class);
        conversations = mock(ConversationRepository.class);
        messages = mock(MessageRepository.class);
        interactions = mock(AiRepository.class);
        socket = mock(SimpMessagingTemplate.class);
        ai = mock(AiService.class);
        var roadmapController = new RoadmapController();
        ReflectionTestUtils.setField(roadmapController, "roadmapRepository", roadmaps);
        var conversationController = new ConversationController();
        ReflectionTestUtils.setField(conversationController, "conversationRepository", conversations);
        ReflectionTestUtils.setField(conversationController, "messagingTemplate", socket);
        var messageController = new MessageController();
        ReflectionTestUtils.setField(messageController, "messageRepository", messages);
        ReflectionTestUtils.setField(messageController, "conversationRepository", conversations);
        ReflectionTestUtils.setField(messageController, "messagingTemplate", socket);
        ReflectionTestUtils.setField(messageController, "aiService", ai);
        var aiController = new AiInteractionController();
        ReflectionTestUtils.setField(aiController, "aiRepository", interactions);
        for (Object controller : List.of(roadmapController, conversationController, messageController, aiController)) {
            ReflectionTestUtils.setField(controller, "vocationalAccessService", access);
        }
        conversation = mock(Conversation.class);
        when(conversation.getId()).thenReturn(10L);
        when(conversation.getType()).thenReturn(ConversationType.DIRECT);
        when(conversation.getParticipants()).thenReturn(List.of(user));
        when(conversation.getMessages()).thenReturn(List.of());
        when(conversations.getReferenceById(10L)).thenReturn(conversation);
        when(conversations.findById(10L)).thenReturn(Optional.of(conversation));
        mvc = MockMvcBuilders.standaloneSetup(roadmapController, conversationController, messageController, aiController)
                .setControllerAdvice(new GestorDeErrores())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
    }

    @AfterEach
    void clearSecurity() { SecurityContextHolder.clearContext(); }

    private void completed() {
        when(results.findByUserId(7L)).thenReturn(Optional.of(mock(VocationalTestResult.class)));
    }

    @Test
    void roadmapCreationWithoutTestReturns403BeforeSaving() throws Exception {
        mvc.perform(post("/roadmap").contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Java","description":"Backend","category":"Software","isPublic":false,"createdVia":"MANUAL"}
                """))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("test vocacional")));
        verifyNoInteractions(roadmaps);
    }

    @Test
    void directConversationWithoutTestCannotCreateOrReuseAChat() throws Exception {
        mvc.perform(post("/conversation").contentType(MediaType.APPLICATION_JSON).content("""
                {"type":"DIRECT","participantId":8}
                """)) .andExpect(status().isForbidden());
        verify(conversations, never()).save(any());
        verify(conversations, never()).findDirectConversationBetween(anyLong(), anyLong());
    }

    @Test
    void messagesInExistingDirectAndAiConversationsAreBlockedBeforeSavingOrBroadcasting() throws Exception {
        for (var type : ConversationType.values()) {
            when(conversation.getType()).thenReturn(type);
            mvc.perform(post("/message").contentType(MediaType.APPLICATION_JSON).content("""
                    {"content":"Hola","conversation_id":10}
                    """)) .andExpect(status().isForbidden());
        }
        verify(messages, never()).save(any());
        verifyNoInteractions(socket, ai);
    }

    @Test
    void typingAndEditingCannotBypassThePrerequisite() throws Exception {
        mvc.perform(post("/conversation/10/typing").contentType(MediaType.APPLICATION_JSON).content("""
                {"typing":true}
                """)) .andExpect(status().isForbidden());
        var message = mock(Message.class);
        when(message.getConversation()).thenReturn(conversation);
        when(messages.getReferenceById(12L)).thenReturn(message);
        mvc.perform(put("/message/12").contentType(MediaType.APPLICATION_JSON).content("""
                {"content":"Otro mensaje"}
                """)) .andExpect(status().isForbidden());
        verify(message, never()).actualizar(any());
        verifyNoInteractions(socket);
    }

    @Test
    void aiRoadmapInteractionCannotBypassThePrerequisite() throws Exception {
        mvc.perform(post("/ai-interaction").contentType(MediaType.APPLICATION_JSON).content("""
                {"purpose":"ROADMAP_CREATION","user_id":999}
                """)) .andExpect(status().isForbidden());
        verifyNoInteractions(interactions);
        verify(results).findByUserId(7L);
    }

    @Test
    void completedUserCanCreateRoadmaps() throws Exception {
        completed();
        when(roadmaps.save(any(Roadmap.class))).thenAnswer(invocation -> {
            Roadmap saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 2L);
            return saved;
        });
        mvc.perform(post("/roadmap").contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Java","description":"Backend","category":"Software","isPublic":false,"createdVia":"MANUAL"}
                """)) .andExpect(status().isCreated());
        verify(roadmaps).save(any(Roadmap.class));
    }

    @Test
    void completedUserCanReuseDirectChatAndSendMessages() throws Exception {
        completed();
        when(conversations.findDirectConversationBetween(7L, 8L)).thenReturn(Optional.of(conversation));
        mvc.perform(post("/conversation").contentType(MediaType.APPLICATION_JSON).content("""
                {"type":"DIRECT","participantId":8}
                """)) .andExpect(status().isOk());
        when(messages.save(any(Message.class))).thenAnswer(invocation -> {
            Message saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 12L);
            return saved;
        });
        mvc.perform(post("/message").contentType(MediaType.APPLICATION_JSON).content("""
                {"content":"Hola","conversation_id":10}
                """)) .andExpect(status().isCreated());
        verify(messages).save(any(Message.class));
    }
}
