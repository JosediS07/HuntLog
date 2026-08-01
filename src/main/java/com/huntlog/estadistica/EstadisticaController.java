package com.huntlog.estadistica;

import com.huntlog.estadistica.dto.EstadisticaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping
    public ResponseEntity<EstadisticaResponse> obtenerEstadisticas(@AuthenticationPrincipal Long usuarioId) {
        return ResponseEntity.ok(estadisticaService.obtenerEstadisticas(usuarioId));
    }
}
