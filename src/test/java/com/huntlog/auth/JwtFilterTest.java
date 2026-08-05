package com.huntlog.auth;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void tokenValido_autenticaAlUsuario() throws Exception {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("7");
        when(claims.get("rol", String.class)).thenReturn("USER");
        when(jwtService.obtenerClaimsSiValido("token-valido")).thenReturn(claims);

        ejecutarFiltro("token-valido");

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(7L, authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void tokenInvalido_noAutentica() throws Exception {
        when(jwtService.obtenerClaimsSiValido("token-falso")).thenReturn(null);

        ejecutarFiltro("token-falso");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenSinSubject_noAutentica() throws Exception {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(null);
        when(jwtService.obtenerClaimsSiValido("token-sin-subject")).thenReturn(claims);

        ejecutarFiltro("token-sin-subject");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void tokenConSubjectNoNumerico_noAutentica() throws Exception {
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("no-numerico");
        when(jwtService.obtenerClaimsSiValido("token-mal")).thenReturn(claims);

        ejecutarFiltro("token-mal");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void sinHeaderAuthorization_noAutentica() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void ejecutarFiltro(String token) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilter(request, response, chain);
    }
}
