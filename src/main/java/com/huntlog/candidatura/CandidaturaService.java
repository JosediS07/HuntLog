package com.huntlog.candidatura;

import com.huntlog.candidatura.dto.CandidaturaRequest;
import com.huntlog.candidatura.dto.CandidaturaResponse;
import com.huntlog.candidatura.exception.CandidaturaNoEncontradaException;
import com.huntlog.candidatura.exception.TransicionInvalidaException;
import com.huntlog.empresa.Empresa;
import com.huntlog.empresa.EmpresaRepository;
import com.huntlog.shared.exception.EntidadNoEncontradaException;
import com.huntlog.shared.exception.ReglaDeNegocioException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final HistorialEstadoRepository historialRepository;
    private final EmpresaRepository empresaRepository;

    public CandidaturaService(CandidaturaRepository candidaturaRepository,
                               HistorialEstadoRepository historialRepository,
                               EmpresaRepository empresaRepository) {
        this.candidaturaRepository = candidaturaRepository;
        this.historialRepository = historialRepository;
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public Page<CandidaturaResponse> listar(Long usuarioId, String estado, Long empresaId,
                                             LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                             BigDecimal salarioDesde, BigDecimal salarioHasta,
                                             Pageable pageable) {
        if (empresaId != null) {
            validarEmpresaDelUsuario(empresaId, usuarioId);
        }
        validarRangoDeFiltroSalarial(salarioDesde, salarioHasta);
        Specification<Candidatura> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("usuarioId"), usuarioId));
            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), EstadoCandidatura.valueOf(estado)));
            }
            if (empresaId != null) {
                predicates.add(cb.equal(root.get("empresaId"), empresaId));
            }
            if (fechaDesde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("creado"), fechaDesde));
            }
            if (fechaHasta != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("creado"), fechaHasta));
            }
            if (salarioDesde != null) {
                predicates.add(cb.isNotNull(root.get("salarioMax")));
                predicates.add(cb.greaterThanOrEqualTo(root.get("salarioMax"), salarioDesde));
            }
            if (salarioHasta != null) {
                predicates.add(cb.isNotNull(root.get("salarioMin")));
                predicates.add(cb.lessThanOrEqualTo(root.get("salarioMin"), salarioHasta));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Candidatura> pagina = candidaturaRepository.findAll(spec, pageable);
        Map<Long, String> nombresEmpresas = cargarNombresEmpresas(
                pagina.getContent().stream().map(Candidatura::getEmpresaId).toList());
        return pagina.map(candidatura -> toResponse(candidatura, nombresEmpresas));
    }

    @Transactional(readOnly = true)
    public CandidaturaResponse obtenerPorId(Long id, Long usuarioId) {
        Candidatura candidatura = findByIdAndUsuario(id, usuarioId);
        return toResponse(candidatura);
    }

    public CandidaturaResponse crear(CandidaturaRequest request, Long usuarioId) {
        Empresa empresa = validarEmpresaDelUsuario(request.empresaId(), usuarioId);
        validarRangoSalarial(request.salarioMin(), request.salarioMax());

        Candidatura candidatura = new Candidatura(
                empresa.getId(), usuarioId, request.puesto(),
                request.urlOferta(), request.salarioMin(), request.salarioMax(),
                request.moneda(), request.ubicacion(), request.notas()
        );
        candidatura = candidaturaRepository.save(candidatura);
        return toResponse(candidatura);
    }

    public CandidaturaResponse actualizar(Long id, CandidaturaRequest request, Long usuarioId) {
        Candidatura candidatura = findByIdAndUsuario(id, usuarioId);
        validarRangoSalarial(request.salarioMin(), request.salarioMax());
        candidatura.setPuesto(request.puesto());
        candidatura.setUrlOferta(request.urlOferta());
        candidatura.setSalarioMin(request.salarioMin());
        candidatura.setSalarioMax(request.salarioMax());
        candidatura.setMoneda(request.moneda());
        candidatura.setUbicacion(request.ubicacion());
        candidatura.setNotas(request.notas());
        candidatura.actualizar();
        candidatura = candidaturaRepository.save(candidatura);
        return toResponse(candidatura);
    }

    public CandidaturaResponse cambiarEstado(Long id, EstadoCandidatura nuevoEstado, Long usuarioId) {
        Candidatura candidatura = findByIdAndUsuario(id, usuarioId);
        EstadoCandidatura estadoAnterior = candidatura.getEstado();

        if (!estadoAnterior.puedeTransicionarA(nuevoEstado)) {
            throw new TransicionInvalidaException(
                "No se puede cambiar de " + estadoAnterior + " a " + nuevoEstado
            );
        }

        candidatura.cambiarEstado(nuevoEstado);
        candidatura = candidaturaRepository.save(candidatura);

        HistorialEstado historial = new HistorialEstado(
                candidatura.getId(),
                estadoAnterior.name(),
                nuevoEstado.name()
        );
        historialRepository.save(historial);

        return toResponse(candidatura);
    }

    public void eliminar(Long id, Long usuarioId) {
        Candidatura candidatura = findByIdAndUsuario(id, usuarioId);
        candidaturaRepository.deleteById(id);
    }

    private Candidatura findByIdAndUsuario(Long id, Long usuarioId) {
        Candidatura candidatura = candidaturaRepository.findById(id)
                .orElseThrow(() -> new CandidaturaNoEncontradaException(id));
        if (!candidatura.getUsuarioId().equals(usuarioId)) {
            throw new CandidaturaNoEncontradaException(id);
        }
        return candidatura;
    }

    private Empresa validarEmpresaDelUsuario(Long empresaId, Long usuarioId) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empresa", empresaId));
        if (!empresa.getUsuarioId().equals(usuarioId)) {
            throw new EntidadNoEncontradaException("Empresa", empresaId);
        }
        return empresa;
    }

    private void validarRangoSalarial(BigDecimal salarioMin, BigDecimal salarioMax) {
        if (salarioMin != null && salarioMax != null && salarioMin.compareTo(salarioMax) > 0) {
            throw new IllegalArgumentException("El salario mínimo no puede ser mayor que el máximo");
        }
    }

    private void validarRangoDeFiltroSalarial(BigDecimal salarioDesde, BigDecimal salarioHasta) {
        if (salarioDesde != null && salarioHasta != null && salarioDesde.compareTo(salarioHasta) > 0) {
            throw new ReglaDeNegocioException("El salario mínimo del filtro no puede ser mayor que el máximo");
        }
    }

    private Map<Long, String> cargarNombresEmpresas(List<Long> empresaIds) {
        if (empresaIds.isEmpty()) {
            return Map.of();
        }
        return empresaRepository.findAllById(empresaIds).stream()
                .collect(Collectors.toMap(Empresa::getId, Empresa::getNombre));
    }

    private CandidaturaResponse toResponse(Candidatura c) {
        return toResponse(c, cargarNombresEmpresas(List.of(c.getEmpresaId())));
    }

    private CandidaturaResponse toResponse(Candidatura c, Map<Long, String> nombresEmpresas) {
        String empresaNombre = nombresEmpresas.get(c.getEmpresaId());
        return new CandidaturaResponse(
                c.getId(), c.getEmpresaId(), empresaNombre,
                c.getPuesto(), c.getEstado().name(),
                c.getUrlOferta(), c.getSalarioMin(), c.getSalarioMax(),
                c.getMoneda(), c.getUbicacion(), c.getNotas(),
                c.getAplicadoEn(), c.getRespondidoEn(),
                c.getCreado(), c.getActualizado()
        );
    }
}
