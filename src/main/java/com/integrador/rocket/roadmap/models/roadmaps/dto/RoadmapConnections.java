package com.integrador.rocket.roadmap.models.roadmaps.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record RoadmapConnections(@NotNull @PositiveOrZero Long version,
        @NotNull @Size(max=2000) List<@NotNull @Valid Link> links) {
    public record Link(@NotNull Long sourceId, @NotNull Long targetId) {}
}
