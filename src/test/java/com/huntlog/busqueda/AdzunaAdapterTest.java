package com.huntlog.busqueda;

import com.huntlog.busqueda.dto.OfertaExternaResponse;
import com.huntlog.shared.exception.ServicioExternoNoDisponibleException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AdzunaAdapterTest {

    private MockWebServer mockWebServer;
    private AdzunaAdapter adzunaAdapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        adzunaAdapter = new AdzunaAdapter(webClient,
                new AdzunaProperties("", "app-id-test", "app-key-test", 10));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void buscar_respuestaExitosa_devuelveOfertas() {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "results": [
                            {
                              "title": "Backend Developer",
                              "company": {"display_name": "Acme"},
                              "location": "London",
                              "redirect_url": "https://adzuna.com/oferta",
                              "description": "Java developer",
                              "salary_min": 40000.0,
                              "salary_max": 60000.0
                            }
                          ]
                        }
                        """));

        List<OfertaExternaResponse> result = adzunaAdapter.buscar("developer", "gb");

        assertEquals(1, result.size());
        OfertaExternaResponse oferta = result.getFirst();
        assertEquals("Backend Developer", oferta.titulo());
        assertEquals("Acme", oferta.empresa());
        assertEquals("London", oferta.ubicacion());
        assertEquals("https://adzuna.com/oferta", oferta.url());
        assertEquals("Java developer", oferta.descripcion());
        assertEquals(40000, oferta.salarioMin().intValue());
        assertEquals(60000, oferta.salarioMax().intValue());
    }

    @Test
    void buscar_sinResultados_devuelveListaVacia() {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"results\": []}"));

        List<OfertaExternaResponse> result = adzunaAdapter.buscar("inexistente", "gb");

        assertTrue(result.isEmpty());
    }

    @Test
    void buscar_apiDevuelveError_lanzaExcepcion() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThrows(ServicioExternoNoDisponibleException.class,
                () -> adzunaAdapter.buscar("developer", "gb"));
    }

    @Test
    void buscar_sinEmpresa_devuelveOfertaConEmpresaNula() {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "results": [
                            {
                              "title": "Frontend Developer",
                              "company": null,
                              "location": "Madrid",
                              "redirect_url": "https://adzuna.com/oferta2",
                              "description": "Angular",
                              "salary_min": null,
                              "salary_max": null
                            }
                          ]
                        }
                        """));

        List<OfertaExternaResponse> result = adzunaAdapter.buscar("frontend", "es");

        OfertaExternaResponse oferta = result.getFirst();
        assertNull(oferta.empresa());
        assertNull(oferta.salarioMin());
        assertNull(oferta.salarioMax());
    }

    @Test
    void buscar_timeoutExpirado_lanzaExcepcion() {
        mockWebServer.enqueue(new MockResponse()
                .setBodyDelay(3, TimeUnit.SECONDS)
                .setBody("{\"results\": []}"));

        AdzunaAdapter adapterLento = new AdzunaAdapter(
                WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build(),
                new AdzunaProperties("", "app-id-test", "app-key-test", 1));

        assertThrows(ServicioExternoNoDisponibleException.class,
                () -> adapterLento.buscar("developer", "gb"));
    }
}
