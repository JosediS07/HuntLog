package com.huntlog.shared.exception;

public class EntidadNoEncontradaException extends RuntimeException {

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public EntidadNoEncontradaException(String entidad, Long id) {
        super(entidad + " con id " + id + " no encontrada");
    }
}
