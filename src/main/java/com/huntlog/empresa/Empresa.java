package com.huntlog.empresa;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 255)
    private String sitioWeb;

    @Column(length = 100)
    private String industria;

    @Column(length = 150)
    private String ubicacion;

    @Column(length = 500)
    private String logoUrl;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime creado = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime actualizado = LocalDateTime.now();

    protected Empresa() {}

    public Empresa(String nombre, String sitioWeb, String industria, String ubicacion, String logoUrl, Long usuarioId) {
        this.nombre = nombre;
        this.sitioWeb = sitioWeb;
        this.industria = industria;
        this.ubicacion = ubicacion;
        this.logoUrl = logoUrl;
        this.usuarioId = usuarioId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }
    public String getIndustria() { return industria; }
    public void setIndustria(String industria) { this.industria = industria; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public Long getUsuarioId() { return usuarioId; }
    public LocalDateTime getCreado() { return creado; }
    public LocalDateTime getActualizado() { return actualizado; }
    public void actualizar() { this.actualizado = LocalDateTime.now(); }
}
