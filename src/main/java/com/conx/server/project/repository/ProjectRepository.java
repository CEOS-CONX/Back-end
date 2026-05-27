package com.conx.server.project.repository;

import com.conx.server.landingPage.dto.CrewWrapperDTOForDashboard;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.landingPage.dto.ProjectWrapperForDashBoardDTO;
import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.types.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query("""
        select new com.conx.server.landingPage.dto.ProjectWrapperForDashBoardDTO(
            p.id, p.projectImage, p.name, p.company.companyName, p.company.industry, p.projectType,
            p.projectStartDate, p.projectDeadline
        )
        from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        and p.company.industry = :category
        order by p.views
    """)
    List<ProjectWrapperForDashBoardDTO> findActiveProjectByCategoryWithViews(
            @Param("category") Industry category
    );

    @Query("""
        select new com.conx.server.landingPage.dto.ProjectWrapperForDashBoardDTO(
            p.id, p.projectImage, p.name, p.company.companyName, p.company.industry, p.projectType,
            p.projectStartDate, p.projectDeadline
        )
        from Project p
        where p.status = com.conx.server.project.domain.enums.ProjectStatus.RECRUITING
        order by p.views
    """)
    List<ProjectWrapperForDashBoardDTO> findAllActiveProjectWithViews(
    );
}
