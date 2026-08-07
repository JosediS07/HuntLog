package com.huntlog.shared.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitLoginFilterTest {

    private MockHttpServletRequest requestLogin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/v1/auth/login");
        request.setRemoteAddr("127.0.0.1");
        return request;
    }

    @Test
    void alSuperarElLimite_responde429() throws Exception {
        RateLimitLoginFilter filtro = new RateLimitLoginFilter(3, 60);

        for (int i = 0; i < 3; i++) {
            MockHttpServletResponse respuesta = new MockHttpServletResponse();
            MockFilterChain cadena = new MockFilterChain();
            filtro.doFilter(requestLogin(), respuesta, cadena);
            assertEquals(200, respuesta.getStatus());
            assertNotNull(cadena.getRequest());
        }

        MockHttpServletResponse bloqueada = new MockHttpServletResponse();
        MockFilterChain cadenaBloqueada = new MockFilterChain();
        filtro.doFilter(requestLogin(), bloqueada, cadenaBloqueada);

        assertEquals(429, bloqueada.getStatus());
        assertTrue(bloqueada.getContentType().startsWith("application/json"));
        assertNull(cadenaBloqueada.getRequest());
    }

    @Test
    void ventanaCero_nuncaBloquea() throws Exception {
        RateLimitLoginFilter filtro = new RateLimitLoginFilter(3, 0);

        for (int i = 0; i < 20; i++) {
            MockHttpServletResponse respuesta = new MockHttpServletResponse();
            MockFilterChain cadena = new MockFilterChain();
            filtro.doFilter(requestLogin(), respuesta, cadena);
            assertNotEquals(429, respuesta.getStatus());
        }
    }

    @Test
    void rutasQueNoSonLogin_noSeLimitan() throws Exception {
        RateLimitLoginFilter filtro = new RateLimitLoginFilter(1, 60);

        MockHttpServletRequest request = requestLogin();
        request.setMethod("GET");
        request.setRequestURI("/api/empresas");

        MockHttpServletResponse respuesta = new MockHttpServletResponse();
        MockFilterChain cadena = new MockFilterChain();
        filtro.doFilter(request, respuesta, cadena);

        assertEquals(200, respuesta.getStatus());
        assertNotNull(cadena.getRequest());
    }

    @Test
    void ipsDistintas_tienenLimitesIndependientes() throws Exception {
        RateLimitLoginFilter filtro = new RateLimitLoginFilter(1, 60);

        MockHttpServletResponse respuestaA = new MockHttpServletResponse();
        filtro.doFilter(requestLogin(), respuestaA, new MockFilterChain());
        assertEquals(200, respuestaA.getStatus());

        MockHttpServletRequest ipB = requestLogin();
        ipB.setRemoteAddr("10.0.0.2");
        MockHttpServletResponse respuestaB = new MockHttpServletResponse();
        filtro.doFilter(ipB, respuestaB, new MockFilterChain());
        assertEquals(200, respuestaB.getStatus());
    }
}
