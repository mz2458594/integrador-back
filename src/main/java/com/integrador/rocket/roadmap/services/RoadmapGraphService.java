package com.integrador.rocket.roadmap.services;

import com.integrador.rocket.roadmap.models.roadmaps.*;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapConnections;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapConnections.Link;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapRegister;
import com.integrador.rocket.roadmap.models.roadmapsteps.RoadmapStep;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service @Transactional
public class RoadmapGraphService {
    private final RoadmapRepository roadmaps;
    private final RoadmapStepsRepository steps;
    private final RoadmapGraphRepository graphs;
    private final RoadmapConnectionRepository connections;
    public RoadmapGraphService(RoadmapRepository roadmaps, RoadmapStepsRepository steps,
            RoadmapGraphRepository graphs, RoadmapConnectionRepository connections) {
        this.roadmaps=roadmaps; this.steps=steps; this.graphs=graphs; this.connections=connections;
    }
    private List<RoadmapStep> ordered(Long id) { return steps.findAllByRoadmapIdOrderByOrderIndexAscIdAsc(id); }
    private List<Link> linear(List<RoadmapStep> items) {
        List<Link> links=new ArrayList<>();
        for(int i=1;i<items.size();i++) links.add(new Link(items.get(i-1).getId(), items.get(i).getId()));
        return links;
    }
    @Transactional(readOnly=true)
    public RoadmapConnections read(Long id) {
        if(!roadmaps.existsById(id)) throw new EntityNotFoundException();
        return graphs.findById(id).map(graph -> new RoadmapConnections(graph.getRevision(),
            connections.findAllByRoadmapIdOrderByIdAsc(id).stream().map(c -> new Link(c.getSource().getId(),c.getTarget().getId())).toList()))
            .orElseGet(() -> new RoadmapConnections(0L,linear(ordered(id))));
    }
    private Roadmap lock(Long id) { return roadmaps.findByIdForUpdate(id).orElseThrow(EntityNotFoundException::new); }
    private void owner(Roadmap roadmap,User user) {
        if(user==null || !roadmap.getUser().getId().equals(user.getId())) throw new AccessDeniedException("Solo el propietario puede editar las conexiones");
    }
    public static void validate(Set<Long> ids,List<Link> links) {
        Map<Long,List<Long>> next=new HashMap<>(); Map<Long,Integer> degree=new HashMap<>();
        ids.forEach(id -> { next.put(id,new ArrayList<>()); degree.put(id,0); });
        Set<Link> unique=new HashSet<>();
        for(Link link:links) {
            if(!ids.contains(link.sourceId()) || !ids.contains(link.targetId())) throw new ValidationException("Todos los pasos deben pertenecer al mismo roadmap");
            if(link.sourceId().equals(link.targetId()) || !unique.add(link)) throw new ValidationException("No se permiten conexiones repetidas ni al mismo paso");
            next.get(link.sourceId()).add(link.targetId()); degree.merge(link.targetId(),1,Integer::sum);
        }
        Deque<Long> queue=new ArrayDeque<>(); degree.forEach((id,count)-> {if(count==0)queue.add(id);});
        int visited=0;
        while(!queue.isEmpty()) { Long id=queue.remove();visited++; for(Long target:next.get(id))if(degree.merge(target,-1,Integer::sum)==0)queue.add(target); }
        if(visited!=ids.size())throw new ValidationException("La conexion crearia un ciclo. El roadmap debe avanzar hacia otros pasos");
    }
    private RoadmapConnections persist(Roadmap roadmap,List<RoadmapStep> items,RoadmapGraph graph,List<Link> links) {
        Map<Long,RoadmapStep> byId=items.stream().collect(Collectors.toMap(RoadmapStep::getId,s->s));
        validate(byId.keySet(),links);
        connections.removeByRoadmapId(roadmap.getId()); connections.flush();
        connections.saveAll(links.stream().map(link -> new RoadmapConnection(roadmap,byId.get(link.sourceId()),byId.get(link.targetId()))).toList());
        graph.advance(); graphs.save(graph);
        return new RoadmapConnections(graph.getRevision(),List.copyOf(links));
    }
    public RoadmapConnections replace(Long id,User user,RoadmapConnections request) {
        Roadmap roadmap=lock(id); owner(roadmap,user);
        RoadmapGraph graph=graphs.findById(id).orElseGet(()->new RoadmapGraph(roadmap));
        if(request.version()!=graph.getRevision())throw new ResponseStatusException(HttpStatus.CONFLICT,"El mapa cambio en otra sesion. Actualizalo antes de editar las conexiones");
        return persist(roadmap,ordered(id),graph,request.links());
    }
    public void initial(Roadmap roadmap,List<RoadmapRegister.InitialConnection> links) {
        if(links==null)return;
        List<RoadmapStep> items=roadmap.getRoadmapSteps();
        List<Link> resolved=links.stream().map(link->{
            if(link.source()<0 || link.target()<0 || link.source()>=items.size() || link.target()>=items.size())throw new ValidationException("Indice de conexion invalido");
            return new Link(items.get(link.source()).getId(),items.get(link.target()).getId());
        }).toList();
        persist(roadmap,items,new RoadmapGraph(roadmap),resolved);
    }
    public void removeStep(RoadmapStep step) {
        Roadmap roadmap=lock(step.getRoadmap().getId());
        RoadmapGraph graph=graphs.findById(roadmap.getId()).orElse(null);
        if(graph==null) { graph=new RoadmapGraph(roadmap); persist(roadmap,ordered(roadmap.getId()),graph,linear(ordered(roadmap.getId()))); }
        connections.removeByStepId(step.getId()); graph.advance();graphs.save(graph);
    }
    public void removeRoadmap(Long id) {
        lock(id); connections.removeByRoadmapId(id);
        graphs.findById(id).ifPresent(graphs::delete);
    }
}
