package com.integrador.rocket.roadmap.models.vocationalOption.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record VocationalOptionRegisterForQuestion(

        @NotBlank String text,

        @NotEmpty Map<String, Integer> careerScore
) {
}
