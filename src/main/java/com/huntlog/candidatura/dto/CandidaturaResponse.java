package com.huntlog.candidatura.dto;

public record CandidaturaResponse(
        Long id,
        Long empresaId,
        String empresaNombre,
        String puesto,
        String estado,
        String urlOferta,
        java.math.BigDecimal salarioMin,
        java.math.BigDecimal salarioMax,
        String moneda,
        String ubicacion,
        String notas,
        java.time.LocalDateTime aplicadoEn,
        java.time.LocalDateTime respondidoEn,
        java.time.LocalDateTime creado,
        java.time.LocalDateTime actualizado
) {}
