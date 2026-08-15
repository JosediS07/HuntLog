package com.huntlog.auth.dto;

public record AuthResponse(
        Long id,
        String nombre,
        String email,
        String rol,
        String accessToken,
        String refreshToken,
        Long expiraEn
) {}
