package com.integrador.rocket.roadmap.models.vocacionalQuestions.dto;

import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionDetail;
import jakarta.persistence.*;

import java.util.List;

public record VocationalQuestionDetail(
        Long id,
        String text,
        int order,
        List<VocationalOptionDetail> vocacionalOptions
) {
    public VocationalQuestionDetail(VocationalQuestion vocationalQuestion){
        this(
                vocationalQuestion.getId(),
                vocationalQuestion.getText(),
                vocationalQuestion.getOrderIndex(),
                vocationalQuestion.getVocacionalOptions().stream().map(VocationalOptionDetail::new).toList()
        );
    }
}
