package com.huntlog.entrevista;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrevistas")
public class Entrevista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidatura_id", nullable = false)
    private Long candidaturaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEntrevista tipo;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "duracion_min")
    private Integer duracionMin;

    @Column(length = 150)
    private String entrevistador;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(nullable = false)
    private LocalDateTime creado = LocalDateTime.now();

    protected Entrevista() {}

    public Entrevista(Long candidaturaId, TipoEntrevista tipo, LocalDateTime fechaHora,
                      Integer duracionMin, String entrevistador, String feedback, String notas) {
        this.candidaturaId = candidaturaId;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.duracionMin = duracionMin;
        this.entrevistador = entrevistador;
        this.feedback = feedback;
        this.notas = notas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCandidaturaId() { return candidaturaId; }
    public TipoEntrevista getTipo() { return tipo; }
    public void setTipo(TipoEntrevista tipo) { this.tipo = tipo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public Integer getDuracionMin() { return duracionMin; }
    public void setDuracionMin(Integer duracionMin) { this.duracionMin = duracionMin; }
    public String getEntrevistador() { return entrevistador; }
    public void setEntrevistador(String entrevistador) { this.entrevistador = entrevistador; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public LocalDateTime getCreado() { return creado; }
}
