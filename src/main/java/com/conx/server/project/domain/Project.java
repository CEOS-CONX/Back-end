package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.dto.company.request.CompanyProjectRequest;
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
import java.util.List;
import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    public Project(Company company,
                   String brandName, String managerName, String managerEmail, String managerPhone,
                   String name, String objectives, ProjectType type, String requirement, String resultForm, String essentialSubmitPart,
                   LocalDate recruitDeadLine, LocalDate projectStartDate, LocalDate projectDeadline, LocalDate submitDeadline,
                   CrewType crewType, String competency, String preferenceCondition, long subsidy,
                   boolean incentive, String incentiveCondition,
                   List<String> additionalFileLinks, String referenceLink) {
        this.company = company;
        this.brandName = brandName;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.managerPhone = managerPhone;
        this.name = name;
        this.objectives = objectives;
        this.projectType = type;
        this.requirement = requirement;
        this.resultForm = resultForm;
        this.essentialSubmitPart = essentialSubmitPart;
        this.recruitDeadLine = recruitDeadLine;
        this.projectStartDate = projectStartDate;
        this.projectDeadline = projectDeadline;
        this.submitDeadline = submitDeadline;
        this.crewType = crewType;
        this.competency = competency;
        this.preferenceCondition = preferenceCondition;
        this.subsidy = subsidy;
        this.incentive = incentive;
        this.incentiveCondition = incentiveCondition;
        this.additionalFileLinks = additionalFileLinks;
        this.referenceLink = referenceLink;
        this.status = ProjectStatus.RECRUITING;
    }

    private Project(Company company,
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
                    long subsidy,
                    boolean incentive,
                    String incentiveCondition,
                    List<String> additionalFileLinks,
                    String referenceLink,
                    ProjectStatus status) {
        this.company = company;
        this.projectImage = projectImage;
        this.brandName = brandName;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.managerPhone = managerPhone;
        this.name = name;
        this.objectives = objectives;
        this.projectType = projectType;
        this.requirement = requirement;
        this.projectExplanation = projectExplanation;
        this.resultForm = resultForm;
        this.essentialSubmitPart = essentialSubmitPart;
        this.recruitDeadLine = recruitDeadLine;
        this.projectStartDate = projectStartDate;
        this.projectDeadline = projectDeadline;
        this.submitDeadline = submitDeadline;
        this.crewType = crewType;
        this.competency = competency;
        this.preferenceCondition = preferenceCondition;
        this.subsidy = subsidy;
        this.incentive = incentive;
        this.incentiveCondition = incentiveCondition;
        this.additionalFileLinks = additionalFileLinks;
        this.referenceLink = referenceLink;
        this.status = status;
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

    private String managerName;

    private String managerEmail;

    private String managerPhone;

    private String projectImage;

    private String brandName;

    private String name;

    private String objectives;

    private ProjectType projectType;

    private String requirement;

    private String projectExplanation;

    private String resultForm;

    private String essentialSubmitPart;

    private LocalDate recruitDeadLine;

    private LocalDate projectStartDate;

    private LocalDate projectDeadline;

    private LocalDate submitDeadline;

    private CrewType crewType;

    private String competency;

    private String preferenceCondition;

    private long subsidy;

    private boolean incentive;

    private String incentiveCondition;

    private List<String> additionalFileLinks;

    private String referenceLink;

    private ProjectStatus status;

    private int views;

    private static Project createWithStatus(
            Company company,
            CompanyProjectRequest request,
            ProjectStatus status
    ){
        return new Project(
                company,
                request.projectImage(),
                request.brandName(),
                request.managerName(),
                request.managerEmail(),
                request.managerPhone(),
                request.name(),
                request.objectives(),
                request.projectType(),
                request.requirement(),
                request.projectExplanation(),
                request.resultForm(),
                request.essentialSubmitPart(),
                request.recruitDeadLine(),
                request.projectStartDate(),
                request.projectDeadline(),
                request.submitDeadline(),
                request.crewType(),
                request.competency(),
                request.preferenceCondition(),
                getOrDefault(request.subsidy(), 0L),
                getOrDefault(request.incentive(), false),
                request.incentiveCondition(),
                request.additionalFileLinks(),
                request.referenceLink(),
                status
        );
    }

    public static Project createRecruitingProject(
            Company company,
            CompanyProjectRequest request
    ) {
        return createWithStatus(company, request, ProjectStatus.RECRUITING);
    }

    public static Project createDraft(
            Company company,
            CompanyProjectRequest request
    ) {
        return createWithStatus(company, request, ProjectStatus.DRAFT);
    }



    private void modify(
            CompanyProjectRequest request
    )
    {
        this.projectImage = getOrDefault(request.projectImage(), this.getProjectImage());
        this.brandName = getOrDefault(request.brandName(), this.getBrandName());
        this.managerName = getOrDefault(request.managerName(), this.getManagerName());
        this.managerEmail = getOrDefault(request.managerEmail(), this.getManagerEmail());
        this.managerPhone = getOrDefault(request.managerPhone(), this.getManagerPhone());
        this.name = getOrDefault(request.name(), this.getName());
        this.objectives = getOrDefault(request.objectives(), this.getObjectives());
        this.projectType = getOrDefault(request.projectType(), this.getProjectType());
        this.requirement = getOrDefault(request.requirement(), this.getRequirement());
        this.projectExplanation = getOrDefault(request.projectExplanation(), this.getProjectExplanation());
        this.resultForm = getOrDefault(request.resultForm(), this.getResultForm());
        this.essentialSubmitPart = getOrDefault(request.essentialSubmitPart(), this.getEssentialSubmitPart());
        this.recruitDeadLine = getOrDefault(request.recruitDeadLine(), this.getRecruitDeadLine());
        this.projectStartDate = getOrDefault(request.projectStartDate(), this.getProjectStartDate());
        this.projectDeadline = getOrDefault(request.projectDeadline(), this.getProjectDeadline());
        this.submitDeadline = getOrDefault(request.submitDeadline(), this.getSubmitDeadline());
        this.crewType = getOrDefault(request.crewType(), this.getCrewType());
        this.competency = getOrDefault(request.competency(), this.getCompetency());
        this.preferenceCondition = getOrDefault(request.preferenceCondition(), this.getPreferenceCondition());
        this.subsidy = getOrDefault(request.subsidy(), this.getSubsidy());
        this.incentive = getOrDefault(request.incentive(), this.isIncentive());
        this.incentiveCondition = getOrDefault(request.incentiveCondition(), this.getIncentiveCondition());
        this.additionalFileLinks = getOrDefault(request.additionalFileLinks(), this.getAdditionalFileLinks());
        this.referenceLink = getOrDefault(request.referenceLink(), this.getReferenceLink());
    }

    public String getCompanyName(){
        return company.getCompanyName();
    }

    public String getCrewName(){
        return selectedCrew.getCrewName();
    }

    public void modifyDraft(
            CompanyProjectRequest request
    ) {
        modify(request);
    }

    public void modifyProject(
            CompanyProjectRequest request
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