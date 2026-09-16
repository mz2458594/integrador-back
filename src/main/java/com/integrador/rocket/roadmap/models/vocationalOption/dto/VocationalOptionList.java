package com.integrador.rocket.roadmap.models.vocationalOption.dto;

import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;

import java.util.Map;

public record VocationalOptionList(
        Long id,

        Long questionId,

        String text,

        Map<String, Integer> careerScore
) {
    public VocationalOptionList(VocationalOption vocationalOption) {
        this(
                vocationalOption.getId(),
                vocationalOption.getQuestion().getId(),
                vocationalOption.getText(),
                vocationalOption.getCareerScore()
        );
    }

}
