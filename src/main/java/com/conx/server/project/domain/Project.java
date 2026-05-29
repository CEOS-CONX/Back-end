package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
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

    public static Project createRecruitingProject(
            Company company,
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
            String referenceLink
    ) {
        return new Project(
                company,
                projectImage,
                brandName,
                managerName,
                managerEmail,
                managerPhone,
                name,
                objectives,
                projectType,
                requirement,
                projectExplanation,
                resultForm,
                essentialSubmitPart,
                recruitDeadLine,
                projectStartDate,
                projectDeadline,
                submitDeadline,
                crewType,
                competency,
                preferenceCondition,
                subsidy,
                incentive,
                incentiveCondition,
                additionalFileLinks,
                referenceLink,
                ProjectStatus.RECRUITING
        );
    }

    public static Project createDraft(
            Company company,
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
            String referenceLink
    ) {
        return new Project(
                company,
                projectImage,
                brandName,
                managerName,
                managerEmail,
                managerPhone,
                name,
                objectives,
                projectType,
                requirement,
                projectExplanation,
                resultForm,
                essentialSubmitPart,
                recruitDeadLine,
                projectStartDate,
                projectDeadline,
                submitDeadline,
                crewType,
                competency,
                preferenceCondition,
                subsidy,
                incentive,
                incentiveCondition,
                additionalFileLinks,
                referenceLink,
                ProjectStatus.DRAFT
        );
    }

    public void modifyDraft(
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
            String referenceLink
    ) {
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
    }

    public void modifyProject(
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
            String referenceLink
    ) {
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
    }

    public void selectCrew(Crew crew) {
        this.selectedCrew = crew;
        this.status = ProjectStatus.PROGRESS;
    }

    public void requestRevision() {
        this.status = ProjectStatus.WAITING_RESULT;
    }

    public void approveResult() {
        this.status = ProjectStatus.ADJUSTING;
    }
}