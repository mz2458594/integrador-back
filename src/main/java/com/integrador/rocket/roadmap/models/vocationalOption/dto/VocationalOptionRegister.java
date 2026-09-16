package com.integrador.rocket.roadmap.models.vocationalOption.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record VocationalOptionRegister(

        @NotNull Long questionId,

        @NotBlank String text,

        @NotEmpty Map<String, Integer> careerScore
) {
}
