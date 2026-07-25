package com.huntlog.entrevista.dto;

public record EntrevistaResponse(
        Long id,
        Long candidaturaId,
        String tipo,
        java.time.LocalDateTime fechaHora,
        Integer duracionMin,
        String entrevistador,
        String feedback,
        String notas,
        java.time.LocalDateTime creado
) {}
