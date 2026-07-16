package com.conx.server.user.dto.company.response;

import com.conx.server.project.domain.enums.ProjectStatus;

import java.util.List;

public record CompanyTodoProjectResponseDTO(
        ProjectStatus status,
        List<TodoProjectWrapperDTO> projects
) {

}