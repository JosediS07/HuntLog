package com.huntlog.empresa.dto;

public record EmpresaResponse(
        Long id,
        String nombre,
        String sitioWeb,
        String industria,
        String ubicacion,
        String logoUrl
) {}
