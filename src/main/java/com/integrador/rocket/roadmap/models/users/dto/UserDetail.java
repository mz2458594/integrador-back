package com.integrador.rocket.roadmap.models.users.dto;

import com.integrador.rocket.roadmap.models.users.Role;
import com.integrador.rocket.roadmap.models.users.User;

import java.time.LocalDateTime;

public record UserDetail(
        Long id,
        String name,
        String email,
        //VERIFICAR SI ROLE SERA ENUM O NO
        Role role,
        LocalDateTime createadAt,
        LocalDateTime updateAt,
        String university,
        String bio
) {
    public UserDetail(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUniversity(),
                user.getBiography()
        );
    }
}
