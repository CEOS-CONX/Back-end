package com.conx.server.user.repository;

import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.dto.crew.response.CrewEvaluationWrapperDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    @Query("""
        select new com.conx.server.user.dto.crew.response.CrewEvaluationWrapperDTO(
            e.mean,
            e.completeness,
            e.ability,
            e.communication,
            e.schedule,
            e.recooperation
        )
        from Evaluation e
        where e.crew = :crew
    """)
    CrewEvaluationWrapperDTO getEvaluationByCrew(Crew crew);
}