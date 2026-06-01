package com.conx.server.user.service.workspace;

import com.conx.server.user.dto.crew.response.CrewDashboardResultDTO;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.dto.response.TodoProjectInfoDTO;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.crew.response.CrewEvaluationWrapperDTO;
import com.conx.server.user.dto.crew.response.CrewProjectInfoDTO;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.repository.EvaluationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewDashboardService {

    private final CrewRepository crewRepository;
    private final EvaluationRepository evaluationRepository;
    private final ProjectRepository projectRepository;
    private final ProjectApplicationRepository projectApplicationRepository;

    private CrewProjectInfoDTO getProjectDashboard(Crew crew){
        int appliedProjectAmount = projectApplicationRepository.countByCrew(crew);
        int progressProjectAmount = projectRepository.countActiveProjectBySelectedCrew(crew);
        int doneProjectAmount = projectRepository.countFinishedProjectBySelectedCrew(crew);

        return new CrewProjectInfoDTO(appliedProjectAmount, progressProjectAmount, doneProjectAmount);
    }

    /**
     * 크루 대시보드
     */
    public CrewDashboardResultDTO getCrewDashboard(CustomUserDetails customUserDetails){
        Crew crew = crewRepository.findByEmail(customUserDetails.getUserEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        int totalSubsidy = crew.getTotalSubsidy();
        CrewEvaluationWrapperDTO evaluation = evaluationRepository.getEvaluationByCrew(crew);
        CrewProjectInfoDTO crewProjectInfo = getProjectDashboard(crew);
        List<Project> todoProjects = projectRepository.findByTodoProjectCrew(crew);

        List<TodoProjectInfoDTO> todoProjectInfoDTOS = TodoProjectInfoDTO.create(todoProjects);


        return new CrewDashboardResultDTO(totalSubsidy, evaluation, crewProjectInfo, todoProjectInfoDTOS);
    }
}