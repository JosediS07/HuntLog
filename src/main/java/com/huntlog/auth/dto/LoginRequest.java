package com.huntlog.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}
