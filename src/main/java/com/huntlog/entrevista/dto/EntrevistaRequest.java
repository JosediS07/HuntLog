package com.huntlog.entrevista.dto;

public record EntrevistaRequest(
        String tipo,
        java.time.LocalDateTime fechaHora,
        Integer duracionMin,
        String entrevistador,
        String feedback,
        String notas
) {}
