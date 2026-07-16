package com.conx.server.user.dto.company.request;

import com.conx.server.domain.file.dto.FileRequestDTO;
import com.conx.server.project.domain.AdditionalLinksRequestDTO;
import com.conx.server.project.domain.ResultForm;
import com.conx.server.project.domain.ResultFormRequestDTO;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CompanyProjectRequestDTO(
        // 브랜드 정보
        @NotBlank(message = "브랜드이름을 입력해주세요")
        String brandName,
        @NotBlank(message = "담당자명을 입력해주세요")
        String managerName,
        @NotBlank(message = "담당자 이메일을 입력해주세요")
        String managerEmail,

        // 프로젝트 정보
        List<String> projectImages,
        @NotBlank(message = "프로젝트 이름을 입력해주세요")
        String projectName,

        @NotBlank(message = "프로젝트 설명을 입력해주세요")
        String projectExplanation,

        @NotNull(message = "산업분야를 입력해주세요")
        Industry industry,

        @NotNull(message = "프로젝트 유형을 입력해주세요")
        ProjectType projectType,

        @NotEmpty(message = "결과물을 입력해주세요")
        List<ResultFormRequestDTO> resultForm,

        // 일정
        @NotNull(message = "모집마감일을 입력해주세요")
        LocalDate recruitDeadline,
        @NotNull(message = "프로젝트 시작일을 입력해주세요")
        LocalDate projectStartDate,
        @NotNull(message = "프로젝트 마감일을 입력해주세요")
        LocalDate projectDeadline,
        @NotNull(message = "결과물 제출마감일을 입력해주세요")
        LocalDate submitDeadline,

        // 지원금
        @NotNull(message = "지원금을 입력해주세요")
        Long subsidy,
        @NotNull(message = "인센티브 지급일을 입력해주세요")
        Boolean incentive,
        String incentiveCondition,

        // 모집 조건
        @NotNull(message = "크루유형을 입력해주세요")
        CrewType crewType,
        @NotNull(message = "참여인원수를 입력해주세요")
        Integer peopleNumber,
        @NotBlank(message = "필수 역량을 입력해주세요")
        String competency,
        String preferenceCondition,

        // 참고자료
        List<FileRequestDTO> fileLinks,

        // 링크
        List<AdditionalLinksRequestDTO> additionalLinks
) {}