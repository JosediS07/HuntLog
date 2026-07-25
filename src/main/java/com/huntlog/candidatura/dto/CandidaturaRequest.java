package com.huntlog.candidatura.dto;

public record CandidaturaRequest(
        Long empresaId,
        String puesto,
        String urlOferta,
        java.math.BigDecimal salarioMin,
        java.math.BigDecimal salarioMax,
        String moneda,
        String ubicacion,
        String notas
) {}
