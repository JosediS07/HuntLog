package com.huntlog.admin;

import com.huntlog.admin.dto.AdminDashboardResponse;
import com.huntlog.auth.UserRepository;
import com.huntlog.candidatura.CandidaturaRepository;
import com.huntlog.candidatura.EstadoCandidatura;
import com.huntlog.empresa.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final EmpresaRepository empresaRepository;
    private final CandidaturaRepository candidaturaRepository;

    public AdminDashboardService(UserRepository userRepository,
                                 EmpresaRepository empresaRepository,
                                 CandidaturaRepository candidaturaRepository) {
        this.userRepository = userRepository;
        this.empresaRepository = empresaRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse obtenerDashboard() {
        long totalCandidaturas = candidaturaRepository.count();

        Map<String, Long> porEstado = new LinkedHashMap<>();
        candidaturaRepository.contarPorEstadoGlobal().forEach(fila ->
                porEstado.put(((EstadoCandidatura) fila[0]).name(), (Long) fila[1]));

        long noBorrador = totalCandidaturas - porEstado.getOrDefault("DRAFT", 0L);
        double tasaRespuesta = totalCandidaturas > 0
                ? (double) noBorrador / totalCandidaturas
                : 0.0;

        return new AdminDashboardResponse(
                userRepository.count(),
                empresaRepository.count(),
                totalCandidaturas,
                porEstado,
                tasaRespuesta
        );
    }
}
