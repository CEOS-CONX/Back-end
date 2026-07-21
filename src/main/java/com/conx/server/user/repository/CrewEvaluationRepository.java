package com.conx.server.user.repository;

import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrewEvaluationRepository extends JpaRepository<CrewEvaluation, Long> {
    Optional<CrewEvaluation> findByCrew(Crew crew);
}
