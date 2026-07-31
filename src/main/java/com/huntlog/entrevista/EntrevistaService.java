package com.huntlog.entrevista;

import com.huntlog.candidatura.CandidaturaRepository;
import com.huntlog.candidatura.exception.CandidaturaNoEncontradaException;
import com.huntlog.entrevista.dto.EntrevistaRequest;
import com.huntlog.entrevista.dto.EntrevistaResponse;
import com.huntlog.entrevista.exception.EntrevistaNoEncontradaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntrevistaService {

    private final EntrevistaRepository entrevistaRepository;
    private final CandidaturaRepository candidaturaRepository;

    public EntrevistaService(EntrevistaRepository entrevistaRepository, CandidaturaRepository candidaturaRepository) {
        this.entrevistaRepository = entrevistaRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    @Transactional(readOnly = true)
    public List<EntrevistaResponse> listarPorCandidatura(Long candidaturaId, Long usuarioId) {
        validarCandidaturaDelUsuario(candidaturaId, usuarioId);
        return entrevistaRepository.findByCandidaturaIdOrderByFechaHoraAsc(candidaturaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EntrevistaResponse crear(Long candidaturaId, EntrevistaRequest request, Long usuarioId) {
        validarCandidaturaDelUsuario(candidaturaId, usuarioId);

        Entrevista entrevista = new Entrevista(
                candidaturaId, request.tipo(), request.fechaHora(),
                request.duracionMin(), request.entrevistador(), request.feedback(), request.notas()
        );
        return toResponse(entrevistaRepository.save(entrevista));
    }

    @Transactional
    public EntrevistaResponse actualizar(Long id, EntrevistaRequest request, Long usuarioId) {
        Entrevista entrevista = obtenerEntrevistaDelUsuario(id, usuarioId);
        entrevista.setTipo(request.tipo());
        entrevista.setFechaHora(request.fechaHora());
        entrevista.setDuracionMin(request.duracionMin());
        entrevista.setEntrevistador(request.entrevistador());
        entrevista.setFeedback(request.feedback());
        entrevista.setNotas(request.notas());
        return toResponse(entrevistaRepository.save(entrevista));
    }

    @Transactional
    public void eliminar(Long id, Long usuarioId) {
        Entrevista entrevista = obtenerEntrevistaDelUsuario(id, usuarioId);
        entrevistaRepository.deleteById(entrevista.getId());
    }

    private void validarCandidaturaDelUsuario(Long candidaturaId, Long usuarioId) {
        candidaturaRepository.findById(candidaturaId)
                .filter(candidatura -> candidatura.getUsuarioId().equals(usuarioId))
                .orElseThrow(() -> new CandidaturaNoEncontradaException(candidaturaId));
    }

    private Entrevista obtenerEntrevistaDelUsuario(Long id, Long usuarioId) {
        Entrevista entrevista = entrevistaRepository.findById(id)
                .orElseThrow(() -> new EntrevistaNoEncontradaException(id));
        validarCandidaturaDelUsuario(entrevista.getCandidaturaId(), usuarioId);
        return entrevista;
    }

    private EntrevistaResponse toResponse(Entrevista e) {
        return new EntrevistaResponse(
                e.getId(), e.getCandidaturaId(), e.getTipo().name(),
                e.getFechaHora(), e.getDuracionMin(), e.getEntrevistador(),
                e.getFeedback(), e.getNotas(), e.getCreado()
        );
    }
}
