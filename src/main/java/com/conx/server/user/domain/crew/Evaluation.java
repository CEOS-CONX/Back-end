package com.conx.server.user.domain.crew;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.company.Company;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "project_evaluation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_evaluation_project",
                        columnNames = "project_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * 프로젝트 하나당 평가 하나만 존재합니다.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            unique = true
    )
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "crew_id",
            nullable = false
    )
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "company_id",
            nullable = false
    )
    private Company company;

    @Column(nullable = false)
    private int completeness;

    @Column(nullable = false)
    private int schedule;

    @Column(nullable = false)
    private int ability;

    @Column(nullable = false)
    private int reCooperation;

    @Column(nullable = false)
    private int communication;

    @Column(nullable = false)
    private double mean;

    private Evaluation(
            Project project,
            Crew crew,
            Company company,
            int completeness,
            int schedule,
            int ability,
            int reCooperation,
            int communication
    ) {
        this.project = project;
        this.crew = crew;
        this.company = company;
        this.completeness = completeness;
        this.schedule = schedule;
        this.ability = ability;
        this.reCooperation = reCooperation;
        this.communication = communication;
        this.mean = calculateMean(
                completeness,
                schedule,
                ability,
                reCooperation,
                communication
        );
    }

    public static Evaluation create(
            Project project,
            Crew crew,
            Company company,
            int completeness,
            int schedule,
            int ability,
            int reCooperation,
            int communication
    ) {
        return new Evaluation(
                project,
                crew,
                company,
                completeness,
                schedule,
                ability,
                reCooperation,
                communication
        );
    }

    private static double calculateMean(
            int completeness,
            int schedule,
            int ability,
            int reCooperation,
            int communication
    ) {
        return (
                completeness
                        + schedule
                        + ability
                        + reCooperation
                        + communication
        ) / 5.0;
    }
}