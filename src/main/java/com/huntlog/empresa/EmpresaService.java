package com.huntlog.empresa;

import com.huntlog.empresa.dto.EmpresaRequest;
import com.huntlog.shared.exception.EntidadNoEncontradaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public Page<Empresa> listar(Long usuarioId, Pageable pageable) {
        return empresaRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public Empresa obtenerPorId(Long id, Long usuarioId) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("Empresa", id));
        if (!empresa.getUsuarioId().equals(usuarioId)) {
            throw new EntidadNoEncontradaException("Empresa", id);
        }
        return empresa;
    }

    @Transactional
    public Empresa crear(EmpresaRequest request, Long usuarioId) {
        Empresa empresa = new Empresa(
                request.nombre(),
                request.sitioWeb(),
                request.industria(),
                request.ubicacion(),
                request.logoUrl(),
                usuarioId
        );
        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa actualizar(Long id, EmpresaRequest request, Long usuarioId) {
        Empresa empresa = obtenerPorId(id, usuarioId);
        empresa.setNombre(request.nombre());
        empresa.setSitioWeb(request.sitioWeb());
        empresa.setIndustria(request.industria());
        empresa.setUbicacion(request.ubicacion());
        empresa.setLogoUrl(request.logoUrl());
        empresa.actualizar();
        return empresaRepository.save(empresa);
    }

    @Transactional
    public void eliminar(Long id, Long usuarioId) {
        Empresa empresa = obtenerPorId(id, usuarioId);
        empresaRepository.delete(empresa);
    }
}
