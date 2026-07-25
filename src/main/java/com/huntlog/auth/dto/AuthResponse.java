package com.huntlog.auth.dto;

public record AuthResponse(
        Long id,
        String token,
        String nombre,
        String email,
        String rol
) {}
