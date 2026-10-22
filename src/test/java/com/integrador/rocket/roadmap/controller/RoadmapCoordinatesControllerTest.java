package com.integrador.rocket.roadmap.controller;

import com.integrador.rocket.roadmap.infra.exceptions.GestorDeErrores;
import com.integrador.rocket.roadmap.models.roadmaps.Roadmap;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepRegister;
import com.integrador.rocket.roadmap.models.roadmapsteps.dto.RoadmapStepUpdate;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.RoadmapRepository;
import com.integrador.rocket.roadmap.repositories.RoadmapStepsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoadmapCoordinatesControllerTest {
    private MockMvc mvc;
    private RoadmapRepository roadmaps;
    private RoadmapStepsRepository steps;
    private Roadmap roadmap;
    private RoadmapStep step;

    @BeforeEach
    void setup() {
        roadmaps = mock(RoadmapRepository.class);
        steps = mock(RoadmapStepsRepository.class);
        roadmap = mock(Roadmap.class);
        when(roadmap.getId()).thenReturn(7L);
        when(roadmaps.getReferenceById(7L)).thenReturn(roadmap);
        step = new RoadmapStep(new RoadmapStepRegister("React", "Hooks", 1, 7L, 450.5, -25.25), roadmap);
        ReflectionTestUtils.setField(step, "id", 12L);
        when(steps.getReferenceById(12L)).thenReturn(step);
        when(steps.save(any(RoadmapStep.class))).thenAnswer(invocation -> {
            RoadmapStep saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 12L);
            return saved;
        });
        var controller = new RoadmapStepsController();
        ReflectionTestUtils.setField(controller, "roadmapRepository", roadmaps);
        ReflectionTestUtils.setField(controller, "roadmapStepsRepository", steps);
        var roadmapController = new RoadmapController();
        ReflectionTestUtils.setField(roadmapController, "roadmapRepository", roadmaps);
        mvc = MockMvcBuilders.standaloneSetup(controller, roadmapController)
                .setControllerAdvice(new GestorDeErrores())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    void createAcceptsFractionalAndNegativeCoordinates() throws Exception {
        mvc.perform(post("/roadmap-step").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"React","description":"Hooks","orderIndex":1,"roadmapId":7,"x":450.5,"y":-25.25}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/roadmap-step/12"))
                .andExpect(jsonPath("$.x").value(450.5))
                .andExpect(jsonPath("$.y").value(-25.25))
                .andExpect(jsonPath("$.roadmapId").value(7));
    }

    @Test
    void createStillAcceptsLegacyPayload() throws Exception {
        mvc.perform(post("/roadmap-step").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"React","description":"Hooks","orderIndex":1,"roadmapId":7}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("React"))
                .andExpect(jsonPath("$.x").isEmpty())
                .andExpect(jsonPath("$.y").isEmpty());
    }

    @Test
    void updateCoordinatesWithoutChangingExistingFields() throws Exception {
        mvc.perform(put("/roadmap-step/12").contentType(MediaType.APPLICATION_JSON)
                .content("{\"x\":0,\"y\":-120.75}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(0.0))
                .andExpect(jsonPath("$.y").value(-120.75))
                .andExpect(jsonPath("$.title").value("React"))
                .andExpect(jsonPath("$.orderIndex").value(1));
        assertEquals(0.0, step.getX());
    }

    @Test
    void legacyUpdatePreservesCoordinates() throws Exception {
        mvc.perform(put("/roadmap-step/12").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"React actualizado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(450.5))
                .andExpect(jsonPath("$.y").value(-25.25));
    }

    @Test
    void updatingOneAxisPreservesTheOther() throws Exception {
        mvc.perform(put("/roadmap-step/12").contentType(MediaType.APPLICATION_JSON)
                .content("{\"x\":100.25,\"y\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.x").value(100.25))
                .andExpect(jsonPath("$.y").value(-25.25));
    }

    @Test
    void detailAndBothListsExposeCoordinates() throws Exception {
        when(steps.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(step), PageRequest.of(0, 10), 1));
        when(steps.findAllByRoadmapId(any(Pageable.class), eq(7L)))
                .thenReturn(new PageImpl<>(List.of(step), PageRequest.of(0, 10), 1));
        mvc.perform(get("/roadmap-step/12"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.x").value(450.5));
        for (String url : List.of("/roadmap-step", "/roadmap-step/roadmap/7")) {
            mvc.perform(get(url)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].x").value(450.5))
                    .andExpect(jsonPath("$.content[0].y").value(-25.25));
        }
    }

    @Test
    void roadmapDetailIncludesStepCoordinates() throws Exception {
        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);
        when(roadmap.getUser()).thenReturn(user);
        when(roadmap.getRoadmapSteps()).thenReturn(List.of(step));
        when(roadmap.getUserRoadmaps()).thenReturn(List.of());
        mvc.perform(get("/roadmap/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roadmapSteps[0].x").value(450.5))
                .andExpect(jsonPath("$.roadmapSteps[0].y").value(-25.25));
    }

    @Test
    void invalidCoordinateReturnsBadRequest() throws Exception {
        mvc.perform(put("/roadmap-step/12").contentType(MediaType.APPLICATION_JSON)
                .content("{\"x\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
        assertEquals(450.5, step.getX());
    }

    @Test
    void nonFiniteCoordinatesCannotMutateAnEntity() {
        assertThrows(jakarta.validation.ValidationException.class,
                () -> step.actualizar(new RoadmapStepUpdate("Changed", null, null, Double.NaN, 1.0)));
        assertEquals("React", step.getTitle());
        assertEquals(450.5, step.getX());
        assertThrows(jakarta.validation.ValidationException.class,
                () -> new RoadmapStep(new RoadmapStepRegister("React", "Hooks", 1, 7L,
                        0.0, Double.POSITIVE_INFINITY), roadmap));
    }
}
