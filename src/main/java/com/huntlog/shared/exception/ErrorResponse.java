package com.huntlog.shared.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        String error,
        String message,
        int status,
        LocalDateTime timestamp,
        List<String> detalles
) {
    public ErrorResponse(String error, String message, int status, LocalDateTime timestamp) {
        this(error, message, status, timestamp, List.of());
    }
}
