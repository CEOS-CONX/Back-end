package com.conx.server.landingPage.service;

import com.conx.server.landingPage.dto.AnonymousLandingPageResponseDTO;
import com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.repository.CrewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnonymousLandingPageService {

    private final ProjectRepository projectRepository;
    private final CrewRepository crewRepository;

    public AnonymousLandingPageService(ProjectRepository projectRepository, CrewRepository crewRepository) {
        this.projectRepository = projectRepository;
        this.crewRepository = crewRepository;
    }

    @Transactional(readOnly = true)
    public AnonymousLandingPageResponseDTO landing(IndustryForLandingPage category){
        if(category.equals(IndustryForLandingPage.ALL)){

            List<ProjectWrapperForLandingPageDTO> projects = projectRepository.findAllActiveProjectWithViews();
            List<CrewWrapperForLandingPageDTO> crews = crewRepository.findAllActiveCrewsWithEvaluation();

            return new AnonymousLandingPageResponseDTO(projects, crews);
        } else {

            List<ProjectWrapperForLandingPageDTO> projects = projectRepository.findActiveProjectByCategoryWithViews(category.toIndustry());
            List<CrewWrapperForLandingPageDTO> crews = crewRepository.findActiveCrewsByCategoryWithEvaluation(category.toIndustry());

            return new AnonymousLandingPageResponseDTO(projects, crews);
        }
    }
}
