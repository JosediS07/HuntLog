package com.huntlog.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expira_en", nullable = false)
    private LocalDateTime expiraEn;

    @Column(name = "revocado_en")
    private LocalDateTime revocadoEn;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    protected RefreshToken() {}

    public RefreshToken(User usuario, String tokenHash, LocalDateTime expiraEn) {
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.expiraEn = expiraEn;
        this.creadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUsuario() { return usuario; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public LocalDateTime getRevocadoEn() { return revocadoEn; }
    public void setRevocadoEn(LocalDateTime revocadoEn) { this.revocadoEn = revocadoEn; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
