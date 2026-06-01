package com.conx.server.user.service.workspace;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.ApplicationBrowseFilter;
import com.conx.server.project.dto.response.ProjectApplicationWrapperDTO;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.crew.response.CrewApplicationStatusResponseDTO;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.service.common.UserFinder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CrewApplicationStatusService {

    private final CrewRepository crewRepository;
    private final ProjectApplicationRepository projectApplicationRepository;

    public CrewApplicationStatusService(CrewRepository crewRepository, ProjectApplicationRepository projectApplicationRepository) {
        this.crewRepository = crewRepository;
        this.projectApplicationRepository = projectApplicationRepository;
    }

    public CrewApplicationStatusResponseDTO getCrewApplicationStatus(
            ApplicationBrowseFilter browseFilter,
            CustomUserDetails customUserDetails
    ){
        Crew crew = crewRepository.findByEmail(customUserDetails.getUserEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        List<ProjectApplicationWrapperDTO> crewApplicationStatus;
        if (browseFilter.equals(ApplicationBrowseFilter.ALL)){
             crewApplicationStatus = projectApplicationRepository
                    .findProjectApplicationByCrew(crew);
        } else {
            crewApplicationStatus = projectApplicationRepository
                    .findProjectApplicationByCrewAndStatus(crew, browseFilter.toApplicationStatus());
        }

        return new CrewApplicationStatusResponseDTO(crewApplicationStatus);
    }
}
