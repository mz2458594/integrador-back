package com.integrador.rocket.roadmap.models.vocacionalQuestions.dto;

import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionRegisterForQuestion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VocationalQuestionRegister(
        @NotBlank String text,
//        @NotNull int order,
        @Valid List<VocationalOptionRegisterForQuestion> vocationalOptionRegisters
        ) {
}
