package com.huntlog.candidatura;

import com.huntlog.candidatura.dto.CambiarEstadoRequest;
import com.huntlog.candidatura.dto.CandidaturaRequest;
import com.huntlog.candidatura.dto.CandidaturaResponse;
import com.huntlog.candidatura.exception.CandidaturaNoEncontradaException;
import com.huntlog.candidatura.exception.TransicionInvalidaException;
import com.huntlog.empresa.Empresa;
import com.huntlog.empresa.EmpresaRepository;
import com.huntlog.shared.exception.EntidadNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidaturaServiceTest {

    @Mock
    private CandidaturaRepository candidaturaRepository;

    @Mock
    private HistorialEstadoRepository historialRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private CandidaturaService candidaturaService;

    private Empresa crearEmpresa(Long id, String nombre) {
        Empresa empresa = new Empresa(nombre, "web.com", "Tech", "Madrid", null, 1L);
        empresa.setId(id);
        return empresa;
    }

    @Test
    void crear_candidaturaValida_guardaYDevuelve() {
        CandidaturaRequest request = new CandidaturaRequest(
                1L, "Ingeniero", "url.com",
                BigDecimal.valueOf(30000), BigDecimal.valueOf(50000),
                "EUR", "Madrid", "notas"
        );
        Empresa empresa = crearEmpresa(1L, "Acme");
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.findAllById(List.of(1L))).thenReturn(List.of(empresa));
        when(candidaturaRepository.save(any(Candidatura.class))).thenAnswer(inv -> {
            Candidatura c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CandidaturaResponse result = candidaturaService.crear(request, 1L);

        assertEquals("Ingeniero", result.puesto());
        assertEquals("Acme", result.empresaNombre());
        assertEquals("DRAFT", result.estado());
        assertEquals(1L, result.empresaId());
        verify(candidaturaRepository).save(any(Candidatura.class));
    }

    @Test
    void crear_empresaDeOtroUsuario_lanzaExcepcion() {
        CandidaturaRequest request = new CandidaturaRequest(
                1L, "Ingeniero", "url.com",
                BigDecimal.valueOf(30000), BigDecimal.valueOf(50000),
                "EUR", "Madrid", "notas"
        );
        Empresa empresa = new Empresa("Acme", "web.com", "Tech", "Madrid", null, 2L);
        empresa.setId(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        assertThrows(EntidadNoEncontradaException.class, () -> candidaturaService.crear(request, 1L));
        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    void crear_salarioMinMayorQueMax_lanzaExcepcion() {
        CandidaturaRequest request = new CandidaturaRequest(
                1L, "Ingeniero", "url.com",
                BigDecimal.valueOf(50000), BigDecimal.valueOf(30000),
                "EUR", "Madrid", "notas"
        );
        Empresa empresa = crearEmpresa(1L, "Acme");
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        assertThrows(IllegalArgumentException.class, () -> candidaturaService.crear(request, 1L));
        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    void actualizar_salarioMinMayorQueMax_lanzaExcepcion() {
        Candidatura candidatura = new Candidatura(1L, 1L, "Ingeniero", null, null, null, null, null, null);
        candidatura.setId(1L);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(candidatura));

        CandidaturaRequest request = new CandidaturaRequest(
                1L, "Ingeniero", "url.com",
                BigDecimal.valueOf(50000), BigDecimal.valueOf(30000),
                "EUR", "Madrid", "notas"
        );

        assertThrows(IllegalArgumentException.class, () -> candidaturaService.actualizar(1L, request, 1L));
        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    void listar_conEmpresaDeOtroUsuario_lanzaExcepcion() {
        Empresa empresa = new Empresa("Acme", "web.com", "Tech", "Madrid", null, 2L);
        empresa.setId(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        assertThrows(EntidadNoEncontradaException.class,
                () -> candidaturaService.listar(1L, null, 1L, null, null, PageRequest.of(0, 20)));
        verify(candidaturaRepository, never()).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void obtenerPorId_existe_devuelveCandidatura() {
        Empresa empresa = crearEmpresa(1L, "Acme");
        when(empresaRepository.findAllById(List.of(1L))).thenReturn(List.of(empresa));

        Candidatura candidatura = new Candidatura(1L, 1L, "Ingeniero", null, null, null, null, null, null);
        candidatura.setId(1L);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(candidatura));

        CandidaturaResponse result = candidaturaService.obtenerPorId(1L, 1L);

        assertEquals("Ingeniero", result.puesto());
        assertEquals("DRAFT", result.estado());
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(candidaturaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CandidaturaNoEncontradaException.class, () -> candidaturaService.obtenerPorId(999L, 1L));
    }

    @Test
    void obtenerPorId_perteneceAOtroUsuario_lanzaExcepcion() {
        Candidatura candidatura = new Candidatura(1L, 1L, "Ingeniero", null, null, null, null, null, null);
        candidatura.setId(1L);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(candidatura));

        assertThrows(CandidaturaNoEncontradaException.class, () -> candidaturaService.obtenerPorId(1L, 2L));
    }

    @Test
    void actualizar_candidaturaExistente_actualizaCampos() {
        Empresa empresa = crearEmpresa(1L, "Acme");
        when(empresaRepository.findAllById(List.of(1L))).thenReturn(List.of(empresa));

        Candidatura candidatura = new Candidatura(1L, 1L, "Ingeniero", null, null, null, null, null, null);
        candidatura.setId(1L);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(candidatura));
        when(candidaturaRepository.save(any(Candidatura.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidaturaRequest request = new CandidaturaRequest(
                1L, "Senior Engineer", "url2.com",
                BigDecimal.valueOf(40000), BigDecimal.valueOf(60000),
                "USD", "Barcelona", "nuevas notas"
        );
        CandidaturaResponse result = candidaturaService.actualizar(1L, request, 1L);

        assertEquals("Senior Engineer", result.puesto());
        assertEquals("url2.com", result.urlOferta());
        assertEquals("Barcelona", result.ubicacion());
    }

    @Test
    void cambiarEstado_transicionValida_cambiaEstadoYGuardaHistorial() {
        Empresa empresa = crearEmpresa(1L, "Acme");
        when(empresaRepository.findAllById(List.of(1L))).thenReturn(List.of(empresa));

        Candidatura candidatura = new Candidatura(1L, 1L, "Ingeniero", null, null, null, null, null, null);
        candidatura.setId(1L);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(candidatura));
        when(candidaturaRepository.save(any(Candidatura.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidaturaResponse result = candidaturaService.cambiarEstado(1L, EstadoCandidatura.APPLIED, 1L);

        assertEquals("APPLIED", result.estado());
        assertNotNull(result.aplicadoEn());
        verify(historialRepository).save(any(HistorialEstado.class));
    }

    @Test
    void cambiarEstado_transicionInvalida_lanzaExcepcion() {
        Candidatura candidatura = new Candidatura(1L, 1L, "Ingeniero", null, null, null, null, null, null);
        candidatura.setId(1L);
        candidatura.setEstado(EstadoCandidatura.REJECTED);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(candidatura));

        assertThrows(TransicionInvalidaException.class,
                () -> candidaturaService.cambiarEstado(1L, EstadoCandidatura.APPLIED, 1L));
        verify(historialRepository, never()).save(any());
    }

    @Test
    void eliminar_candidaturaExistente_elimina() {
        Candidatura candidatura = new Candidatura(1L, 1L, "Ingeniero", null, null, null, null, null, null);
        candidatura.setId(1L);
        when(candidaturaRepository.findById(1L)).thenReturn(Optional.of(candidatura));

        candidaturaService.eliminar(1L, 1L);

        verify(candidaturaRepository).deleteById(1L);
    }

    @Test
    void eliminar_candidaturaNoExiste_lanzaExcepcion() {
        when(candidaturaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CandidaturaNoEncontradaException.class, () -> candidaturaService.eliminar(999L, 1L));
        verify(candidaturaRepository, never()).deleteById(any());
    }
}
