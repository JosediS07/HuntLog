package com.huntlog.empresa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaRequest(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 255) String sitioWeb,
        @Size(max = 100) String industria,
        @Size(max = 150) String ubicacion,
        @Size(max = 500) String logoUrl
) {}
