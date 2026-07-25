package com.huntlog.entrevista.exception;

public class EntrevistaNoEncontradaException extends RuntimeException {

    public EntrevistaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public EntrevistaNoEncontradaException(Long id) {
        super("Entrevista con id " + id + " no encontrada");
    }
}
