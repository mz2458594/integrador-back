package com.integrador.rocket.roadmap.models.users.dto;

import com.integrador.rocket.roadmap.models.users.Role;
import jakarta.validation.constraints.*;

public record UserRegister(
        @NotBlank(message = "El nombre es requerido") String name,
        @NotBlank(message = "El email es requerido") @Email(message = "Por favor ingrese un email válido") String email,
        //NOT BLANK NO FUNCIONA CON ENUMS - VALIDAR SI CAMBIA ESTE PARÁMETRO
        Role role,
        @NotBlank(message = "La contraseña es requerida") @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!\\-_]).{8,}$",
                message = "La contraseña debe tener al menos 8 caractéres, una mayúscula, una minúscula, un número y un caractér especial"
        ) String password
) {
}
