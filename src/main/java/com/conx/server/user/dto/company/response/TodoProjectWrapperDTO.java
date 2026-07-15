package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;

import java.time.LocalDate;

public record TodoProjectWrapperDTO(
        long projectId,
        ProjectStatus projectStatus,
        String projectName,
        String brandName,
        LocalDate registerDate
) {
    public static TodoProjectWrapperDTO from(Project p) {
        return new TodoProjectWrapperDTO(p.getId(), p.getStatus(), p.getProjectName(), p.getBrandName(), p.getCreatedAt().toLocalDate());
    }
}
