package com.huntlog.entrevista;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrevistaRepository extends JpaRepository<Entrevista, Long> {

    List<Entrevista> findByCandidaturaIdOrderByFechaHoraAsc(Long candidaturaId);
}
