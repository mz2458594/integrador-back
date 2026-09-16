package com.integrador.rocket.roadmap.models.users.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdate(
        String name,
        String email,
        // VALIDAR SI ROLE SERA UN ENUM
        String role,
        String password
) {
}
