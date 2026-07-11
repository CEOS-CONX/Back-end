package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;
import com.conx.server.user.domain.crew.Crew;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private String introduction;

    private String proposal;

    @Enumerated(EnumType.STRING)
    private ProjectApplicationStatus status;

    private ProjectApplication(Project project, Crew crew, String introduction, String proposal) {
        this.project = project;
        this.crew = crew;
        this.introduction = introduction;
        this.proposal = proposal;
        this.status = ProjectApplicationStatus.PENDING;
    }

    public static ProjectApplication create(
            Project project,
            Crew crew,
            String introduction,
            String proposal
    ) {
        return new ProjectApplication(project, crew, introduction, proposal);
    }

    public void select() {
        this.status = ProjectApplicationStatus.SELECTED;
    }

    public void reject() {
        this.status = ProjectApplicationStatus.REJECTED;
    }

    public boolean isPending() {
        return this.status == ProjectApplicationStatus.PENDING;
    }

    public String getCrewName() {
        return crew.getCrewName();
    }

    public String getCompanyName() {
        return project.getCompanyName();
    }
}