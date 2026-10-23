package com.integrador.rocket.roadmap.controller;
import com.integrador.rocket.roadmap.services.RoadmapGraphService;
import com.integrador.rocket.roadmap.models.roadmaps.dto.RoadmapConnections;
import com.integrador.rocket.roadmap.models.users.User;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/roadmap/{id}/connections")
public class RoadmapConnectionsController {
    private final RoadmapGraphService service;
    public RoadmapConnectionsController(RoadmapGraphService service) { this.service=service; }
    @GetMapping public RoadmapConnections read(@PathVariable Long id) { return service.read(id); }
    @PutMapping public RoadmapConnections replace(@PathVariable Long id,@AuthenticationPrincipal User user,@RequestBody @Valid RoadmapConnections request) {
        return service.replace(id,user,request);
    }
}
