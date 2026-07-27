package com.huntlog.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET = "dW50cmFuc2FibGVzZWNyZXRrZXlmb3Jqd3QyNTZiaXRzbWluaW11bQ==";
    private static final long EXPIRATION = 86400000;

    @Test
    void generarToken_y_extraerClaims() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        User user = new User("Juan", "juan@mail.com", "pass", "USER");
        user.setId(1L);

        String token = jwtService.generarToken(user);

        assertNotNull(token);
        assertEquals(1L, jwtService.extraerUsuarioId(token));
        assertEquals("juan@mail.com", jwtService.extraerEmail(token));
        assertEquals("USER", jwtService.extraerRol(token));
    }

    @Test
    void esTokenValido_tokenValido_devuelveTrue() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        User user = new User("Juan", "juan@mail.com", "pass", "USER");
        user.setId(1L);

        String token = jwtService.generarToken(user);

        assertTrue(jwtService.esTokenValido(token));
    }

    @Test
    void esTokenValido_tokenInvalido_devuelveFalse() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        assertFalse(jwtService.esTokenValido("token_falso"));
    }
}
