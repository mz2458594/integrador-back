package com.integrador.rocket.roadmap.models.vocationaltestresults.dto;

import com.integrador.rocket.roadmap.models.users.User;
import com.integrador.rocket.roadmap.models.vocationalOption.VocationalOption;
import com.integrador.rocket.roadmap.models.vocationalOption.dto.VocationalOptionDetail;
import com.integrador.rocket.roadmap.models.vocationaltestresults.VocationalTestResult;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

public record VocationalTestResultDetail(

        Long id,

        Long userId,

        String suggestedCareer,

        LocalDateTime completedAt,

        List<VocationalOptionDetail> vocacionalOptions

) {
    public VocationalTestResultDetail(VocationalTestResult vocationalTestResult) {
        this(
                vocationalTestResult.getId(),
                vocationalTestResult.getUser().getId(),
                vocationalTestResult.getSuggestedCareer(),
                vocationalTestResult.getCompletedAt(),
                vocationalTestResult.getVocationalOptions().stream().map(VocationalOptionDetail::new).toList()
        );
    }
}
