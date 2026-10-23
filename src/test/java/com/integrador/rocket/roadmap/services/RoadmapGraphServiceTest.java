package com.integrador.rocket.roadmap.services;
import com.integrador.rocket.roadmap.models.roadmaps.*;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapConnections;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapConnections.Link;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class RoadmapGraphServiceTest {
    RoadmapRepository roadmaps; RoadmapStepsRepository steps;
    RoadmapGraphRepository graphs; RoadmapConnectionRepository connections;
    RoadmapGraphService service; Roadmap roadmap; User owner; List<RoadmapStep> items;
    @BeforeEach void setup() {
        roadmaps=mock(RoadmapRepository.class); steps=mock(RoadmapStepsRepository.class);
        graphs=mock(RoadmapGraphRepository.class); connections=mock(RoadmapConnectionRepository.class);
        service=new RoadmapGraphService(roadmaps,steps,graphs,connections);
        roadmap=mock(Roadmap.class);owner=mock(User.class);
        when(owner.getId()).thenReturn(1L);when(roadmap.getId()).thenReturn(7L);when(roadmap.getUser()).thenReturn(owner);
        when(roadmaps.existsById(7L)).thenReturn(true);when(roadmaps.findByIdForUpdate(7L)).thenReturn(Optional.of(roadmap));
        items=new ArrayList<>();
        for(long id=1;id<=4;id++) { var step=mock(RoadmapStep.class);when(step.getId()).thenReturn(id);when(step.getRoadmap()).thenReturn(roadmap);items.add(step); }
        when(steps.findAllByRoadmapIdOrderByOrderIndexAscIdAsc(7L)).thenReturn(items);
    }
    @Test void legacyReadPreservesLinearFlowWithoutWriting() {
        assertEquals(List.of(new Link(1L,2L),new Link(2L,3L),new Link(3L,4L)),service.read(7L).links());
        verify(graphs,never()).save(any());verify(connections,never()).saveAll(any());
    }
    @Test void acceptsBranchesAndConvergence() {
        var links=List.of(new Link(1L,2L),new Link(1L,3L),new Link(2L,4L),new Link(3L,4L));
        assertDoesNotThrow(()->RoadmapGraphService.validate(Set.of(1L,2L,3L,4L),links));
        var response=service.replace(7L,owner,new RoadmapConnections(0L,links));
        assertEquals(1L,response.version());assertEquals(links,response.links());
        verify(connections).removeByRoadmapId(7L);verify(connections).saveAll(argThat(list->((List<?>)list).size()==4));
    }
    @Test void rejectsCycleSelfLinkDuplicateAndForeignStepsBeforeReplacingAnything() {
        for(var links:List.of(List.of(new Link(1L,1L)),List.of(new Link(1L,99L)),
            List.of(new Link(1L,2L),new Link(1L,2L)),List.of(new Link(1L,2L),new Link(2L,1L)))) {
            assertThrows(ValidationException.class,()->service.replace(7L,owner,new RoadmapConnections(0L,links)));
        }
        verify(connections,never()).removeByRoadmapId(any());
    }
    @Test void emptyStoredGraphDoesNotFallBackToLinear() {
        var graph=new RoadmapGraph(roadmap);
        when(graphs.findById(7L)).thenReturn(Optional.of(graph));
        when(connections.findAllByRoadmapIdOrderByIdAsc(7L)).thenReturn(List.of());
        assertTrue(service.read(7L).links().isEmpty());
    }
    @Test void revisionConflictCannotOverwriteNewerConnections() {
        var graph=new RoadmapGraph(roadmap);graph.advance();when(graphs.findById(7L)).thenReturn(Optional.of(graph));
        assertThrows(ResponseStatusException.class,()->service.replace(7L,owner,new RoadmapConnections(0L,List.of())));
        verify(connections,never()).removeByRoadmapId(any());
    }
    @Test void otherUserCannotReplaceConnections() {
        User other=mock(User.class);when(other.getId()).thenReturn(9L);
        assertThrows(AccessDeniedException.class,()->service.replace(7L,other,new RoadmapConnections(0L,List.of())));
        verifyNoInteractions(connections);
    }
    @Test void deletingLegacyStepPersistsOldGraphThenRemovesOnlyItsIncidentConnections() {
        service.removeStep(items.get(1));
        verify(connections).removeByStepId(2L);
        verify(connections).saveAll(argThat(list->((List<?>)list).size()==3));
    }
}
