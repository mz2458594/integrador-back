package com.integrador.rocket.roadmap.models.vocacionalQuestions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VocationalQuestionUpdate(
        String text
//        Integer order
) {
}
