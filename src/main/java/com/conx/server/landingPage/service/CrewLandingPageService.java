package com.conx.server.landingPage.service;

import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CrewLandingPageService {
    private final ProjectRepository projectRepository;

    public CrewLandingPageService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectWrapperForLandingPageDTO> landing(IndustryForLandingPage category){
        if(category.equals(IndustryForLandingPage.ALL)){
            return projectRepository.findAllActiveProjectWithViews();
        } else {
            return projectRepository.findActiveProjectByCategoryWithViews(category.toIndustry());
        }
    }
}
