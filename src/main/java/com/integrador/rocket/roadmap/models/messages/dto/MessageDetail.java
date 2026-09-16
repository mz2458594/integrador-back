package com.integrador.rocket.roadmap.models.messages.dto;

import com.integrador.rocket.roadmap.models.conversations.Conversation;
import com.integrador.rocket.roadmap.models.messages.Message;
import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public record MessageDetail(
        Long id,
        String content,
        boolean isFromAgent,
        LocalDateTime createdAt,
        Long conversation_id,
        UserDetail user
) {
    public MessageDetail(Message message) {
        this(
                message.getId(),
                message.getContent(),
                message.isFromAgent(),
                message.getCreatedAt(),
                message.getConversation().getId(),
                message.getUser() != null ? new UserDetail(message.getUser()) : null

        );
    }
}
