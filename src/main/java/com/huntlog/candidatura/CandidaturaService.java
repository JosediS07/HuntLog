package com.huntlog.candidatura;

import com.huntlog.candidatura.dto.CandidaturaRequest;
import com.huntlog.candidatura.dto.CandidaturaResponse;
import com.huntlog.candidatura.exception.CandidaturaNoEncontradaException;
import com.huntlog.candidatura.exception.TransicionInvalidaException;
import com.huntlog.empresa.Empresa;
import com.huntlog.empresa.EmpresaRepository;
import com.huntlog.shared.exception.EntidadNoEncontradaException;
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
                                             Pageable pageable) {
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
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return candidaturaRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CandidaturaResponse obtenerPorId(Long id, Long usuarioId) {
        Candidatura candidatura = findByIdAndUsuario(id, usuarioId);
        return toResponse(candidatura);
    }

    public CandidaturaResponse crear(CandidaturaRequest request, Long usuarioId) {
        Empresa empresa = empresaRepository.findById(request.empresaId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Empresa", request.empresaId()));

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

    private CandidaturaResponse toResponse(Candidatura c) {
        String empresaNombre = empresaRepository.findById(c.getEmpresaId())
                .map(Empresa::getNombre)
                .orElse(null);
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
