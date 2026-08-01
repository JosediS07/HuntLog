package com.huntlog.candidatura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long>, JpaSpecificationExecutor<Candidatura> {

    long countByUsuarioId(Long usuarioId);

    long countByUsuarioIdAndEstadoNot(Long usuarioId, EstadoCandidatura estado);

    @Query("SELECT c.estado, COUNT(c) FROM Candidatura c WHERE c.usuarioId = :usuarioId GROUP BY c.estado")
    List<Object[]> contarPorEstado(@Param("usuarioId") Long usuarioId);

    @Query("SELECT AVG(FUNCTION('date_part', 'epoch', c.respondidoEn) - FUNCTION('date_part', 'epoch', c.aplicadoEn)) " +
            "FROM Candidatura c WHERE c.usuarioId = :usuarioId " +
            "AND c.aplicadoEn IS NOT NULL AND c.respondidoEn IS NOT NULL")
    Double calcularTiempoMedioRespuestaSegundos(@Param("usuarioId") Long usuarioId);
}
