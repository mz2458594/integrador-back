package com.integrador.rocket.roadmap.services;

import com.integrador.rocket.roadmap.infra.exceptions.VocationalTestRequiredException;
import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.messages.Message;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import com.integrador.rocket.roadmap.repositories.MessageRepository;
import com.integrador.rocket.roadmap.repositories.VocationalTestResultRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class AiServiceTest {
    @Test
    void modelReceivesVocationalResultRecentHistoryAndCurrentContent() {
        var model = mock(ChatModel.class);
        var messages = mock(MessageRepository.class);
        var results = mock(VocationalTestResultRepository.class);
        var result = mock(VocationalTestResult.class);
        when(result.getSuggestedCareer()).thenReturn("Ingenieria de Software");
        when(results.findByUserId(7L)).thenReturn(Optional.of(result));
        var conversation = mock(Conversation.class);
        when(conversation.getId()).thenReturn(10L);
        var recent = mock(Message.class);
        when(recent.getContent()).thenReturn("Ultima respuesta del agente");
        when(recent.isFromAgent()).thenReturn(true);
        var older = mock(Message.class);
        when(older.getContent()).thenReturn("Tengo cuatro horas semanales");
        when(messages.findAllByConversationId(any(Pageable.class), eq(10L)))
                .thenReturn(new PageImpl<>(List.of(recent, older)));
        when(model.call(anyString())).thenReturn("Respuesta personalizada");
        var service = new AiService(model, messages, results);
        assertEquals("Respuesta personalizada", service.generarRespuesta(conversation, "Quiero aprender Java", 7L));
        var prompt = ArgumentCaptor.forClass(String.class);
        verify(model).call(prompt.capture());
        assertTrue(prompt.getValue().contains("Ingenieria de Software"));
        assertTrue(prompt.getValue().contains("Quiero aprender Java"));
        assertTrue(prompt.getValue().contains("Tengo cuatro horas semanales"));
        assertTrue(prompt.getValue().indexOf("Tengo cuatro horas semanales") < prompt.getValue().indexOf("Ultima respuesta del agente"));
        var pagination = ArgumentCaptor.forClass(Pageable.class);
        verify(messages).findAllByConversationId(pagination.capture(), eq(10L));
        assertEquals(20, pagination.getValue().getPageSize());
        assertEquals(Sort.Direction.DESC, pagination.getValue().getSort().getOrderFor("createdAt").getDirection());
        verify(results).findByUserId(7L);
    }

    @Test
    void missingTestNeverCallsTheModelOrLoadsHistory() {
        var model = mock(ChatModel.class);
        var messages = mock(MessageRepository.class);
        var results = mock(VocationalTestResultRepository.class);
        when(results.findByUserId(7L)).thenReturn(Optional.empty());
        var service = new AiService(model, messages, results);
        assertThrows(VocationalTestRequiredException.class,
                () -> service.generarRespuesta(mock(Conversation.class), "Genera un roadmap", 7L));
        verifyNoInteractions(model, messages);
    }
}
