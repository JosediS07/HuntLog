package com.huntlog.admin;

import com.huntlog.admin.dto.AdminDashboardResponse;
import com.huntlog.auth.UserRepository;
import com.huntlog.candidatura.CandidaturaRepository;
import com.huntlog.candidatura.EstadoCandidatura;
import com.huntlog.empresa.EmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private CandidaturaRepository candidaturaRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    @Test
    void obtenerDashboard_agregaMetricasGlobales() {
        when(userRepository.count()).thenReturn(3L);
        when(empresaRepository.count()).thenReturn(5L);
        when(candidaturaRepository.count()).thenReturn(10L);
        when(candidaturaRepository.contarPorEstadoGlobal()).thenReturn(List.of(
                new Object[]{EstadoCandidatura.DRAFT, 4L},
                new Object[]{EstadoCandidatura.APPLIED, 6L}
        ));

        AdminDashboardResponse result = adminDashboardService.obtenerDashboard();

        assertEquals(3L, result.totalUsuarios());
        assertEquals(5L, result.totalEmpresas());
        assertEquals(10L, result.totalCandidaturas());
        assertEquals(2, result.candidaturasPorEstado().size());
        assertEquals(6L, result.candidaturasPorEstado().get("APPLIED"));
        assertEquals(0.6, result.tasaRespuesta(), 0.0001);
    }

    @Test
    void obtenerDashboard_sinCandidaturas_devuelveTasaCero() {
        when(userRepository.count()).thenReturn(1L);
        when(empresaRepository.count()).thenReturn(0L);
        when(candidaturaRepository.count()).thenReturn(0L);
        when(candidaturaRepository.contarPorEstadoGlobal()).thenReturn(List.<Object[]>of());

        AdminDashboardResponse result = adminDashboardService.obtenerDashboard();

        assertEquals(0L, result.totalCandidaturas());
        assertEquals(0.0, result.tasaRespuesta(), 0.0001);
    }

    @Test
    void obtenerDashboard_soloBorradores_devuelveTasaCero() {
        when(userRepository.count()).thenReturn(1L);
        when(empresaRepository.count()).thenReturn(1L);
        when(candidaturaRepository.count()).thenReturn(2L);
        when(candidaturaRepository.contarPorEstadoGlobal()).thenReturn(List.<Object[]>of(
                new Object[]{EstadoCandidatura.DRAFT, 2L}
        ));

        AdminDashboardResponse result = adminDashboardService.obtenerDashboard();

        assertEquals(0.0, result.tasaRespuesta(), 0.0001);
    }
}
