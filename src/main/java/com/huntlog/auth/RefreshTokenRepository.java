package com.huntlog.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revocadoEn = :ahora WHERE r.id = :id AND r.revocadoEn IS NULL")
    int revocarSiActivo(@Param("id") Long id, @Param("ahora") LocalDateTime ahora);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revocadoEn = :ahora WHERE r.usuario.id = :usuarioId AND r.revocadoEn IS NULL")
    int revocarTodosActivosDelUsuario(@Param("usuarioId") Long usuarioId, @Param("ahora") LocalDateTime ahora);
}
