package com.huntlog.busqueda.port;

import com.huntlog.busqueda.dto.OfertaExternaResponse;

import java.util.List;

public interface BusquedaPort {

    List<OfertaExternaResponse> buscar(String query, String pais);
}
