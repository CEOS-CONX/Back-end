package com.conx.server.user.service.admin;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.dto.admin.response.AdminProjectContractCompleteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public AdminProjectContractCompleteResponse completeContract(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        if (project.getSelectedCrew() == null) {
            throw new CustomException(ErrorCode.PARTNER_CREW_NOT_FOUND);
        }

        if (project.getStatus() != ProjectStatus.CONTRACT_PENDING) {
            throw new CustomException(ErrorCode.INVALID_PROJECT_STATUS);
        }

        project.completeContract();

        return AdminProjectContractCompleteResponse.from(project);
    }
}