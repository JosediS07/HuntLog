package com.huntlog.auth;

import com.huntlog.auth.exception.RefreshTokenInvalidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpiration;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpiration = refreshExpiration;
    }

    @Transactional
    public String generar(User usuario) {
        String token = generarValorAleatorio();
        RefreshToken refreshToken = new RefreshToken(usuario, hash(token),
                LocalDateTime.now().plus(refreshExpiration, ChronoUnit.MILLIS));
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional
    public RotacionResult rotar(String token) {
        RefreshToken refreshToken = buscarPorHash(token);
        revocarTodosSiReuso(refreshToken);
        rechazarSiExpirado(refreshToken);
        validarUsuarioActivo(refreshToken.getUsuario());

        int revocados = refreshTokenRepository.revocarSiActivo(refreshToken.getId(), LocalDateTime.now());
        if (revocados == 0) {
            revocarTodosDelUsuario(refreshToken.getUsuario().getId());
            throw new RefreshTokenInvalidoException("Refresh token reutilizado");
        }

        String nuevoToken = generar(refreshToken.getUsuario());
        return new RotacionResult(nuevoToken, refreshToken.getUsuario());
    }

    @Transactional
    public void revocar(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        Optional<RefreshToken> existente = refreshTokenRepository.findByTokenHash(hash(token));
        existente.ifPresent(refreshToken ->
                refreshTokenRepository.revocarSiActivo(refreshToken.getId(), LocalDateTime.now()));
    }

    private RefreshToken buscarPorHash(String token) {
        if (token == null || token.isBlank()) {
            throw new RefreshTokenInvalidoException("Refresh token inválido");
        }
        return refreshTokenRepository.findByTokenHash(hash(token))
                .orElseThrow(() -> new RefreshTokenInvalidoException("Refresh token inválido"));
    }

    private void revocarTodosSiReuso(RefreshToken refreshToken) {
        if (refreshToken.getRevocadoEn() != null) {
            revocarTodosDelUsuario(refreshToken.getUsuario().getId());
            throw new RefreshTokenInvalidoException("Refresh token reutilizado");
        }
    }

    private void revocarTodosDelUsuario(Long usuarioId) {
        refreshTokenRepository.revocarTodosActivosDelUsuario(usuarioId, LocalDateTime.now());
    }

    private void rechazarSiExpirado(RefreshToken refreshToken) {
        if (!refreshToken.getExpiraEn().isAfter(LocalDateTime.now())) {
            throw new RefreshTokenInvalidoException("Refresh token expirado");
        }
    }

    private void validarUsuarioActivo(User usuario) {
        if (!usuario.getActivo()) {
            throw new RefreshTokenInvalidoException("Usuario no activo");
        }
    }

    private String generarValorAleatorio() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo SHA-256 no disponible", ex);
        }
    }

    public record RotacionResult(String token, User usuario) {}
}
