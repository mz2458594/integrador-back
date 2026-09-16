package com.integrador.rocket.roadmap.models.users.dto;

import com.integrador.rocket.roadmap.models.users.User;

import java.time.LocalDateTime;

public record UserDetail(
        String name,
        String email,
        //VERIFICAR SI ROLE SERA ENUM O NO
        String role,
        LocalDateTime createadAt,
        LocalDateTime updateAt
) {
    public UserDetail(User user) {
        this(
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
