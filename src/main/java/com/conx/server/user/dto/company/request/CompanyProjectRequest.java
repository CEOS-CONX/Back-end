package com.conx.server.user.dto.company.request;

import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CompanyProjectRequest(
        String projectImage,
        @NotBlank(message = "브랜드이름을 입력해주세요") String brandName,
        @NotBlank(message = "직무 담당자 이름을 입력해주세요") String managerName,
        String managerEmail,
        String managerPhone,
        @NotBlank(message = "프로젝트 이름을 입력해주세요") String name,
        @NotBlank(message = "프로젝트 목표를 입력해주세요") String objectives,
        @NotNull(message = "프로젝트 유형을 입력해주세요") ProjectType projectType,
        String requirement,
        String projectExplanation,
        @NotBlank(message = "결과물 양식을 입력해주세요") String resultForm,
        @NotBlank(message = "필수제출항목을 입력해주세요") String essentialSubmitPart,
        @NotNull(message = "모집마감일을 입력해주세요") LocalDate recruitDeadLine,
        @NotNull(message = "프로젝트 시작일을 입력해주세요") LocalDate projectStartDate,
        @NotNull(message = "프로젝트 마감일을 입력해주세요") LocalDate projectDeadline,
        @NotNull(message = "프로젝트 결과 제출일을 입력해주세요") LocalDate submitDeadline,
        @NotBlank(message = "원하는 크루 유형을 입력해주세요") CrewType crewType,
        @NotBlank(message = "프로젝트 필수 역량을 입력해주세요") String competency,
        @NotBlank(message = "우대조건을 입력해주세요") String preferenceCondition,
        @NotBlank(message = "지원금을 입력해주세요") Long subsidy,
        @NotNull(message = "인센티브 여부를 골라주세요") Boolean incentive,
        String incentiveCondition,
        List<String> additionalFileLinks,
        String referenceLink
) {
}