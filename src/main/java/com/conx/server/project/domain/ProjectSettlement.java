package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.enums.CrewPaymentStatus;
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

    /**
     * 프로젝트 지원금 및 정산 금액
     */
    private long subsidy;

    /**
     * 기업이 입력한 정산 예정일
     */
    private LocalDate expectedPaymentDate;

    /**
     * 실제 CONX 정산 완료일
     */
    private LocalDate paymentDate;

    /**
     * 실제 CONX 정산 상태
     */
    @Enumerated(EnumType.STRING)
    private ProjectSettlementStatus status;

    /**
     * 크루가 직접 선택하는 지급 확인 상태
     */
    @Enumerated(EnumType.STRING)
    private CrewPaymentStatus crewPaymentStatus;

    /**
     * 크루가 지급 완료로 확인한 날짜
     */
    private LocalDate crewPaymentConfirmedDate;

    private ProjectSettlement(
            Project project,
            Company company,
            Crew crew,
            long subsidy
    ) {
        this.project = project;
        this.company = company;
        this.crew = crew;
        this.subsidy = subsidy;
        this.status = ProjectSettlementStatus.WAITING;
        this.crewPaymentStatus =
                CrewPaymentStatus.BEFORE_PAYMENT;
    }

    public static ProjectSettlement create(
            Project project
    ) {
        return new ProjectSettlement(
                project,
                project.getCompany(),
                project.getSelectedCrew(),
                project.getSubsidy()
        );
    }

    public void updateExpectedPaymentDate(
            LocalDate expectedPaymentDate
    ) {
        this.expectedPaymentDate =
                expectedPaymentDate;
    }

    /**
     * 실제 정산 완료 처리
     */
    public void markAsPaid(
            LocalDate paymentDate
    ) {
        if (isPaid()) {
            throw new CustomException(
                    ErrorCode.SETTLEMENT_ALREADY_PAID
            );
        }

        if (paymentDate == null) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        this.status =
                ProjectSettlementStatus.PAID;

        this.paymentDate =
                paymentDate;
    }

    /**
     * dev 코드 호환용
     */
    public void markAsPaid() {
        markAsPaid(
                LocalDate.now()
        );
    }

    /**
     * 크루 지급 확인 상태 변경
     *
     * 실제 정산 상태 및 실제 정산일과는
     * 독립적으로 변경한다.
     */
    public void changeCrewPaymentStatus(
            CrewPaymentStatus paymentStatus,
            LocalDate confirmedDate
    ) {
        if (paymentStatus == null) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        this.crewPaymentStatus =
                paymentStatus;

        if (
                paymentStatus
                        == CrewPaymentStatus.PAYMENT_CONFIRMED
        ) {
            if (confirmedDate == null) {
                throw new CustomException(
                        ErrorCode.INVALID_INPUT_VALUE
                );
            }

            this.crewPaymentConfirmedDate =
                    confirmedDate;

            return;
        }

        this.crewPaymentConfirmedDate =
                null;
    }

    /**
     * 기존 데이터에 신규 컬럼 값이 없는 경우를 위한 응답용 상태
     */
    public CrewPaymentStatus getResolvedCrewPaymentStatus() {
        return crewPaymentStatus == null
                ? CrewPaymentStatus.BEFORE_PAYMENT
                : crewPaymentStatus;
    }

    public boolean isWaiting() {
        return status
                == ProjectSettlementStatus.WAITING;
    }

    public boolean isPaid() {
        return status
                == ProjectSettlementStatus.PAID;
    }

    /**
     * 실제 정산일이 이번 달인지 확인
     */
    public boolean isInThisMonth() {
        if (paymentDate == null) {
            return false;
        }

        LocalDate now =
                LocalDate.now();

        return now.getYear()
                == paymentDate.getYear()
                && now.getMonthValue()
                == paymentDate.getMonthValue();
    }

}
