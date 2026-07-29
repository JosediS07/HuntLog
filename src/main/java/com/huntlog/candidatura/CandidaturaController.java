package com.huntlog.candidatura;

import com.huntlog.candidatura.dto.CambiarEstadoRequest;
import com.huntlog.candidatura.dto.CandidaturaRequest;
import com.huntlog.candidatura.dto.CandidaturaResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;

    public CandidaturaController(CandidaturaService candidaturaService) {
        this.candidaturaService = candidaturaService;
    }

    @GetMapping
    public ResponseEntity<Page<CandidaturaResponse>> listar(
            @AuthenticationPrincipal Long usuarioId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<CandidaturaResponse> result = candidaturaService.listar(
                usuarioId, estado, empresaId, fechaDesde, fechaHasta, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidaturaResponse> obtenerPorId(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {

        return ResponseEntity.ok(candidaturaService.obtenerPorId(id, usuarioId));
    }

    @PostMapping
    public ResponseEntity<CandidaturaResponse> crear(
            @AuthenticationPrincipal Long usuarioId,
            @Valid @RequestBody CandidaturaRequest request) {

        return ResponseEntity.ok(candidaturaService.crear(request, usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidaturaResponse> actualizar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id,
            @Valid @RequestBody CandidaturaRequest request) {

        return ResponseEntity.ok(candidaturaService.actualizar(id, request, usuarioId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CandidaturaResponse> cambiarEstado(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request) {

        EstadoCandidatura nuevoEstado = EstadoCandidatura.valueOf(request.nuevoEstado());
        return ResponseEntity.ok(candidaturaService.cambiarEstado(id, nuevoEstado, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {

        candidaturaService.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
