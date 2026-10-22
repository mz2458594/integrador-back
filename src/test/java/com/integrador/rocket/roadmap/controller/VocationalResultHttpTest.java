package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.infra.exceptions.GestorDeErrores;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import com.integrador.rocket.roadmap.repositories.*;
import com.integrador.rocket.roadmap.services.VocationalScoringService;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VocationalResultHttpTest {
    private MockMvc mvc;
    private VocationalTestResultRepository results;
    private User user;
    private String payload;
    @BeforeEach void setup() {
        user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        var questions = mock(VocationalQuestionRepository.class);
        results = mock(VocationalTestResultRepository.class);
        List<VocationalQuestion> bank = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            var question = new VocationalQuestion((long) i + 1, "Question " + i, new ArrayList<>());
            question.getVocationalOptions().add(new VocationalOption((long) i * 2 + 1, question, "Nada", Map.of("Software", 0), new ArrayList<>()));
            question.getVocationalOptions().add(new VocationalOption((long) i * 2 + 2, question, "Mucho", Map.of("Software", 4), new ArrayList<>()));
            bank.add(question);
            ids.add(String.valueOf(i * 2 + 2));
        }
        payload = "{\"vocacionalOptionIds\":[" + String.join(",", ids) + "]}";
        when(questions.findAll(any(Sort.class))).thenReturn(bank);
        when(results.findByUserId(7L)).thenReturn(Optional.empty());
        when(results.saveAndFlush(any())).thenAnswer(invocation -> {
            VocationalTestResult result = invocation.getArgument(0);
            ReflectionTestUtils.setField(result, "id", 10L);
            return result;
        });
        mvc = MockMvcBuilders.standaloneSetup(new VocationalTestResultController(questions, results, new VocationalScoringService()))
            .setControllerAdvice(new GestorDeErrores())
            .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
    }
    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }
    @Test void postCalculatesPersistsAndReturnsServerRanking() throws Exception {
        mvc.perform(post("/vocational-test-result").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated()).andExpect(header().string("Location", "http://localhost/vocational-test-result/10"))
            .andExpect(jsonPath("$.userId").value(7)).andExpect(jsonPath("$.suggestedCareer").value("Software"))
            .andExpect(jsonPath("$.ranking[0].score").value(80)).andExpect(jsonPath("$.ranking[0].maxScore").value(80))
            .andExpect(jsonPath("$.ranking[0].percentage").value(100.0)).andExpect(jsonPath("$.vocacionalOptions.length()").value(20));
    }
    @Test void missingAnswersAreBadRequest() throws Exception {
        mvc.perform(post("/vocational-test-result").contentType(MediaType.APPLICATION_JSON).content("{\"vocacionalOptionIds\":[2]}"))
            .andExpect(status().isBadRequest());
        verify(results, never()).saveAndFlush(any());
    }
    @Test void foreignOptionsAreBadRequest() throws Exception {
        mvc.perform(post("/vocational-test-result").contentType(MediaType.APPLICATION_JSON).content(payload.replace("[2,", "[9999,")))
            .andExpect(status().isBadRequest());
        verify(results, never()).saveAndFlush(any());
    }
    @Test void meUses204UntilCompletedAndReturnsSnapshotAfterwards() throws Exception {
        mvc.perform(get("/vocational-test-result/me")).andExpect(status().isNoContent());
        var evaluation = new VocationalScoringService.Evaluation("Software", List.of(new com.integrador.rocket.roadmap.models.vocationaltestresults.dto.CareerScore("Software", 14, 20, 70)), List.of());
        var result = new VocationalTestResult(user, evaluation);
        ReflectionTestUtils.setField(result, "id", 10L);
        when(results.findByUserId(7L)).thenReturn(Optional.of(result));
        mvc.perform(get("/vocational-test-result/me")).andExpect(status().isOk())
            .andExpect(jsonPath("$.ranking[0].score").value(14)).andExpect(jsonPath("$.ranking[0].percentage").value(70));
    }
    @Test void alreadyCompletedTestReturnsConflict() throws Exception {
        when(results.findByUserId(7L)).thenReturn(Optional.of(new VocationalTestResult()));
        mvc.perform(post("/vocational-test-result").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isConflict());
    }
    @Test void anotherUsersDetailIsForbiddenAndResultsCannotBeOverwritten() throws Exception {
        var other = mock(User.class); when(other.getId()).thenReturn(99L);
        var result = new VocationalTestResult(); ReflectionTestUtils.setField(result, "user", other);
        when(results.findById(10L)).thenReturn(Optional.of(result));
        mvc.perform(get("/vocational-test-result/10")).andExpect(status().isForbidden());
        mvc.perform(put("/vocational-test-result/10").contentType(MediaType.APPLICATION_JSON).content("{\"suggestedCareer\":\"Forged\"}"))
            .andExpect(status().isMethodNotAllowed());
    }
}
