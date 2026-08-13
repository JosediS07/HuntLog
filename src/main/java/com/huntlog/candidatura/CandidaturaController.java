package com.huntlog.candidatura;

import com.huntlog.candidatura.dto.CambiarEstadoRequest;
import com.huntlog.candidatura.dto.CandidaturaRequest;
import com.huntlog.candidatura.dto.CandidaturaResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/candidaturas")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;
    private final ExportacionCandidaturaService exportacionService;

    public CandidaturaController(CandidaturaService candidaturaService,
                                 ExportacionCandidaturaService exportacionService) {
        this.candidaturaService = candidaturaService;
        this.exportacionService = exportacionService;
    }

    @GetMapping
    public ResponseEntity<Page<CandidaturaResponse>> listar(
            @AuthenticationPrincipal Long usuarioId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) BigDecimal salarioDesde,
            @RequestParam(required = false) BigDecimal salarioHasta,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<CandidaturaResponse> result = candidaturaService.listar(
                usuarioId, estado, empresaId, fechaDesde, fechaHasta,
                salarioDesde, salarioHasta, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(
            @AuthenticationPrincipal Long usuarioId,
            @RequestParam(defaultValue = "csv") String formato,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(required = false) BigDecimal salarioDesde,
            @RequestParam(required = false) BigDecimal salarioHasta) {

        List<CandidaturaResponse> candidaturas = candidaturaService.listarParaExportacion(
                usuarioId, estado, empresaId, fechaDesde, fechaHasta, salarioDesde, salarioHasta);

        return switch (formato.toLowerCase(Locale.ROOT)) {
            case "csv" -> construirDescarga(exportacionService.generarCsv(candidaturas),
                    new MediaType("text", "csv", StandardCharsets.UTF_8), "candidaturas.csv");
            case "pdf" -> construirDescarga(exportacionService.generarPdf(candidaturas),
                    MediaType.APPLICATION_PDF, "candidaturas.pdf");
            default -> throw new IllegalArgumentException("Formato de exportación no soportado: " + formato);
        };
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

    private ResponseEntity<byte[]> construirDescarga(byte[] contenido, MediaType tipo, String nombreArchivo) {
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(contenido);
    }
}
