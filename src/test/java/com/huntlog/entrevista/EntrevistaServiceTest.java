package com.huntlog.entrevista;

import com.huntlog.candidatura.Candidatura;
import com.huntlog.candidatura.CandidaturaRepository;
import com.huntlog.candidatura.exception.CandidaturaNoEncontradaException;
import com.huntlog.entrevista.dto.EntrevistaRequest;
import com.huntlog.entrevista.dto.EntrevistaResponse;
import com.huntlog.entrevista.exception.EntrevistaNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrevistaServiceTest {

    @Mock
    private EntrevistaRepository entrevistaRepository;

    @Mock
    private CandidaturaRepository candidaturaRepository;

    @InjectMocks
    private EntrevistaService entrevistaService;

    private Candidatura crearCandidatura(Long id, Long usuarioId) {
        Candidatura candidatura = new Candidatura(1L, usuarioId, "Ingeniero", null, null, null, null, null, null);
        candidatura.setId(id);
        return candidatura;
    }

    private EntrevistaRequest crearRequest() {
        return new EntrevistaRequest(
                TipoEntrevista.TECHNICAL,
                LocalDateTime.of(2026, 8, 1, 10, 0),
                60, "Maria", "Bien", "Preparar algoritmos"
        );
    }

    @Test
    void listarPorCandidatura_candidaturaDelUsuario_devuelveEntrevistas() {
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(crearCandidatura(1L, 1L)));

        Entrevista entrevista = new Entrevista(1L, TipoEntrevista.PHONE, LocalDateTime.now(), 30, "Juan", null, null);
        entrevista.setId(1L);
        when(entrevistaRepository.findByCandidaturaIdOrderByFechaHoraAsc(1L)).thenReturn(List.of(entrevista));

        List<EntrevistaResponse> result = entrevistaService.listarPorCandidatura(1L, 1L);

        assertEquals(1, result.size());
        assertEquals("PHONE", result.getFirst().tipo());
    }

    @Test
    void listarPorCandidatura_candidaturaDeOtroUsuario_lanzaExcepcion() {
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(crearCandidatura(1L, 1L)));

        assertThrows(CandidaturaNoEncontradaException.class, () -> entrevistaService.listarPorCandidatura(1L, 2L));
        verify(entrevistaRepository, never()).findByCandidaturaIdOrderByFechaHoraAsc(any());
    }

    @Test
    void listarPorCandidatura_candidaturaNoExiste_lanzaExcepcion() {
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CandidaturaNoEncontradaException.class, () -> entrevistaService.listarPorCandidatura(1L, 1L));
    }

    @Test
    void crear_candidaturaValida_guardaYDevuelve() {
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(crearCandidatura(1L, 1L)));
        when(entrevistaRepository.save(any(Entrevista.class))).thenAnswer(inv -> {
            Entrevista e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        EntrevistaResponse result = entrevistaService.crear(1L, crearRequest(), 1L);

        assertEquals("TECHNICAL", result.tipo());
        assertEquals(1L, result.candidaturaId());
        assertEquals(60, result.duracionMin());
        verify(entrevistaRepository).save(any(Entrevista.class));
    }

    @Test
    void crear_candidaturaNoExiste_lanzaExcepcion() {
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CandidaturaNoEncontradaException.class, () -> entrevistaService.crear(1L, crearRequest(), 1L));
        verify(entrevistaRepository, never()).save(any());
    }

    @Test
    void actualizar_entrevistaDelUsuario_actualizaCampos() {
        Entrevista entrevista = new Entrevista(1L, TipoEntrevista.PHONE, LocalDateTime.now(), 30, "Juan", null, null);
        entrevista.setId(1L);
        when(entrevistaRepository.findById(1L)).thenReturn(Optional.of(entrevista));
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(crearCandidatura(1L, 1L)));
        when(entrevistaRepository.save(any(Entrevista.class))).thenAnswer(inv -> inv.getArgument(0));

        EntrevistaRequest request = new EntrevistaRequest(
                TipoEntrevista.MANAGER,
                LocalDateTime.of(2026, 8, 5, 15, 0),
                45, "Laura", "Excelente", null
        );
        EntrevistaResponse result = entrevistaService.actualizar(1L, request, 1L);

        assertEquals("MANAGER", result.tipo());
        assertEquals(45, result.duracionMin());
        assertEquals("Laura", result.entrevistador());
    }

    @Test
    void actualizar_entrevistaNoExiste_lanzaExcepcion() {
        when(entrevistaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntrevistaNoEncontradaException.class, () -> entrevistaService.actualizar(999L, crearRequest(), 1L));
    }

    @Test
    void eliminar_entrevistaDelUsuario_elimina() {
        Entrevista entrevista = new Entrevista(1L, TipoEntrevista.PHONE, LocalDateTime.now(), 30, "Juan", null, null);
        entrevista.setId(1L);
        when(entrevistaRepository.findById(1L)).thenReturn(Optional.of(entrevista));
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(crearCandidatura(1L, 1L)));

        entrevistaService.eliminar(1L, 1L);

        verify(entrevistaRepository).deleteById(1L);
    }

    @Test
    void eliminar_entrevistaDeOtroUsuario_lanzaExcepcion() {
        Entrevista entrevista = new Entrevista(1L, TipoEntrevista.PHONE, LocalDateTime.now(), 30, "Juan", null, null);
        entrevista.setId(1L);
        when(entrevistaRepository.findById(1L)).thenReturn(Optional.of(entrevista));
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(crearCandidatura(1L, 1L)));

        assertThrows(CandidaturaNoEncontradaException.class, () -> entrevistaService.eliminar(1L, 2L));
        verify(entrevistaRepository, never()).deleteById(any());
    }
}
