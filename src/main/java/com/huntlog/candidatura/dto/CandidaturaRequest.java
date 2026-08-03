package com.huntlog.candidatura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CandidaturaRequest(
        @NotNull Long empresaId,
        @NotBlank String puesto,
        String urlOferta,
        @Positive BigDecimal salarioMin,
        @Positive BigDecimal salarioMax,
        String moneda,
        String ubicacion,
        String notas
) {}
