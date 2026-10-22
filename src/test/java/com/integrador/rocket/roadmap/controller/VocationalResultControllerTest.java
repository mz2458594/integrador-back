package com.integrador.rocket.roadmap.controller;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import com.integrador.rocket.roadmap.models.vocationaltestresults.dto.VocationalTestResultRegister;
import com.integrador.rocket.roadmap.repositories.*;
import com.integrador.rocket.roadmap.services.VocationalScoringService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VocationalResultControllerTest {
    private final VocationalQuestionRepository questions = mock(VocationalQuestionRepository.class);
    private final VocationalTestResultRepository results = mock(VocationalTestResultRepository.class);
    private final VocationalScoringService scoring = mock(VocationalScoringService.class);
    private final VocationalTestResultController controller = new VocationalTestResultController(questions, results, scoring);
    private User user(long id) { var user = mock(User.class); when(user.getId()).thenReturn(id); return user; }
    @Test void ownResultIsAbsentUntilPersisted() {
        when(results.findByUserId(7L)).thenReturn(Optional.empty());
        assertEquals(204, controller.current(user(7)).getStatusCode().value());
    }
    @Test void cannotReadAnotherUsersResult() {
        var result = new VocationalTestResult();
        ReflectionTestUtils.setField(result, "user", user(99));
        when(results.findById(1L)).thenReturn(Optional.of(result));
        var error = assertThrows(ResponseStatusException.class, () -> controller.detail(user(7), 1L));
        assertEquals(403, error.getStatusCode().value());
    }
    @Test void secondSubmissionIsRejectedBeforeScoring() {
        when(results.findByUserId(7L)).thenReturn(Optional.of(new VocationalTestResult()));
        var error = assertThrows(ResponseStatusException.class, () -> controller.create(user(7), new VocationalTestResultRegister(List.of(1L)), UriComponentsBuilder.newInstance()));
        assertEquals(409, error.getStatusCode().value());
        verifyNoInteractions(scoring);
    }
}
