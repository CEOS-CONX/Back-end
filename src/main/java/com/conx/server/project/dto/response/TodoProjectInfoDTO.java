package com.conx.server.project.dto.response;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;

import java.util.List;

public record TodoProjectInfoDTO(
        long projectId,
        ProjectStatus projectStatus,
        String projectName
) {
    public static TodoProjectInfoDTO create(Project project){
        return new TodoProjectInfoDTO(project.getId(), project.getStatus(), project.getProjectName());
    }

    public static List<TodoProjectInfoDTO> create(List<Project> projects){
        return projects.stream().map(TodoProjectInfoDTO::create).toList();
    }
}
