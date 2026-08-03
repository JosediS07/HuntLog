package com.huntlog.busqueda;

import com.huntlog.busqueda.dto.AdzunaResponse;
import com.huntlog.busqueda.dto.OfertaExternaResponse;
import com.huntlog.busqueda.port.BusquedaPort;
import com.huntlog.shared.exception.ServicioExternoNoDisponibleException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Component
public class AdzunaAdapter implements BusquedaPort {

    private static final int RESULTADOS_POR_PAGINA = 20;

    private final WebClient webClient;
    private final AdzunaProperties adzunaProperties;

    public AdzunaAdapter(WebClient webClient, AdzunaProperties adzunaProperties) {
        this.webClient = webClient;
        this.adzunaProperties = adzunaProperties;
    }

    @Override
    public List<OfertaExternaResponse> buscar(String query, String pais) {
        try {
            AdzunaResponse respuesta = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/{pais}/search/1")
                            .queryParam("app_id", adzunaProperties.appId())
                            .queryParam("app_key", adzunaProperties.appKey())
                            .queryParam("q", query)
                            .queryParam("results_per_page", RESULTADOS_POR_PAGINA)
                            .queryParam("content-type", "application/json")
                            .build(pais))
                    .retrieve()
                    .bodyToMono(AdzunaResponse.class)
                    .block(Duration.ofSeconds(adzunaProperties.timeoutSeconds()));

            if (respuesta == null || respuesta.results() == null) {
                throw new ServicioExternoNoDisponibleException("El servicio de ofertas no esta disponible");
            }
            return respuesta.results().stream()
                    .map(this::toOfertaExterna)
                    .toList();
        } catch (WebClientResponseException | WebClientRequestException ex) {
            throw new ServicioExternoNoDisponibleException("El servicio de ofertas no esta disponible");
        } catch (IllegalStateException ex) {
            throw new ServicioExternoNoDisponibleException("El servicio de ofertas no esta disponible");
        }
    }

    private OfertaExternaResponse toOfertaExterna(AdzunaResponse.AdzunaOferta oferta) {
        return new OfertaExternaResponse(
                oferta.title(),
                oferta.company() != null ? oferta.company().display_name() : null,
                oferta.location(),
                oferta.redirect_url(),
                oferta.description(),
                oferta.salary_min() != null ? BigDecimal.valueOf(oferta.salary_min()) : null,
                oferta.salary_max() != null ? BigDecimal.valueOf(oferta.salary_max()) : null
        );
    }
}
