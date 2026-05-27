package com.conx.server.landingPage.service;

import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.landingPage.dto.ProjectWrapperForDashBoardDTO;
import com.conx.server.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CrewLandingPage {
    private final ProjectRepository projectRepository;

    public CrewLandingPage(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectWrapperForDashBoardDTO> landing(IndustryForLandingPage category){
        if(category.equals(IndustryForLandingPage.ALL)){
            return projectRepository.findAllActiveProjectWithViews();
        } else {
            return projectRepository.findActiveProjectByCategoryWithViews(category.toIndustry());
        }
    }
}
