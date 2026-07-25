package com.huntlog.candidatura.exception;

public class CandidaturaNoEncontradaException extends RuntimeException {

    public CandidaturaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public CandidaturaNoEncontradaException(Long id) {
        super("Candidatura con id " + id + " no encontrada");
    }
}
