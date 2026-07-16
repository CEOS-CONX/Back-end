package com.conx.server.project.repository;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.dto.response.ProjectApplicationWrapperDTO;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.company.response.CompanyWorkSpaceForProjectApplicationDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long> {

    List<ProjectApplication> findAllByProjectId(Long projectId);

    Optional<ProjectApplication> findByIdAndProjectId(Long applicationId, Long projectId);

    List<ProjectApplication> findAllByProjectIdAndStatus(Long projectId, ProjectApplicationStatus status);

    int countByCrew(Crew crew);

    boolean existsByProjectIdAndCrewId(Long projectId, Long crewId);

    Optional<ProjectApplication> findByProjectIdAndCrewId(Long projectId, Long crewId);

    @Query("""
        select new com.conx.server.project.dto.response.ProjectApplicationWrapperDTO(
            p.id, pa.id,
            p.projectType, pa.createdAt, pa.status
        )
        from ProjectApplication pa
        join pa.project p
        where pa.crew = :crew
        order by pa.createdAt
    """)
    List<ProjectApplicationWrapperDTO> findProjectApplicationByCrew(
            @Param("crew") Crew crew
    );

    @Query("""
        select new com.conx.server.project.dto.response.ProjectApplicationWrapperDTO(
            p.id, pa.id,
            p.projectType, pa.createdAt, pa.status
        )
        from ProjectApplication pa
        join pa.project p
        where pa.crew = :crew
        and pa.status = :status
        order by pa.createdAt
    """)
    List<ProjectApplicationWrapperDTO> findProjectApplicationByCrewAndStatus(
            @Param("crew") Crew crew,
            @Param("status") ProjectApplicationStatus status
    );

    List<ProjectApplication> findAllByProject(Project project);
}


//long projectId,
//long applicationId,
//
//ProjectType projectType,
//LocalDate applyDate,
//ProjectApplicationStatus status