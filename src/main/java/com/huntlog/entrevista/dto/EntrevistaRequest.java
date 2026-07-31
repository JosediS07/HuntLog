package com.huntlog.entrevista.dto;

import com.huntlog.entrevista.TipoEntrevista;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EntrevistaRequest(
        @NotNull TipoEntrevista tipo,
        @NotNull LocalDateTime fechaHora,
        Integer duracionMin,
        String entrevistador,
        String feedback,
        String notas
) {}
