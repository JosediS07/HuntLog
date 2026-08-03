package com.huntlog.auth.exception;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException() {
        super("El email ya está registrado");
    }
}
