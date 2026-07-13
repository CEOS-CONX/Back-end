package com.conx.server.project.domain;

import com.conx.server.domain.file.domain.File;
import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
        this.projectImage = projectImage;
        this.projectName = projectName;
        this.projectExplanation = projectExplanation;
        this.industry = industry;
        this.projectType = projectType;
        this.resultForm = resultForm;
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
        this.fileLinks = fileLinks;
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

    //브랜드 정보
    //브랜드 이름
    private String brandName;

    //담당자명
    private String managerName;

    //이메일
    private String managerEmail;


    //프로젝트 설명
    //프로젝트 이미지(5개)
    private List<String> projectImage;

    //프로젝트명
    private String projectName;

    //프로젝트 소개
    private String projectExplanation;

    //산업 분야
    private Industry industry;

    //프로젝트 유형
    private ProjectType projectType;

    //결과물
    @ElementCollection
    @CollectionTable(
            name = "project_result_form",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<ResultForm> resultForm;

    //일정
    //크루 모집 마감일
    private LocalDate recruitDeadLine;

    //프로젝트 시작일
    private LocalDate projectStartDate;

    //프로젝트 마감일
    private LocalDate projectDeadline;

    //결과물 제출일
    private LocalDate submitDeadline;

    //지원금
    private long subsidy;

    //인센티브 지급여부
    private boolean incentive;

    //인센티브 지급조건
    private String incentiveCondition;

    //모집 크루 조건
    //크루유형
    private CrewType crewType;

    //참여 인원수
    private int peopleNumber;

    //필수 역량
    private String competency;

    //우대 조건
    private String preferenceCondition;


    //참고자료
    private List<String> fileLinks;

    //링크
    @ElementCollection
    @CollectionTable(
            name = "project_links",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<AdditionalLinksWrapper> links;

    private ProjectStatus status;

    private int views;

    public static Project createWithStatus(
            Company company,
            CompanyProjectRequestDTO request,
            ProjectStatus status
    ) {
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
                request.resultForm().stream().map(ResultForm::from).toList(),
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
                request.fileLinks(),
                request.additionalLinks() == null ? null : request.additionalLinks().stream().map(AdditionalLinksWrapper::from).toList(),
                status,
                0                         // 조회수 초기값
        );
    }

    public static Project createRecruitingProject(
            Company company,
            CompanyProjectRequestDTO request
    ) {
        return createWithStatus(company, request, ProjectStatus.RECRUITING);
    }

    public static Project createDraft(
            Company company,
            CompanyProjectRequestDTO request
    ) {
        return createWithStatus(company, request, ProjectStatus.DRAFT);
    }

    private void modify(CompanyProjectRequestDTO request) {
        this.brandName = getOrDefault(request.brandName(), this.getBrandName());
        this.managerName = getOrDefault(request.managerName(), this.getManagerName());
        this.managerEmail = getOrDefault(request.managerEmail(), this.getManagerEmail());

        this.projectImage = getOrDefault(request.projectImages(), this.getProjectImage());
        this.projectName = getOrDefault(request.projectName(), this.getProjectName());
        this.projectExplanation = getOrDefault(request.projectExplanation(), this.getProjectExplanation());
        this.industry = getOrDefault(request.industry(), this.getIndustry());
        this.projectType = getOrDefault(request.projectType(), this.getProjectType());
        this.resultForm = getOrDefault(request.resultForm().stream().map(ResultForm::from).toList(), this.getResultForm());

        this.recruitDeadLine = getOrDefault(request.recruitDeadline(), this.getRecruitDeadLine());
        this.projectStartDate = getOrDefault(request.projectStartDate(), this.getProjectStartDate());
        this.projectDeadline = getOrDefault(request.projectDeadline(), this.getProjectDeadline());
        this.submitDeadline = getOrDefault(request.submitDeadline(), this.getSubmitDeadline());

        this.subsidy = getOrDefault(request.subsidy(), this.getSubsidy());
        this.incentive = getOrDefault(request.incentive(), this.isIncentive());
        this.incentiveCondition = getOrDefault(request.incentiveCondition(), this.getIncentiveCondition());

        this.crewType = getOrDefault(request.crewType(), this.getCrewType());
        this.peopleNumber = getOrDefault(request.peopleNumber(), this.getPeopleNumber());
        this.competency = getOrDefault(request.competency(), this.getCompetency());
        this.preferenceCondition = getOrDefault(request.preferenceCondition(), this.getPreferenceCondition());

        this.fileLinks = getOrDefault(request.fileLinks(), this.getFileLinks());

        this.links = getOrDefault(request.additionalLinks().stream().map(AdditionalLinksWrapper::from).toList(), this.links);
    }

    public String getCompanyName(){
        return company.getCompanyName();
    }

    public String getCrewName(){
        return selectedCrew.getCrewName();
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

    public void selectCrew(Crew crew) {
        this.selectedCrew = crew;
        this.status = ProjectStatus.CONTRACT_PENDING;
    }

    public boolean isDone(){
        return this.status == ProjectStatus.DONE;
    }

    public boolean isWaitingResult(){
        return this.status == ProjectStatus.WAITING_RESULT
                || this.status == ProjectStatus.PROGRESS;
    }

    public boolean isBeforeSigningContract(){
        return this.status == ProjectStatus.CONTRACT_PENDING;
    }

    public void submitProjectResult() {
        this.status = ProjectStatus.INSPECTION;
    }

    public void requestRevision() {
        this.status = ProjectStatus.WAITING_RESULT;
    }

    public void afterProjectDeadline() {
        this.status = ProjectStatus.WAITING_RESULT;
    }

    public void approveResult() {
        this.status = ProjectStatus.ADJUSTING;
    }

    public void increaseViews() {
        this.views++;
    }

    public void expire(){
        this.status = ProjectStatus.EXPIRED;
    }

    public void completeContract() {
        this.status = ProjectStatus.PROGRESS;
    }
}