package com.integrador.rocket.roadmap.models.users.dto;

import com.integrador.rocket.roadmap.models.users.User;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UserList (
        Long id,
        String name,
        String email,
        //VERIFICAR SI ROLE SERA ENUM O NO
        String role,
        LocalDateTime createadAt,
        LocalDateTime updateAt
) {
    public UserList(User user){
        this (
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
