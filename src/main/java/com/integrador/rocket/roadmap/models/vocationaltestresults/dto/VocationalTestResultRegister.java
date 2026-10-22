package com.integrador.rocket.roadmap.models.vocationaltestresults.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
public record VocationalTestResultRegister(
    @NotEmpty @Size(min = 20, max = 40) List<@NotNull Long> vocacionalOptionIds
) {}
