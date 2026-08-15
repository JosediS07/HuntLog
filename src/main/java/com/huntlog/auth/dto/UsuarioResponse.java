package com.huntlog.auth.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String rol
) {}
