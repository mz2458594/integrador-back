package com.integrador.rocket.roadmap.models.users.dto;

import com.integrador.rocket.roadmap.models.users.Role;

public record UserUpdate(
        String name,
        String email,
        Role role,
        String password
) {
}
