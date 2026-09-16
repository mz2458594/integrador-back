package com.integrador.rocket.roadmap.models.users.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRegister(
        @NotBlank String name,
        @NotBlank String email,
        //NOT BLANK NO FUNCIONA CON ENUMS - VALIDAR SI CAMBIA ESTE PARÁMETRO
        @NotBlank String role,
        @NotBlank String password
) {
}
