package com.conx.server.user.service.workspace;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectSubmission;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.dto.response.ProjectWrapperForCrewWorkSpaceDTO;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.project.repository.ProjectSubmissionRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import com.conx.server.user.dto.crew.response.CrewProjectSubmissionDTO;
import com.conx.server.user.dto.crew.response.CrewProjectWorkSpaceDTO;
import com.conx.server.user.dto.crew.response.ProjectSubmitConditionDTO;
import com.conx.server.user.service.common.UserFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CrewWorkSpaceService {

    private final ProjectRepository projectRepository;
    private final UserFinder userFinder;
    private final ProjectSubmissionRepository projectSubmissionRepository;

    public CrewWorkSpaceService(ProjectRepository projectRepository, UserFinder userFinder, ProjectSubmissionRepository projectSubmissionRepository) {
        this.projectRepository = projectRepository;
        this.userFinder = userFinder;
        this.projectSubmissionRepository = projectSubmissionRepository;
    }

    /**
     * 프로젝트 상세 워크플레이스 가져오기
     * 이미 저장한 값이 있다면 그 정보를 가져옵니다.
     */
    @Transactional(readOnly = true)
    public CrewProjectWorkSpaceDTO getCrewWorkSpace(CustomUserDetails customUserDetails, long projectId){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getUserEmail());

        Project project = projectRepository.findBySelectedCrewAndId(crew, projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (project.getStatus()!=ProjectStatus.DONE){
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        ProjectWrapperForCrewWorkSpaceDTO projectWrapper = ProjectWrapperForCrewWorkSpaceDTO.create(project);
        ProjectSubmission submission = projectSubmissionRepository.findByProject(project)
                .orElse(
                        null
                );


        ProjectSubmitConditionDTO submitCondition = ProjectSubmitConditionDTO.create(project);
        CrewProjectSubmissionDTO submissionDTO = CrewProjectSubmissionDTO.create(submission);
        return new CrewProjectWorkSpaceDTO(projectWrapper, submissionDTO, submitCondition);
    }

    /**
     * 결과물 제출하기
     */
    @Transactional
    public void submitProjectResult(CustomUserDetails customUserDetails, long projectId,
                                    SubmitProjectResultRequestDTO req){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getUserEmail());
        Project project = projectRepository.findBySelectedCrewAndId(crew, projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (project.getStatus() != ProjectStatus.WAITING_RESULT){
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        Optional<ProjectSubmission> submissionOptional = projectSubmissionRepository.findByProject(
                project
        );

        if (submissionOptional.isPresent()){
            ProjectSubmission submission = submissionOptional.get();
            submission.update(req);
            submission.activateSubmission();
        } else {
            ProjectSubmission submission = ProjectSubmission.create(project, req.content(), req.fileLinks());
            projectSubmissionRepository.save(submission);
        }

        project.submitProjectResult();
    }

    /**
     * 결과물 임시 저장하기
     */
    @Transactional
    public void draftProjectResult(CustomUserDetails customUserDetails, long projectId,
                                   SubmitProjectResultRequestDTO req){
        Crew crew = userFinder.findActiveCrew(customUserDetails.getUserEmail());
        Project project = projectRepository.findBySelectedCrewAndId(crew, projectId).orElseThrow(
                () -> new CustomException(ErrorCode.PROJECT_NOT_FOUND)
        );

        if (project.getStatus() != ProjectStatus.WAITING_RESULT){
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        Optional<ProjectSubmission> submissionOptional = projectSubmissionRepository.findByProject(
                project
        );

        if (submissionOptional.isPresent()){
            submissionOptional.get().update(req);
        } else {
            ProjectSubmission submission = ProjectSubmission.createDraft(project, req.content(), req.fileLinks());
            projectSubmissionRepository.save(submission);
        }
    }
}
