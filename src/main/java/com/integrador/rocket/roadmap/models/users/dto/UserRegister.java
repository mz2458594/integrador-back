package com.integrador.rocket.roadmap.models.users.dto;

import com.integrador.rocket.roadmap.models.users.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegister(
        @NotBlank String name,
        @NotBlank String email,
        //NOT BLANK NO FUNCIONA CON ENUMS - VALIDAR SI CAMBIA ESTE PARÁMETRO
        Role role,
        @NotBlank String password
) {
}
