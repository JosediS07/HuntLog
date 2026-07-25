package com.huntlog.empresa.dto;

public record EmpresaRequest(
        String nombre,
        String sitioWeb,
        String industria,
        String ubicacion,
        String logoUrl
) {}
