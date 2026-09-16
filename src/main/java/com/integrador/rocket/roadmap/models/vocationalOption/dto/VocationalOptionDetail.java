package com.integrador.rocket.roadmap.models.vocationalOption.dto;

import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record VocationalOptionDetail(
        Long id,

        Long questionId,

        String text,

        Map<String, Integer> careerScore

) {
    public VocationalOptionDetail(VocationalOption vocationalOption){
        this(
                vocationalOption.getId(),
                vocationalOption.getQuestion().getId(),
                vocationalOption.getText(),
                vocationalOption.getCareerScore()
        );
    }
}
