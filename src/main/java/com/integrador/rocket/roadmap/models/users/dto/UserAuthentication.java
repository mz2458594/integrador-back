package com.integrador.rocket.roadmap.models.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserAuthentication(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
