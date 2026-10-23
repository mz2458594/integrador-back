package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.infra.exceptions.GestorDeErrores;
import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.roadmaps.CreationSource;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapRegister;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepRegister;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.RoadmapRepository;
import com.integrador.rocket.roadmap.repositories.RoadmapStepsRepository;
import com.integrador.rocket.roadmap.services.VocationalAccessService;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoadmapManagementControllerTest {
    MockMvc mvc;
    RoadmapRepository roadmaps;
    RoadmapStepsRepository steps;
    User owner;
    Roadmap roadmap;
    RoadmapStep step;

    @BeforeEach
    void setup() {
        owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        authenticate(owner);
        roadmap = new Roadmap(new RoadmapRegister("Backend", "Plan", "Software", false, CreationSource.MANUAL), owner);
        ReflectionTestUtils.setField(roadmap, "id", 7L);
        step = new RoadmapStep(new RoadmapStepRegister("Java", "Learn Java", 0, 7L, 0.0, 80.0), roadmap);
        ReflectionTestUtils.setField(step, "id", 12L);
        roadmaps = mock(RoadmapRepository.class);
        steps = mock(RoadmapStepsRepository.class);
        when(roadmaps.getReferenceById(7L)).thenReturn(roadmap);
        when(steps.getReferenceById(12L)).thenReturn(step);
        when(roadmaps.save(any(Roadmap.class))).thenAnswer(invocation -> {
            Roadmap saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 9L);
            long stepId = 100;
            for (var item : saved.getRoadmapSteps()) ReflectionTestUtils.setField(item, "id", stepId++);
            return saved;
        });
        var controller = new RoadmapController();
        ReflectionTestUtils.setField(controller, "roadmapRepository", roadmaps);
        ReflectionTestUtils.setField(controller, "vocationalAccessService", mock(VocationalAccessService.class));
        ReflectionTestUtils.setField(controller,"graphService",mock(com.integrador.rocket.roadmap.services.RoadmapGraphService.class));
        var stepController = new RoadmapStepsController();
        ReflectionTestUtils.setField(stepController,"graphService",mock(com.integrador.rocket.roadmap.services.RoadmapGraphService.class));
        ReflectionTestUtils.setField(stepController, "roadmapRepository", roadmaps);
        ReflectionTestUtils.setField(stepController, "roadmapStepsRepository", steps);
        mvc = MockMvcBuilders.standaloneSetup(controller, stepController).setControllerAdvice(new GestorDeErrores())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
    }

    void authenticate(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
    }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    @Test void generatedStepsAreSavedAsPartOfTheRoadmapAggregate() throws Exception {
        mvc.perform(post("/roadmap").contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Backend","description":"Generated plan","category":"Software","isPublic":false,"createdVia":"AI_AGENT",
                 "steps":[{"title":"Java","description":"Practice Java","x":360,"y":80},
                          {"title":"REST","description":"Build API","orderIndex":99,"x":360,"y":260}]}
                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.roadmapSteps.length()").value(2))
                .andExpect(jsonPath("$.roadmapSteps[1].orderIndex").value(1))
                .andExpect(jsonPath("$.roadmapSteps[1].roadmapId").value(9))
                .andExpect(jsonPath("$.roadmapSteps[1].y").value(260));
        verify(roadmaps, times(1)).save(any(Roadmap.class));
    }

    @Test void invalidGeneratedStepPreventsSavingTheEntireAggregate() throws Exception {
        mvc.perform(post("/roadmap").contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Backend","description":"Plan","category":"Software","isPublic":false,"createdVia":"AI_AGENT",
                 "steps":[{"title":"Java","description":" "}]}
                """))
                .andExpect(status().isBadRequest());
        verify(roadmaps, never()).save(any());
    }

    @Test void ownerCanEditAndDeleteStepsAndDeleteTheRoadmap() throws Exception {
        mvc.perform(put("/roadmap-step/12").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Spring\",\"description\":\"REST APIs\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Spring"))
                .andExpect(jsonPath("$.x").value(0));
        mvc.perform(delete("/roadmap-step/12")).andExpect(status().isNoContent());
        verify(steps).delete(step);
        mvc.perform(delete("/roadmap/7")).andExpect(status().isNoContent());
        verify(roadmaps).delete(roadmap);
    }

    @Test void anotherUserCannotMutateOrDeleteRoadmapOrSteps() throws Exception {
        User other = mock(User.class);
        when(other.getId()).thenReturn(2L);
        authenticate(other);
        mvc.perform(put("/roadmap-step/12").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Changed\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/roadmap-step/12")).andExpect(status().isForbidden());
        mvc.perform(delete("/roadmap/7")).andExpect(status().isForbidden());
        mvc.perform(post("/roadmap-step").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"X\",\"description\":\"X\",\"orderIndex\":1,\"roadmapId\":7}"))
                .andExpect(status().isForbidden());
        assertEquals("Java", step.getTitle());
        verify(steps, never()).delete(any(RoadmapStep.class));
        verify(roadmaps, never()).delete(any(Roadmap.class));
    }
}
