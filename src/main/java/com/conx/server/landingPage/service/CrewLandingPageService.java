package com.conx.server.landingPage.service;

import com.conx.server.landingPage.dto.ProjectWrapperForLandingPageDTO;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CrewLandingPageService {

    private final ProjectRepository projectRepository;

    public CrewLandingPageService(
            ProjectRepository projectRepository
    ) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectWrapperForLandingPageDTO> landing() {
        return projectRepository.findAllByStatus(
                        ProjectStatus.RECRUITING,
                        Sort.by(
                                Sort.Direction.DESC,
                                "views"
                        )
                )
                .stream()
                .map(ProjectWrapperForLandingPageDTO::from)
                .toList();
    }
}