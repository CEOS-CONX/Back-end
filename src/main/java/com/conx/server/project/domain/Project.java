package com.conx.server.project.domain;

import com.conx.server.domain.file.dto.FileRequestDTO;
import com.conx.server.global.BaseEntity;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    private Project(
            Company company,
            Crew selectedCrew,
            String brandName,
            String managerName,
            String managerEmail,
            List<String> projectImage,
            String projectName,
            String projectExplanation,
            Industry industry,
            ProjectType projectType,
            List<ResultForm> resultForm,
            LocalDate recruitDeadLine,
            LocalDate projectStartDate,
            LocalDate projectDeadline,
            LocalDate submitDeadline,
            long subsidy,
            boolean incentive,
            String incentiveCondition,
            CrewType crewType,
            int peopleNumber,
            String competency,
            String preferenceCondition,
            List<String> fileLinks,
            List<AdditionalLinksWrapper> links,
            ProjectStatus status,
            int views
    ) {
        this.company = company;
        this.selectedCrew = selectedCrew;
        this.brandName = brandName;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.projectImage = copyList(projectImage);
        this.projectName = projectName;
        this.projectExplanation = projectExplanation;
        this.industry = industry;
        this.projectType = projectType;
        this.resultForm = copyList(resultForm);
        this.recruitDeadLine = recruitDeadLine;
        this.projectStartDate = projectStartDate;
        this.projectDeadline = projectDeadline;
        this.submitDeadline = submitDeadline;
        this.subsidy = subsidy;
        this.incentive = incentive;
        this.incentiveCondition = incentiveCondition;
        this.crewType = crewType;
        this.peopleNumber = peopleNumber;
        this.competency = competency;
        this.preferenceCondition = preferenceCondition;
        this.fileLinks = copyList(fileLinks);
        this.links = copyList(links);
        this.status = status;
        this.views = views;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "selected_crew_id")
    private Crew selectedCrew;

    /*
     * 브랜드 정보
     */
    private String brandName;

    private String managerName;

    private String managerEmail;

    /*
     * 프로젝트 이미지: 최대 개수 검증은 요청 DTO 또는 Service에서 처리한다.
     */
    @ElementCollection
    @CollectionTable(
            name = "project_images",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<String> projectImage =
            new ArrayList<>();

    private String projectName;

    private String projectExplanation;

    private Industry industry;

    private ProjectType projectType;

    @ElementCollection
    @CollectionTable(
            name = "project_result_form",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<ResultForm> resultForm =
            new ArrayList<>();

    /*
     * 프로젝트 일정
     */
    private LocalDate recruitDeadLine;

    private LocalDate crewSelectedDate;

    private LocalDate projectStartDate;

    private LocalDate projectDeadline;

    private LocalDate submitDeadline;

    private LocalDate resultSubmittedDate;

    private LocalDate projectEndedDate;

    /*
     * 지원금 및 인센티브
     */
    private long subsidy;

    private boolean incentive;

    private String incentiveCondition;

    /*
     * 모집 크루 조건
     */
    private CrewType crewType;

    private int peopleNumber;

    private String competency;

    private String preferenceCondition;

    /*
     * 프로젝트 참고 파일 URL
     */
    @ElementCollection
    @CollectionTable(
            name = "file_links",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<String> fileLinks =
            new ArrayList<>();

    /*
     * 프로젝트 추가 링크
     */
    @ElementCollection
    @CollectionTable(
            name = "project_links",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<AdditionalLinksWrapper> links =
            new ArrayList<>();

    private ProjectStatus status;

    private int views;

    public static Project createWithStatus(
            Company company,
            CompanyProjectRequestDTO request,
            ProjectStatus status
    ) {
        List<ResultForm> resultForms =
                request.resultForm() == null
                        ? List.of()
                        : request.resultForm()
                        .stream()
                        .map(ResultForm::from)
                        .toList();

        List<String> fileLinks =
                request.fileLinks() == null
                        ? List.of()
                        : request.fileLinks()
                        .stream()
                        .map(FileRequestDTO::fileLinks)
                        .toList();

        List<AdditionalLinksWrapper> additionalLinks =
                request.additionalLinks() == null
                        ? List.of()
                        : request.additionalLinks()
                        .stream()
                        .map(AdditionalLinksWrapper::from)
                        .toList();

        return new Project(
                company,
                null,
                request.brandName(),
                request.managerName(),
                request.managerEmail(),
                request.projectImages(),
                request.projectName(),
                request.projectExplanation(),
                request.industry(),
                request.projectType(),
                resultForms,
                request.recruitDeadline(),
                request.projectStartDate(),
                request.projectDeadline(),
                request.submitDeadline(),
                request.subsidy(),
                request.incentive(),
                request.incentiveCondition(),
                request.crewType(),
                request.peopleNumber(),
                request.competency(),
                request.preferenceCondition(),
                fileLinks,
                additionalLinks,
                status,
                0
        );
    }

    public static Project createRecruitingProject(
            Company company,
            CompanyProjectRequestDTO request
    ) {
        return createWithStatus(
                company,
                request,
                ProjectStatus.RECRUITING
        );
    }

    public static Project createDraft(
            Company company,
            CompanyProjectRequestDTO request
    ) {
        return createWithStatus(
                company,
                request,
                ProjectStatus.DRAFT
        );
    }

    private void modify(
            CompanyProjectRequestDTO request
    ) {
        this.brandName =
                getOrDefault(
                        request.brandName(),
                        this.brandName
                );

        this.managerName =
                getOrDefault(
                        request.managerName(),
                        this.managerName
                );

        this.managerEmail =
                getOrDefault(
                        request.managerEmail(),
                        this.managerEmail
                );

        this.projectImage =
                copyList(
                        getOrDefault(
                                request.projectImages(),
                                this.projectImage
                        )
                );

        this.projectName =
                getOrDefault(
                        request.projectName(),
                        this.projectName
                );

        this.projectExplanation =
                getOrDefault(
                        request.projectExplanation(),
                        this.projectExplanation
                );

        this.industry =
                getOrDefault(
                        request.industry(),
                        this.industry
                );

        this.projectType =
                getOrDefault(
                        request.projectType(),
                        this.projectType
                );

        if (request.resultForm() != null) {
            this.resultForm =
                    request.resultForm()
                            .stream()
                            .map(ResultForm::from)
                            .toList();
        }

        this.recruitDeadLine =
                getOrDefault(
                        request.recruitDeadline(),
                        this.recruitDeadLine
                );

        this.projectStartDate =
                getOrDefault(
                        request.projectStartDate(),
                        this.projectStartDate
                );

        this.projectDeadline =
                getOrDefault(
                        request.projectDeadline(),
                        this.projectDeadline
                );

        this.submitDeadline =
                getOrDefault(
                        request.submitDeadline(),
                        this.submitDeadline
                );

        this.subsidy =
                getOrDefault(
                        request.subsidy(),
                        this.subsidy
                );

        this.incentive =
                getOrDefault(
                        request.incentive(),
                        this.incentive
                );

        this.incentiveCondition =
                getOrDefault(
                        request.incentiveCondition(),
                        this.incentiveCondition
                );

        this.crewType =
                getOrDefault(
                        request.crewType(),
                        this.crewType
                );

        this.peopleNumber =
                getOrDefault(
                        request.peopleNumber(),
                        this.peopleNumber
                );

        this.competency =
                getOrDefault(
                        request.competency(),
                        this.competency
                );

        this.preferenceCondition =
                getOrDefault(
                        request.preferenceCondition(),
                        this.preferenceCondition
                );

        if (request.fileLinks() != null) {
            this.fileLinks =
                    request.fileLinks()
                            .stream()
                            .map(FileRequestDTO::fileLinks)
                            .toList();
        }

        if (request.additionalLinks() != null) {
            this.links =
                    request.additionalLinks()
                            .stream()
                            .map(AdditionalLinksWrapper::from)
                            .toList();
        }
    }

    public void modifyDraft(
            CompanyProjectRequestDTO request
    ) {
        modify(request);
    }

    public void modifyProject(
            CompanyProjectRequestDTO request
    ) {
        modify(request);
    }

    public String getCompanyName() {
        return company == null
                ? null
                : company.getCompanyName();
    }

    public String getCrewName() {
        return selectedCrew == null
                ? null
                : selectedCrew.getCrewName();
    }

    public void selectCrew(
            Crew crew
    ) {
        this.selectedCrew = crew;
        this.status =
                ProjectStatus.CONTRACT_PENDING;
        this.crewSelectedDate =
                LocalDate.now();
    }

    public void completeContract() {
        this.status =
                ProjectStatus.PROGRESS;
    }

    public void afterProjectDeadline() {
        this.status =
                ProjectStatus.WAITING_RESULT;
    }

    /*
     * 결과물 제출 완료 상태 전이.
     * 수정 요청 및 승인 API는 제거하지만, 제출 완료 시각과 기존 상태 집계를 위해 유지한다.
     */
    public void submitProjectResult() {
        this.status =
                ProjectStatus.INSPECTION;
        this.resultSubmittedDate =
                LocalDate.now();
    }

    public void completeInspection() {
        if (status != ProjectStatus.INSPECTION) {
            throw new CustomException(
                    ErrorCode.INVALID_PROJECT_STATUS
            );
        }

        this.status = ProjectStatus.ADJUSTING;
    }

    public void end() {
        this.projectEndedDate =
                LocalDate.now();
        this.status =
                ProjectStatus.DONE;
    }

    /*
     * feature/2 정산 완료 호출부 호환용.
     */
    public void completeSettlement() {
        end();
    }

    public void expire() {
        this.status =
                ProjectStatus.EXPIRED;
        this.projectEndedDate =
                LocalDate.now();
    }

    public void increaseViews() {
        this.views++;
    }

    public boolean isDone() {
        return status
                == ProjectStatus.DONE;
    }

    public boolean isWaitingResult() {
        return status
                == ProjectStatus.WAITING_RESULT
                || status
                == ProjectStatus.PROGRESS;
    }

    public boolean isBeforeSigningContract() {
        return status
                == ProjectStatus.CONTRACT_PENDING;
    }

    public boolean isInProgress() {
        return status
                == ProjectStatus.PROGRESS;
    }

    public boolean isAfterProgress() {
        return status
                == ProjectStatus.PROGRESS
                || status
                == ProjectStatus.WAITING_RESULT
                || status
                == ProjectStatus.INSPECTION
                || status
                == ProjectStatus.ADJUSTING
                || status
                == ProjectStatus.DONE;
    }

    private static <T> List<T> copyList(
            List<T> values
    ) {
        if (values == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(values);
    }
}
