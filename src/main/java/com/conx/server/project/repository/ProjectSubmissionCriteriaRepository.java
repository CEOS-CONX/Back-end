package com.conx.server.project.repository;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmissionCriteria;
import com.conx.server.user.domain.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ProjectSubmissionCriteriaRepository extends JpaRepository<ProjectSubmissionCriteria, Long> {

    @Query("""
    select sc
    from ProjectSubmissionCriteria sc
    join sc.project p
    where sc.id = :criteriaId
      and p.company = :company
    """)
    Optional<ProjectSubmissionCriteria> findByIdAndCompany(
            @Param("criteriaId") long criteriaId,
            @Param("company") Company company
    );
}