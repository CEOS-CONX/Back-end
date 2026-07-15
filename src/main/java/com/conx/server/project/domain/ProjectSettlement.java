package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.enums.ProjectSettlementStatus;
import com.conx.server.user.domain.company.Company;
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

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSettlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private long amount;

    private LocalDate expectedPaymentDate;

    private LocalDate settlementDate;

    @Enumerated(EnumType.STRING)
    private ProjectSettlementStatus status;

    private ProjectSettlement(
            Project project,
            Company company,
            Crew crew,
            long amount
    ) {
        this.project = project;
        this.company = company;
        this.crew = crew;
        this.amount = amount;
        this.status = ProjectSettlementStatus.WAITING;
    }

    public static ProjectSettlement create(Project project) {
        return new ProjectSettlement(
                project,
                project.getCompany(),
                project.getSelectedCrew(),
                project.getSubsidy()
        );
    }

    public void updateExpectedPaymentDate(LocalDate expectedPaymentDate) {
        this.expectedPaymentDate = expectedPaymentDate;
    }

    public void markAsPaid(LocalDate settlementDate) {
        if (this.status == ProjectSettlementStatus.PAID) {
            throw new CustomException(
                    ErrorCode.SETTLEMENT_ALREADY_PAID
            );
        }

        if (settlementDate == null) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        this.status = ProjectSettlementStatus.PAID;
        this.settlementDate = settlementDate;
    }
}