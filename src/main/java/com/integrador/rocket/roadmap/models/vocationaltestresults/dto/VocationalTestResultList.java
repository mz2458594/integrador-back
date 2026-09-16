package com.integrador.rocket.roadmap.models.vocationaltestresults.dto;

import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionDetail;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;

import java.time.LocalDateTime;
import java.util.List;

public record VocationalTestResultList(
        Long id,

        Long userId,

        String suggestedCareer,

        LocalDateTime completedAt,

        List<VocationalOptionDetail> vocacionalOptions
) {
    public VocationalTestResultList(VocationalTestResult vocationalTestResult) {
        this(
                vocationalTestResult.getId(),
                vocationalTestResult.getUser().getId(),
                vocationalTestResult.getSuggestedCareer(),
                vocationalTestResult.getCompletedAt(),
                vocationalTestResult.getVocationalOptions().stream().map(VocationalOptionDetail::new).toList()
        );
    }
}
