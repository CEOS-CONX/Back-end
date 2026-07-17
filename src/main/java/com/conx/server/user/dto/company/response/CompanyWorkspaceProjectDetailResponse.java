package com.conx.server.user.dto.company.response;

import com.conx.server.domain.file.dto.FileResponseDTO;
import com.conx.server.project.domain.AdditionalLinksWrapper;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.dto.response.ResultFormResponse;
import com.conx.server.user.domain.types.CrewType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "status")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProjectApplicationForCompanyWrapperDTO.class, name = "RECRUITING"),
        @JsonSubTypes.Type(value = ProjectStatusResponseDTO.class, name = "PROGRESS")
})
public sealed interface CompanyWorkspaceProjectDetailResponse
        permits ProjectApplicationForCompanyWrapperDTO,
        ProjectStatusResponseDTO {
}