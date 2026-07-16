package com.conx.server.user.service.workspace;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.service.notificationFactory.NotificationFacadeService;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.ApplicationBrowseFilter;
import com.conx.server.project.dto.response.ProjectApplicationWrapperDTO;
import com.conx.server.project.dto.response.DetailedProjectWrapperForCrewWorkSpaceDTO;
import com.conx.server.project.dto.response.TodoProjectInfoDTO;
import com.conx.server.project.repository.ProjectApplicationRepository;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.project.repository.ProjectSubmissionRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.*;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.service.common.UserFinder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewWorkSpaceService {

    private final ProjectRepository projectRepository;
    private final UserFinder userFinder;
    private final ProjectSubmissionRepository projectSubmissionRepository;
    private final NotificationFacadeService notificationFacadeService;
    private final EvaluationRepository evaluationRepository;
    private final ProjectApplicationRepository projectApplicationRepository;

    private CrewProjectInfoDTO getProjectDashboard(Crew crew){
        int appliedProjectAmount = projectApplicationRepository.countByCrew(crew);
        int progressProjectAmount = projectRepository.countActiveProjectBySelectedCrew(crew);
        int doneProjectAmount = projectRepository.countFinishedProjectBySelectedCrew(crew);

        return new CrewProjectInfoDTO(appliedProjectAmount, progressProjectAmount, doneProjectAmount);
    }

    /**
     * 크루 대시보드 가져오기
     */
    public CrewDashboardResultDTO getCrewDashboard(CustomUserDetails customUserDetails){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());

        int totalSubsidy = crew.getTotalSubsidy();
        CrewEvaluationWrapperDTO evaluation = evaluationRepository.getEvaluationByCrew(crew);
        CrewProjectInfoDTO crewProjectInfo = getProjectDashboard(crew);
        List<Project> todoProjects = projectRepository.findByTodoProjectCrew(crew);

        List<TodoProjectInfoDTO> todoProjectInfoDTOS = TodoProjectInfoDTO.create(todoProjects);


        return new CrewDashboardResultDTO(totalSubsidy, evaluation, crewProjectInfo, todoProjectInfoDTOS);
    }

    /**
     * 프로젝트 지원현황 가져오기
     */
    public CrewApplicationStatusResponseDTO getCrewApplicationStatus(
            ApplicationBrowseFilter browseFilter,
            CustomUserDetails customUserDetails
    ){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());

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

    /**
     * 프로젝트 워크스페이스 가져오기
     */
    @Transactional(readOnly = true)
    public CrewWorkSpaceResponseDTO getCrewWorkSpace (CustomUserDetails customUserDetails){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());

        List<Project> projects = projectRepository.findAllBySelectedCrew(crew);
        List<ProjectWrapperForCrewWorkSpaceDTO> projectDTOS = projects.stream().map(
                ProjectWrapperForCrewWorkSpaceDTO::create
        ).toList();

        return new CrewWorkSpaceResponseDTO(projectDTOS);
    }

    /**
     * 프로젝트 상세 워크플레이스 가져오기
     * 이미 저장한 값이 있다면 그 정보를 가져옵니다.
     */
    @Transactional(readOnly = true)
    public CrewProjectWorkSpaceDTO getDetailedCrewWorkSpace(CustomUserDetails customUserDetails, long projectId){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());

        Project project = projectRepository.findBySelectedCrewAndId(crew, projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (project.isDone()){
            throw new CustomException(ErrorCode.PROJECT_ALREADY_END);
        }

        if (project.isBeforeSigningContract()){
            throw new CustomException(ErrorCode.PROJECT_CONTRACT_UNSIGNED);
        }

        ProjectSubmission submission = projectSubmissionRepository.findByProject(project)
                .orElse(
                        null
                );
        CrewProjectSubmissionDTO submissionDTO = CrewProjectSubmissionDTO.create(submission);
        DetailedProjectWrapperForCrewWorkSpaceDTO projectWrapper = DetailedProjectWrapperForCrewWorkSpaceDTO.create(project);
        ProjectSubmitConditionDTO submitCondition = ProjectSubmitConditionDTO.create(project);

        if (submission == null){
            return new CrewProjectWorkSpaceDTO(false, projectWrapper, null, submitCondition);
        } else {
            return new CrewProjectWorkSpaceDTO(submission.isEditable(), projectWrapper, submissionDTO, submitCondition);
        }
    }

    /**
     * 결과물 제출하기
     */
    @Transactional
    public void submitProjectResult(CustomUserDetails customUserDetails, long projectId,
                                    SubmitProjectResultRequestDTO req){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getId());
        Project project = projectRepository.findBySelectedCrewAndId(crew, projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (!project.isWaitingResult()){
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        if (project.isBeforeSigningContract()){
            throw new CustomException(ErrorCode.PROJECT_CONTRACT_UNSIGNED);
        }

        ProjectSubmission submission = ProjectSubmission.create(project,
                req.subject(), req.content(), req.fileLinks(), req.links());
        projectSubmissionRepository.save(submission);

        project.submitProjectResult();
        notificationFacadeService.saveNotificationAboutResultUploaded(project);
    }
}