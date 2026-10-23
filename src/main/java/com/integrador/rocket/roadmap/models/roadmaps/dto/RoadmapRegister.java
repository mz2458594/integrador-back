package com.integrador.rocket.roadmap.models.roadmaps.dto;

import com.integrador.rocket.roadmap.models.roadmaps.CreationSource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoadmapRegister(
        @NotBlank String title,

        @NotBlank String description,

        @NotBlank String category,

        @NotNull boolean isPublic,

        @NotNull CreationSource createdVia,

        @Size(max = 50) List<@NotNull @Valid InitialStep> steps
) {
    public RoadmapRegister(String title, String description, String category, boolean isPublic, CreationSource createdVia) {
        this(title, description, category, isPublic, createdVia, null);
    }

    public record InitialStep(@NotBlank @Size(max = 255) String title,
                              @NotBlank String description, Double x, Double y) {}
}
