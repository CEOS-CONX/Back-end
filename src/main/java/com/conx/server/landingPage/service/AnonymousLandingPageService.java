package com.conx.server.landingPage.service;

import com.conx.server.landingPage.dto.AnonymousLandingPageResponseDTO;
import com.conx.server.landingPage.dto.CrewWrapperForLandingPageDTO;
import com.conx.server.landingPage.dto.IndustryForLandingPage;
import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.project.domain.Project;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.repository.CrewRepository;
import org.springframework.data.domain.Sort;
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
    public AnonymousLandingPageResponseDTO landing(){

        List<ProjectWrapperForLandingPageDTO> projects = projectRepository.findAll(Sort.by(Sort.Direction.DESC, "views"))
                .stream().map(ProjectWrapperForLandingPageDTO::from).toList();
        List<CrewWrapperForLandingPageDTO> crews = crewRepository.findAllActiveCrewsWithEvaluation();

        return new AnonymousLandingPageResponseDTO(projects, crews);
    }
}
