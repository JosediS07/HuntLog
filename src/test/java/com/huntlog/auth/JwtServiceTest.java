package com.huntlog.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.Date;

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

    @Test
    void obtenerClaimsSiValido_tokenValido_devuelveClaims() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        User user = new User("Juan", "juan@mail.com", "pass", "USER");
        user.setId(1L);

        String token = jwtService.generarToken(user);

        assertEquals("1", jwtService.obtenerClaimsSiValido(token).getSubject());
    }

    @Test
    void obtenerClaimsSiValido_tokenInvalido_devuelveNull() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        assertNull(jwtService.obtenerClaimsSiValido("token_falso"));
    }

    @Test
    void obtenerClaimsSiValido_tokenSinSubject_devuelveClaimsSinSubject() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        String token = Jwts.builder()
                .claim("email", "juan@mail.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET)))
                .compact();

        assertNotNull(jwtService.obtenerClaimsSiValido(token));
        assertNull(jwtService.obtenerClaimsSiValido(token).getSubject());
    }

    @Test
    void generarToken_conExpiracionPersonalizada_respetaExpiracion() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        User user = new User("Juan", "juan@mail.com", "pass", "USER");
        user.setId(1L);

        String token = jwtService.generarToken(user, 5000);

        long expiraEn = jwtService.expiraEn(token);
        long ahora = System.currentTimeMillis();
        assertTrue(expiraEn > ahora);
        assertTrue(expiraEn <= ahora + 6000);
    }

    @Test
    void expiraEn_devuelveEpochMsDeExpiracion() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);

        User user = new User("Juan", "juan@mail.com", "pass", "USER");
        user.setId(1L);

        String token = jwtService.generarToken(user);

        long expiraEn = jwtService.expiraEn(token);
        long ahora = System.currentTimeMillis();
        assertTrue(expiraEn > ahora);
        assertTrue(expiraEn <= ahora + EXPIRATION + 1000);
    }
}
