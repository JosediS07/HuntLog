package com.huntlog.empresa;

import com.huntlog.empresa.dto.EmpresaRequest;
import com.huntlog.empresa.dto.EmpresaResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<Page<EmpresaResponse>> listar(
            @AuthenticationPrincipal Long usuarioId,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<EmpresaResponse> empresas = empresaService.listar(usuarioId, pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> obtenerPorId(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {

        EmpresaResponse empresa = toResponse(empresaService.obtenerPorId(id, usuarioId));
        return ResponseEntity.ok(empresa);
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> crear(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody EmpresaRequest request) {

        EmpresaResponse empresa = toResponse(empresaService.crear(request, usuarioId));
        return ResponseEntity.ok(empresa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponse> actualizar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id,
            @Valid @RequestBody EmpresaRequest request) {

        EmpresaResponse empresa = toResponse(empresaService.actualizar(id, request, usuarioId));
        return ResponseEntity.ok(empresa);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {

        empresaService.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    private EmpresaResponse toResponse(Empresa empresa) {
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNombre(),
                empresa.getSitioWeb(),
                empresa.getIndustria(),
                empresa.getUbicacion(),
                empresa.getLogoUrl()
        );
    }
}
