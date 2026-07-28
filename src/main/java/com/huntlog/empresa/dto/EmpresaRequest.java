package com.huntlog.empresa.dto;

import jakarta.validation.constraints.NotBlank;

public record EmpresaRequest(
        @NotBlank String nombre,
        String sitioWeb,
        String industria,
        String ubicacion,
        String logoUrl
) {}
