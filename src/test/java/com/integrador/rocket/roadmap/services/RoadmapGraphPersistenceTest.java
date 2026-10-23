package com.integrador.rocket.roadmap.services;

import com.integrador.rocket.roadmap.models.roadmaps.*;
import com.integrador.rocket.roadmap.models.roadmaps.dto.*;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapConnections.Link;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(showSql=false,properties={"spring.test.database.replace=NONE",
        "spring.datasource.username=sa", "spring.datasource.password=", "logging.level.root=WARN", "debug=false","spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.datasource.url=jdbc:h2:mem:roadmap_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.jpa.show-sql=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import(RoadmapGraphService.class)
class RoadmapGraphPersistenceTest {
    @Autowired EntityManager em;
    @Autowired RoadmapRepository roadmaps;
    @Autowired RoadmapStepsRepository steps;
    @Autowired RoadmapGraphService service;

    private User user() {
        User user=new User();
        ReflectionTestUtils.setField(user,"name","Test Owner");
        ReflectionTestUtils.setField(user,"email","owner@example.test");
        ReflectionTestUtils.setField(user,"password","test-only");
        ReflectionTestUtils.setField(user,"createdAt",LocalDateTime.now());
        em.persist(user);return user;
    }
    @Test void persistsLongGeneratedStepsAndBranchesAndCanRemoveAnEdgeWithoutItReturning() {
        User owner=user();
        var initial=List.of(new RoadmapRegister.InitialStep("Fundamentos","Contenido largo ".repeat(100),360.0,80.0),
            new RoadmapRegister.InitialStep("Backend","API",200.0,260.0),
            new RoadmapRegister.InitialStep("Frontend","Interfaz",500.0,260.0));
        var root=roadmaps.saveAndFlush(new Roadmap(new RoadmapRegister("Plan","Ruta de estudio","Software",false,CreationSource.AI_AGENT,initial),owner));
        var branch=List.of(new RoadmapRegister.InitialConnection(0,1),new RoadmapRegister.InitialConnection(0,2));
        service.initial(root,branch);em.flush();em.clear();
        var graph=service.read(root.getId());assertEquals(2,graph.links().size());
        assertTrue(steps.findAllByRoadmapIdOrderByOrderIndexAscIdAsc(root.getId()).get(0).getDescription().length()>255);
        service.replace(root.getId(),owner,new RoadmapConnections(graph.version(),List.of()));em.flush();em.clear();
        assertTrue(service.read(root.getId()).links().isEmpty());
    }
    @Test void deletingStepAndRoadmapCleansForeignKeyReferences() {
        User owner=user();
        var initial=List.of(new RoadmapRegister.InitialStep("A","A",0.0,0.0),new RoadmapRegister.InitialStep("B","B",0.0,200.0));
        var root=roadmaps.saveAndFlush(new Roadmap(new RoadmapRegister("Plan","Ruta","Software",false,CreationSource.MANUAL,initial),owner));
        var step=root.getRoadmapSteps().get(0);
        service.removeStep(step);
        // Clear the aggregate collection before deleting a child loaded through it.
        root.getRoadmapSteps().remove(step);steps.delete(step);em.flush();em.clear();
        assertTrue(service.read(root.getId()).links().isEmpty());
        service.removeRoadmap(root.getId());roadmaps.deleteById(root.getId());em.flush();
        assertFalse(roadmaps.existsById(root.getId()));
    }
}
