package com.huntlog.estadistica.dto;

import java.util.Map;

public record EstadisticaResponse(
        long totalCandidaturas,
        Map<String, Long> porEstado,
        double tasaRespuesta,
        Double tiempoMedioRespuestaDias
) {}
