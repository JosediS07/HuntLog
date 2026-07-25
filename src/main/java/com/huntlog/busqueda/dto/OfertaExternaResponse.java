package com.huntlog.busqueda.dto;

public record OfertaExternaResponse(
        String titulo,
        String empresa,
        String ubicacion,
        String url,
        String descripcion,
        java.math.BigDecimal salarioMin,
        java.math.BigDecimal salarioMax
) {}
