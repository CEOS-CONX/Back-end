package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne
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

    private String referenceLink;

    private ProjectStatus status;

}