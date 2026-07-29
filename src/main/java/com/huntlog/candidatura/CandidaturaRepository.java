package com.huntlog.candidatura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long>, JpaSpecificationExecutor<Candidatura> {
}
