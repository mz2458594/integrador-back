package com.integrador.rocket.roadmap.models.vocationaltestresults.dto;

import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionDetail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record VocationalTestResultRegister(

        // EL USER_ID LO AGARRAMOS DEL TOKEN
//        @NotNull Long userId,

        @NotBlank String suggestedCareer,

        @NotEmpty List<Long> vocacionalOptionIds
) {
}
