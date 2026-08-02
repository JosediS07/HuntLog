package com.huntlog.busqueda;

import com.huntlog.busqueda.dto.OfertaExternaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
public class BusquedaController {

    private final BusquedaService busquedaService;

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<OfertaExternaResponse>> buscar(
            @RequestParam String q,
            @RequestParam(defaultValue = "gb") String pais) {

        return ResponseEntity.ok(busquedaService.buscar(q, pais));
    }
}
