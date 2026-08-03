package com.huntlog.entrevista.dto;

import com.huntlog.entrevista.TipoEntrevista;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EntrevistaRequest(
        @NotNull TipoEntrevista tipo,
        @NotNull @Future LocalDateTime fechaHora,
        @Positive Integer duracionMin,
        @Size(max = 150) String entrevistador,
        String feedback,
        String notas
) {}
