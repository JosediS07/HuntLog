package com.huntlog.admin.dto;

import java.util.Map;

public record AdminDashboardResponse(
        long totalUsuarios,
        long totalEmpresas,
        long totalCandidaturas,
        Map<String, Long> candidaturasPorEstado,
        double tasaRespuesta
) {}
