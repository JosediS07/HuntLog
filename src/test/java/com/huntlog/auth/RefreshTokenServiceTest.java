package com.huntlog.auth;

import com.huntlog.auth.exception.RefreshTokenInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final long REFRESH_EXPIRATION = 2592000000L;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    private final User usuario = crearUsuario();

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, REFRESH_EXPIRATION);
    }

    @Test
    void generar_guardaElHashDelTokenYLoDevuelve() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        String token = refreshTokenService.generar(usuario);

        assertNotNull(token);
        verify(refreshTokenRepository).save(argThat(guardado ->
                guardado.getTokenHash().equals(hash(token))
                        && guardado.getUsuario().getId().equals(1L)
                        && guardado.getRevocadoEn() == null));
    }

    @Test
    void rotar_tokenValido_revocaElAnteriorYDevuelveUnoNuevo() {
        String token = "refresh_valido";
        RefreshToken existente = new RefreshToken(usuario, hash(token), LocalDateTime.now().plusDays(30));
        existente.setRevocadoEn(null);
        when(refreshTokenRepository.findByTokenHash(hash(token))).thenReturn(Optional.of(existente));
        when(refreshTokenRepository.revocarSiActivo(eq(existente.getId()), any(LocalDateTime.class))).thenReturn(1);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshTokenService.RotacionResult resultado = refreshTokenService.rotar(token);

        assertNotNull(resultado.token());
        assertEquals(usuario, resultado.usuario());
        verify(refreshTokenRepository).revocarSiActivo(eq(existente.getId()), any(LocalDateTime.class));
    }

    @Test
    void rotar_tokenYaRevocado_revocaTodosYLanzaExcepcion() {
        String token = "refresh_reutilizado";
        RefreshToken existente = new RefreshToken(usuario, hash(token), LocalDateTime.now().plusDays(30));
        existente.setRevocadoEn(LocalDateTime.now());
        when(refreshTokenRepository.findByTokenHash(hash(token))).thenReturn(Optional.of(existente));

        RefreshTokenInvalidoException ex = assertThrows(RefreshTokenInvalidoException.class,
                () -> refreshTokenService.rotar(token));

        assertTrue(ex.getMessage().contains("reutilizado"));
        verify(refreshTokenRepository).revocarTodosActivosDelUsuario(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void rotar_tokenExpirado_lanzaExcepcion() {
        String token = "refresh_expirado";
        RefreshToken existente = new RefreshToken(usuario, hash(token), LocalDateTime.now().minusMinutes(1));
        when(refreshTokenRepository.findByTokenHash(hash(token))).thenReturn(Optional.of(existente));

        assertThrows(RefreshTokenInvalidoException.class, () -> refreshTokenService.rotar(token));
        verify(refreshTokenRepository, never()).revocarSiActivo(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void rotar_tokenDesconocido_lanzaExcepcion() {
        String token = "refresh_desconocido";
        when(refreshTokenRepository.findByTokenHash(hash(token))).thenReturn(Optional.empty());

        assertThrows(RefreshTokenInvalidoException.class, () -> refreshTokenService.rotar(token));
    }

    @Test
    void rotar_casSinFilasAfectadas_trataComoReuso() {
        String token = "refresh_carrera";
        RefreshToken existente = new RefreshToken(usuario, hash(token), LocalDateTime.now().plusDays(30));
        when(refreshTokenRepository.findByTokenHash(hash(token))).thenReturn(Optional.of(existente));
        when(refreshTokenRepository.revocarSiActivo(eq(existente.getId()), any(LocalDateTime.class))).thenReturn(0);

        RefreshTokenInvalidoException ex = assertThrows(RefreshTokenInvalidoException.class,
                () -> refreshTokenService.rotar(token));

        assertTrue(ex.getMessage().contains("reutilizado"));
        verify(refreshTokenRepository).revocarTodosActivosDelUsuario(eq(1L), any(LocalDateTime.class));
    }

    @Test
    void revocar_tokenActivo_loMarcaComoRevocado() {
        String token = "refresh_a_revocar";
        RefreshToken existente = new RefreshToken(usuario, hash(token), LocalDateTime.now().plusDays(30));
        when(refreshTokenRepository.findByTokenHash(hash(token))).thenReturn(Optional.of(existente));

        refreshTokenService.revocar(token);

        verify(refreshTokenRepository).revocarSiActivo(eq(existente.getId()), any(LocalDateTime.class));
    }

    @Test
    void revocar_tokenDesconocido_noLanzaExcepcion() {
        String token = "refresh_desconocido";
        when(refreshTokenRepository.findByTokenHash(hash(token))).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> refreshTokenService.revocar(token));
    }

    private User crearUsuario() {
        User user = new User("Juan", "juan@mail.com", "encoded", "USER");
        user.setId(1L);
        return user;
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
