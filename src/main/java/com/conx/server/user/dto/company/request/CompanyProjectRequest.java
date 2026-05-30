package com.conx.server.user.dto.company.request;

import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;

import java.time.LocalDate;
import java.util.List;

public record CompanyProjectRequest(
        String projectImage,
        String brandName,
        String managerName,
        String managerEmail,
        String managerPhone,
        String name,
        String objectives,
        ProjectType projectType,
        String requirement,
        String projectExplanation,
        String resultForm,
        String essentialSubmitPart,
        LocalDate recruitDeadLine,
        LocalDate projectStartDate,
        LocalDate projectDeadline,
        LocalDate submitDeadline,
        CrewType crewType,
        String competency,
        String preferenceCondition,
        Long subsidy,
        Boolean incentive,
        String incentiveCondition,
        List<String> additionalFileLinks,
        String referenceLink
) {
}