package com.huntlog.estadistica;

import com.huntlog.candidatura.CandidaturaRepository;
import com.huntlog.candidatura.EstadoCandidatura;
import com.huntlog.estadistica.dto.EstadisticaResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadisticaServiceTest {

    @Mock
    private CandidaturaRepository candidaturaRepository;

    @InjectMocks
    private EstadisticaService estadisticaService;

    private List<Object[]> filasPorEstado(Object... pares) {
        List<Object[]> filas = new ArrayList<>();
        for (int i = 0; i < pares.length; i += 2) {
            filas.add(new Object[]{pares[i], pares[i + 1]});
        }
        return filas;
    }

    @Test
    void obtenerEstadisticas_conCandidaturas_calculaMetricas() {
        when(candidaturaRepository.countByUsuarioId(1L)).thenReturn(10L);
        when(candidaturaRepository.countByUsuarioIdAndEstadoNot(1L, EstadoCandidatura.DRAFT)).thenReturn(4L);
        when(candidaturaRepository.contarPorEstado(1L)).thenReturn(filasPorEstado(
                EstadoCandidatura.DRAFT, 6L,
                EstadoCandidatura.APPLIED, 2L,
                EstadoCandidatura.REJECTED, 2L
        ));
        when(candidaturaRepository.calcularTiempoMedioRespuestaSegundos(1L)).thenReturn(172800.0);

        EstadisticaResponse result = estadisticaService.obtenerEstadisticas(1L);

        assertEquals(10L, result.totalCandidaturas());
        assertEquals(0.4, result.tasaRespuesta(), 0.001);
        assertEquals(2.0, result.tiempoMedioRespuestaDias(), 0.001);
        assertEquals(3, result.porEstado().size());
        assertEquals(6L, result.porEstado().get("DRAFT"));
        assertEquals(2L, result.porEstado().get("APPLIED"));
        assertEquals(2L, result.porEstado().get("REJECTED"));
    }

    @Test
    void obtenerEstadisticas_sinCandidaturas_devuelveCeros() {
        when(candidaturaRepository.countByUsuarioId(1L)).thenReturn(0L);
        when(candidaturaRepository.countByUsuarioIdAndEstadoNot(1L, EstadoCandidatura.DRAFT)).thenReturn(0L);
        when(candidaturaRepository.contarPorEstado(1L)).thenReturn(List.of());
        when(candidaturaRepository.calcularTiempoMedioRespuestaSegundos(1L)).thenReturn(null);

        EstadisticaResponse result = estadisticaService.obtenerEstadisticas(1L);

        assertEquals(0L, result.totalCandidaturas());
        assertEquals(0.0, result.tasaRespuesta(), 0.001);
        assertEquals(0.0, result.tiempoMedioRespuestaDias(), 0.001);
        assertTrue(result.porEstado().isEmpty());
    }

    @Test
    void obtenerEstadisticas_conRespuestasNull_tiempoMedioEsCero() {
        when(candidaturaRepository.countByUsuarioId(1L)).thenReturn(3L);
        when(candidaturaRepository.countByUsuarioIdAndEstadoNot(1L, EstadoCandidatura.DRAFT)).thenReturn(3L);
        when(candidaturaRepository.contarPorEstado(1L)).thenReturn(filasPorEstado(
                EstadoCandidatura.APPLIED, 3L
        ));
        when(candidaturaRepository.calcularTiempoMedioRespuestaSegundos(1L)).thenReturn(null);

        EstadisticaResponse result = estadisticaService.obtenerEstadisticas(1L);

        assertEquals(3L, result.totalCandidaturas());
        assertEquals(1.0, result.tasaRespuesta(), 0.001);
        assertEquals(0.0, result.tiempoMedioRespuestaDias(), 0.001);
    }

    @Test
    void obtenerEstadisticas_conMediaDecimal_calculaDias() {
        when(candidaturaRepository.countByUsuarioId(1L)).thenReturn(2L);
        when(candidaturaRepository.countByUsuarioIdAndEstadoNot(1L, EstadoCandidatura.DRAFT)).thenReturn(2L);
        when(candidaturaRepository.contarPorEstado(1L)).thenReturn(filasPorEstado(
                EstadoCandidatura.APPLIED, 2L
        ));
        when(candidaturaRepository.calcularTiempoMedioRespuestaSegundos(1L)).thenReturn(43200.0);

        EstadisticaResponse result = estadisticaService.obtenerEstadisticas(1L);

        assertEquals(0.5, result.tiempoMedioRespuestaDias(), 0.001);
    }
}
