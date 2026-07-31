package com.huntlog.entrevista;

import com.huntlog.entrevista.dto.EntrevistaRequest;
import com.huntlog.entrevista.dto.EntrevistaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EntrevistaController {

    private final EntrevistaService entrevistaService;

    public EntrevistaController(EntrevistaService entrevistaService) {
        this.entrevistaService = entrevistaService;
    }

    @GetMapping("/api/candidaturas/{candidaturaId}/entrevistas")
    public ResponseEntity<List<EntrevistaResponse>> listar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long candidaturaId) {

        return ResponseEntity.ok(entrevistaService.listarPorCandidatura(candidaturaId, usuarioId));
    }

    @PostMapping("/api/candidaturas/{candidaturaId}/entrevistas")
    public ResponseEntity<EntrevistaResponse> crear(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long candidaturaId,
            @Valid @RequestBody EntrevistaRequest request) {

        return ResponseEntity.ok(entrevistaService.crear(candidaturaId, request, usuarioId));
    }

    @PutMapping("/api/entrevistas/{id}")
    public ResponseEntity<EntrevistaResponse> actualizar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id,
            @Valid @RequestBody EntrevistaRequest request) {

        return ResponseEntity.ok(entrevistaService.actualizar(id, request, usuarioId));
    }

    @DeleteMapping("/api/entrevistas/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal Long usuarioId,
            @PathVariable Long id) {

        entrevistaService.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
