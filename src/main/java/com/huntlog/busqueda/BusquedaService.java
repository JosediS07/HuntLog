package com.huntlog.busqueda;

import com.huntlog.busqueda.dto.OfertaExternaResponse;
import com.huntlog.busqueda.port.BusquedaPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusquedaService {

    private final BusquedaPort busquedaPort;

    public BusquedaService(BusquedaPort busquedaPort) {
        this.busquedaPort = busquedaPort;
    }

    public List<OfertaExternaResponse> buscar(String query, String pais) {
        return busquedaPort.buscar(query, pais);
    }
}
