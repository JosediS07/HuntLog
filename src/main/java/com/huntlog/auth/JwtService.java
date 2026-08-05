package com.huntlog.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET no está configurado. Defina la variable de entorno JWT_SECRET con una clave base64 de 256 bits.");
        }
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expiration = expiration;
    }

    public String generarToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("rol", user.getRol())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extraerUsuarioId(String token) {
        return Long.parseLong(extraerClaims(token).getSubject());
    }

    public String extraerEmail(String token) {
        return extraerClaims(token).get("email", String.class);
    }

    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    public Claims obtenerClaimsSiValido(String token) {
        try {
            Claims claims = extraerClaims(token);
            if (claims.getExpiration() == null || !claims.getExpiration().after(new Date())) {
                return null;
            }
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean esTokenValido(String token) {
        return obtenerClaimsSiValido(token) != null;
    }
}
