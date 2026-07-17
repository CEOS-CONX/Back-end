package com.conx.server.user.repository;

import com.conx.server.user.domain.crew.CrewRepresentativeProject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CrewRepresentativeProjectRepository
        extends JpaRepository<CrewRepresentativeProject, Long> {

    @EntityGraph(
            attributePaths = {
                    "project",
                    "project.company"
            }
    )
    List<CrewRepresentativeProject>
    findAllByCrewIdOrderByDisplayOrderAsc(
            Long crewId
    );

    @Query("""
        select representativeProject.project.id
        from CrewRepresentativeProject representativeProject
        where representativeProject.crew.id = :crewId
        order by representativeProject.displayOrder asc
    """)
    List<Long> findProjectIdsByCrewId(
            @Param("crewId") Long crewId
    );

    @Modifying(flushAutomatically = true)
    @Query("""
        delete from CrewRepresentativeProject representativeProject
        where representativeProject.crew.id = :crewId
    """)
    void deleteAllByCrewId(
            @Param("crewId") Long crewId
    );
}