package com.huntlog.candidatura;

import java.util.Set;

public enum EstadoCandidatura {
    DRAFT,
    APPLIED,
    PHONE_SCREEN,
    TECHNICAL_INTERVIEW,
    FINAL_INTERVIEW,
    OFFER,
    HIRED,
    REJECTED,
    WITHDRAWN;

    private static final Set<EstadoCandidatura> FINALES = Set.of(HIRED, REJECTED, WITHDRAWN);

    public boolean puedeTransicionarA(EstadoCandidatura destino) {
        if (this == destino) return false;
        if (FINALES.contains(this)) return false;
        return switch (this) {
            case DRAFT -> destino == APPLIED || destino == WITHDRAWN;
            case APPLIED -> destino != DRAFT;
            case PHONE_SCREEN -> destino != DRAFT && destino != APPLIED;
            case TECHNICAL_INTERVIEW -> destino == FINAL_INTERVIEW || destino == OFFER || destino == REJECTED || destino == WITHDRAWN;
            case FINAL_INTERVIEW -> destino == OFFER || destino == REJECTED || destino == WITHDRAWN;
            case OFFER -> destino == HIRED || destino == REJECTED || destino == WITHDRAWN;
            default -> false;
        };
    }

    public boolean esFinal() {
        return FINALES.contains(this);
    }
}
