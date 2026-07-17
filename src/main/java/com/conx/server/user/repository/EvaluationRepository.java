package com.conx.server.user.repository;

import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.dto.crew.response.CrewEvaluationWrapperDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepository
        extends JpaRepository<Evaluation, Long> {

    @Query("""
        select new com.conx.server.user.dto.crew.response.CrewEvaluationWrapperDTO(
            case
                when e is null or e.evaluationCount = 0 then cast(0.0 as double)
                else cast(
                    (e.completeness + e.schedule + e.ability + e.reCooperation + e.communication) * 1.0
                    / (5 * e.evaluationCount)
                as double)
            end,
            coalesce(avg(e.completeness), 0.0),
            coalesce(avg(e.ability), 0.0),
            coalesce(avg(e.communication), 0.0),
            coalesce(avg(e.schedule), 0.0),
            coalesce(avg(e.reCooperation), 0.0)
        )
        from Evaluation e
        where e.crew = :crew
    """)
    CrewEvaluationWrapperDTO getEvaluationByCrew(
            @Param("crew") Crew crew
    );

    @Query("""
        select case
                   when e is null or e.evaluationCount = 0 then cast(0.0 as double)
                   else cast(
                       (e.completeness + e.schedule + e.ability + e.reCooperation + e.communication) * 1.0
                       / (5 * e.evaluationCount)
                   as double)
               end
        from Evaluation e
        where e.crew = :crew
    """)
    Optional<Double> getMeanByCrew(
            @Param("crew") Crew crew
    );

    boolean existsByProjectId(
            Long projectId
    );

    @EntityGraph(attributePaths = {"project"})
    List<Evaluation> findAllByProjectIdIn(
            Collection<Long> projectIds
    );

    Evaluation findByCrew(Crew crew);
}