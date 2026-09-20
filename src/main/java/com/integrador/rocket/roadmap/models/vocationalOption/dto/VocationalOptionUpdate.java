package com.integrador.rocket.roadmap.models.vocationalOption.dto;



import java.util.Map;

public record VocationalOptionUpdate(
        String text,
        Map<String, Integer> careerScore

) {
}
