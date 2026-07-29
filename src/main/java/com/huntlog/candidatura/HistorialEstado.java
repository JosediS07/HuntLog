package com.huntlog.candidatura;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estado")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidatura_id", nullable = false)
    private Long candidaturaId;

    @Column(name = "estado_anterior", length = 30)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private String estadoNuevo;

    @Column(name = "cambiado_en", nullable = false)
    private LocalDateTime cambiadoEn = LocalDateTime.now();

    protected HistorialEstado() {}

    public HistorialEstado(Long candidaturaId, String estadoAnterior, String estadoNuevo) {
        this.candidaturaId = candidaturaId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }

    public Long getId() { return id; }
    public Long getCandidaturaId() { return candidaturaId; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public LocalDateTime getCambiadoEn() { return cambiadoEn; }
}
