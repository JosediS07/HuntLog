package com.huntlog.estadistica;

import com.huntlog.candidatura.CandidaturaRepository;
import com.huntlog.candidatura.EstadoCandidatura;
import com.huntlog.estadistica.dto.EstadisticaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstadisticaService {

    private static final double SEGUNDOS_POR_DIA = 86400.0;

    private final CandidaturaRepository candidaturaRepository;

    public EstadisticaService(CandidaturaRepository candidaturaRepository) {
        this.candidaturaRepository = candidaturaRepository;
    }

    @Transactional(readOnly = true)
    public EstadisticaResponse obtenerEstadisticas(Long usuarioId) {
        long totalCandidaturas = candidaturaRepository.countByUsuarioId(usuarioId);
        long candidaturasNoBorrador = candidaturaRepository.countByUsuarioIdAndEstadoNot(usuarioId, EstadoCandidatura.DRAFT);

        Map<String, Long> porEstado = new LinkedHashMap<>();
        candidaturaRepository.contarPorEstado(usuarioId).forEach(fila ->
                porEstado.put(((EstadoCandidatura) fila[0]).name(), (Long) fila[1]));

        double tasaRespuesta = totalCandidaturas > 0
                ? (double) candidaturasNoBorrador / totalCandidaturas
                : 0.0;

        double tiempoMedioRespuestaDias = calcularTiempoMedioRespuestaDias(usuarioId);

        return new EstadisticaResponse(totalCandidaturas, porEstado, tasaRespuesta, tiempoMedioRespuestaDias);
    }

    private double calcularTiempoMedioRespuestaDias(Long usuarioId) {
        List<Object[]> pares = candidaturaRepository.obtenerParesAplicadoRespondido(usuarioId);
        if (pares.isEmpty()) {
            return 0.0;
        }
        long segundosTotales = pares.stream()
                .mapToLong(par -> Duration.between((LocalDateTime) par[0], (LocalDateTime) par[1]).getSeconds())
                .sum();
        return (double) segundosTotales / pares.size() / SEGUNDOS_POR_DIA;
    }
}
