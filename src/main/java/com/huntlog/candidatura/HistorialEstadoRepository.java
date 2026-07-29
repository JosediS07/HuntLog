package com.huntlog.candidatura;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    List<HistorialEstado> findByCandidaturaIdOrderByCambiadoEnAsc(Long candidaturaId);
}
