package com.huntlog.candidatura;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidaturas")
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 200)
    private String puesto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoCandidatura estado = EstadoCandidatura.DRAFT;

    @Column(name = "url_oferta", length = 500)
    private String urlOferta;

    @Column(name = "salario_min", precision = 12, scale = 2)
    private BigDecimal salarioMin;

    @Column(name = "salario_max", precision = 12, scale = 2)
    private BigDecimal salarioMax;

    @Column(length = 3)
    private String moneda = "EUR";

    @Column(length = 150)
    private String ubicacion;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "aplicado_en")
    private LocalDateTime aplicadoEn;

    @Column(name = "respondido_en")
    private LocalDateTime respondidoEn;

    @Column(nullable = false)
    private LocalDateTime creado = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime actualizado = LocalDateTime.now();

    protected Candidatura() {}

    public Candidatura(Long empresaId, Long usuarioId, String puesto, String urlOferta,
                       BigDecimal salarioMin, BigDecimal salarioMax, String moneda,
                       String ubicacion, String notas) {
        this.empresaId = empresaId;
        this.usuarioId = usuarioId;
        this.puesto = puesto;
        this.urlOferta = urlOferta;
        this.salarioMin = salarioMin;
        this.salarioMax = salarioMax;
        this.moneda = moneda != null ? moneda : "EUR";
        this.ubicacion = ubicacion;
        this.notas = notas;
    }

    public void cambiarEstado(EstadoCandidatura nuevoEstado) {
        if (!this.estado.puedeTransicionarA(nuevoEstado)) {
            throw new TransicionInvalidaException(
                "No se puede cambiar de " + this.estado + " a " + nuevoEstado
            );
        }
        if (nuevoEstado == EstadoCandidatura.APPLIED && this.estado == EstadoCandidatura.DRAFT) {
            this.aplicadoEn = LocalDateTime.now();
        }
        if (nuevoEstado != EstadoCandidatura.APPLIED && nuevoEstado != EstadoCandidatura.DRAFT
                && !nuevoEstado.esFinal() && this.aplicadoEn != null && this.respondidoEn == null) {
            this.respondidoEn = LocalDateTime.now();
        }
        this.estado = nuevoEstado;
        actualizar();
    }

    public void actualizar() {
        this.actualizado = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }
    public EstadoCandidatura getEstado() { return estado; }
    public void setEstado(EstadoCandidatura estado) { this.estado = estado; }
    public String getUrlOferta() { return urlOferta; }
    public void setUrlOferta(String urlOferta) { this.urlOferta = urlOferta; }
    public BigDecimal getSalarioMin() { return salarioMin; }
    public void setSalarioMin(BigDecimal salarioMin) { this.salarioMin = salarioMin; }
    public BigDecimal getSalarioMax() { return salarioMax; }
    public void setSalarioMax(BigDecimal salarioMax) { this.salarioMax = salarioMax; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public LocalDateTime getAplicadoEn() { return aplicadoEn; }
    public void setAplicadoEn(LocalDateTime aplicadoEn) { this.aplicadoEn = aplicadoEn; }
    public LocalDateTime getRespondidoEn() { return respondidoEn; }
    public void setRespondidoEn(LocalDateTime respondidoEn) { this.respondidoEn = respondidoEn; }
    public LocalDateTime getCreado() { return creado; }
    public LocalDateTime getActualizado() { return actualizado; }
}
