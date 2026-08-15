package com.huntlog.auth.exception;

public class RefreshTokenInvalidoException extends RuntimeException {

    public RefreshTokenInvalidoException(String mensaje) {
        super(mensaje);
    }
}
