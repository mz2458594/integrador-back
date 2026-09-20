package com.integrador.rocket.roadmap.models.vocacionalQuestions.dto;

import com.integrador.rocket.roadmap.models.vocacionalQuestions.VocationalQuestion;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionDetail;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record VocationalQuestionList(
        Long id,
        String text,
        int order,
        List<VocationalOptionDetail> vocacionalOptions
) {
    public VocationalQuestionList(VocationalQuestion vocationalQuestion) {
        this(
                vocationalQuestion.getId(),
                vocationalQuestion.getText(),
                vocationalQuestion.getOrderIndex(),
                vocationalQuestion.getVocationalOptions().stream().map(VocationalOptionDetail::new).toList()
        );
    }
}
