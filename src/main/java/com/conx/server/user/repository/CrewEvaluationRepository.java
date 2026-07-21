package com.conx.server.user.repository;

import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrewEvaluationRepository extends JpaRepository<CrewEvaluation, Long> {
    CrewEvaluation findByCrew(Crew crew);
}
