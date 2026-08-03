package com.huntlog.candidatura.dto;

import jakarta.validation.constraints.NotBlank;

public record CambiarEstadoRequest(
        @NotBlank String nuevoEstado
) {}
