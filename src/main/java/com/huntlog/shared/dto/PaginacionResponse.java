package com.huntlog.shared.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginacionResponse<T>(
        List<T> contenido,
        int paginaActual,
        int tamanoPagina,
        long totalElementos,
        int totalPaginas
) {
    public static <T> PaginacionResponse<T> de(Page<T> page) {
        return new PaginacionResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
