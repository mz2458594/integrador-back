package com.integrador.rocket.roadmap.models.posts.dto;

import com.integrador.rocket.roadmap.models.comments.dto.CommentDetail;
import com.integrador.rocket.roadmap.models.tags.dto.TagDetail;
import com.integrador.rocket.roadmap.models.users.dto.UserDetail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;

public record PostRegister(
        @NotBlank String title,
        @NotBlank String content,
        List<Long> tagIds
) {
}
