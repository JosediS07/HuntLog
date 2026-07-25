package com.huntlog.auth.dto;

public record RegisterRequest(
        String nombre,
        String email,
        String password
) {}
