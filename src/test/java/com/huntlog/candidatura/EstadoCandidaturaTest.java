package com.huntlog.candidatura;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoCandidaturaTest {

    @Test
    void draft_puedeIrAApplied() {
        assertTrue(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.APPLIED));
    }

    @Test
    void draft_puedeIrAWithdrawn() {
        assertTrue(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.WITHDRAWN));
    }

    @Test
    void draft_noPuedeIrAMismoEstado() {
        assertFalse(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.DRAFT));
    }

    @Test
    void draft_noPuedeIrAEntrevistaDirectamente() {
        assertFalse(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.PHONE_SCREEN));
        assertFalse(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.TECHNICAL_INTERVIEW));
        assertFalse(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.FINAL_INTERVIEW));
        assertFalse(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.OFFER));
        assertFalse(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.HIRED));
        assertFalse(EstadoCandidatura.DRAFT.puedeTransicionarA(EstadoCandidatura.REJECTED));
    }

    @Test
    void applied_puedeIrACualquierEstadoNoFinal() {
        assertTrue(EstadoCandidatura.APPLIED.puedeTransicionarA(EstadoCandidatura.PHONE_SCREEN));
        assertTrue(EstadoCandidatura.APPLIED.puedeTransicionarA(EstadoCandidatura.TECHNICAL_INTERVIEW));
        assertTrue(EstadoCandidatura.APPLIED.puedeTransicionarA(EstadoCandidatura.FINAL_INTERVIEW));
        assertTrue(EstadoCandidatura.APPLIED.puedeTransicionarA(EstadoCandidatura.OFFER));
    }

    @Test
    void applied_puedeIrARechazoORetiro() {
        assertTrue(EstadoCandidatura.APPLIED.puedeTransicionarA(EstadoCandidatura.REJECTED));
        assertTrue(EstadoCandidatura.APPLIED.puedeTransicionarA(EstadoCandidatura.WITHDRAWN));
    }

    @Test
    void applied_noPuedeVolverADraft() {
        assertFalse(EstadoCandidatura.APPLIED.puedeTransicionarA(EstadoCandidatura.DRAFT));
    }

    @Test
    void estadosFinales_noPermitenTransiciones() {
        for (var estado : new EstadoCandidatura[]{EstadoCandidatura.HIRED, EstadoCandidatura.REJECTED, EstadoCandidatura.WITHDRAWN}) {
            for (var destino : EstadoCandidatura.values()) {
                assertFalse(estado.puedeTransicionarA(destino));
            }
        }
    }

    @Test
    void hiredRejectedWithdrawn_sonFinales() {
        assertTrue(EstadoCandidatura.HIRED.esFinal());
        assertTrue(EstadoCandidatura.REJECTED.esFinal());
        assertTrue(EstadoCandidatura.WITHDRAWN.esFinal());
    }

    @Test
    void appliedNoEsFinal() {
        assertFalse(EstadoCandidatura.APPLIED.esFinal());
    }
}
