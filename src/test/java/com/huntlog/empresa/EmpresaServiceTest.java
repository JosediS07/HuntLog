package com.huntlog.empresa;

import com.huntlog.empresa.dto.EmpresaRequest;
import com.huntlog.shared.exception.EntidadNoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    @Test
    void listar_devuelvePaginaEmpresas() {
        Empresa empresa = new Empresa("Acme", "acme.com", "Tech", "Madrid", null, 1L);
        empresa.setId(1L);

        Page<Empresa> page = new PageImpl<>(List.of(empresa));
        when(empresaRepository.findByUsuarioId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<Empresa> result = empresaService.listar(1L, PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("Acme", result.getContent().getFirst().getNombre());
    }

    @Test
    void obtenerPorId_existe_devuelveEmpresa() {
        Empresa empresa = new Empresa("Acme", "acme.com", "Tech", "Madrid", null, 1L);
        empresa.setId(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        Empresa result = empresaService.obtenerPorId(1L, 1L);

        assertEquals("Acme", result.getNombre());
    }

    @Test
    void obtenerPorId_noExiste_lanzaExcepcion() {
        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class, () -> empresaService.obtenerPorId(999L, 1L));
    }

    @Test
    void obtenerPorId_perteneceAOtroUsuario_lanzaExcepcion() {
        Empresa empresa = new Empresa("Acme", "acme.com", "Tech", "Madrid", null, 1L);
        empresa.setId(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        assertThrows(EntidadNoEncontradaException.class, () -> empresaService.obtenerPorId(1L, 2L));
    }

    @Test
    void crear_empresaValida_guardaYDevuelve() {
        EmpresaRequest request = new EmpresaRequest("Acme", "acme.com", "Tech", "Madrid", null);
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(inv -> {
            Empresa e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        Empresa result = empresaService.crear(request, 1L);

        assertEquals(1L, result.getId());
        assertEquals("Acme", result.getNombre());
        assertEquals(1L, result.getUsuarioId());
        verify(empresaRepository).save(any(Empresa.class));
    }

    @Test
    void actualizar_empresaExistente_actualizaCampos() {
        Empresa empresa = new Empresa("Acme", "acme.com", "Tech", "Madrid", null, 1L);
        empresa.setId(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(inv -> inv.getArgument(0));

        EmpresaRequest request = new EmpresaRequest("Acme Inc", "acme-inc.com", "Fintech", "Barcelona", null);
        Empresa result = empresaService.actualizar(1L, request, 1L);

        assertEquals("Acme Inc", result.getNombre());
        assertEquals("acme-inc.com", result.getSitioWeb());
        assertEquals("Fintech", result.getIndustria());
        assertEquals("Barcelona", result.getUbicacion());
    }

    @Test
    void eliminar_empresaExistente_elimina() {
        Empresa empresa = new Empresa("Acme", "acme.com", "Tech", "Madrid", null, 1L);
        empresa.setId(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        empresaService.eliminar(1L, 1L);

        verify(empresaRepository).delete(empresa);
    }

    @Test
    void eliminar_empresaNoExiste_lanzaExcepcion() {
        when(empresaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaException.class, () -> empresaService.eliminar(999L, 1L));
        verify(empresaRepository, never()).delete(any());
    }
}
