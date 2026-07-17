package com.conx.server.user.domain.crew;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.dto.company.request.CompanyProjectEvaluationRequest;
import com.conx.server.user.dto.company.response.CompanyProjectEvaluationResponse;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "crew_id",
            nullable = false
    )
    private Crew crew;

    @Column(nullable = false)
    private int completeness;
    //double로 관리하면 소수점 때문에 오차가 발생해서 정수로 관리합니다. 절대 건드리지 마세요!

    @Column(nullable = false)
    private int schedule;
    //double로 관리하면 소수점 때문에 오차가 발생해서 정수로 관리합니다. 절대 건드리지 마세요!

    @Column(nullable = false)
    private int ability;
    //double로 관리하면 소수점 때문에 오차가 발생해서 정수로 관리합니다. 절대 건드리지 마세요!

    @Column(nullable = false)
    private int reCooperation;
    //double로 관리하면 소수점 때문에 오차가 발생해서 정수로 관리합니다. 절대 건드리지 마세요!

    @Column(nullable = false)
    private int communication;
    //double로 관리하면 소수점 때문에 오차가 발생해서 정수로 관리합니다. 절대 건드리지 마세요!

    private int totalEvaluation;
    //double로 관리하면 소수점 때문에 오차가 발생해서 정수로 관리합니다. 절대 건드리지 마세요!

    @Column(nullable = false)
    private int total;
    //double로 관리하면 소수점 때문에 오차가 발생해서 정수로 관리합니다. 절대 건드리지 마세요!

    private Evaluation(Crew crew) {
        this.crew = crew;
        this.total = 0;
        this.totalEvaluation = 0;
    }

    public static Evaluation create(Crew crew) {
        return new Evaluation(crew);
    }

    private void calculateTotal(){
        total = completeness + schedule + ability + reCooperation + communication;
    }

    public void evaluate(CompanyProjectEvaluationRequest req){
        this.completeness += req.completeness();
        this.schedule += req.schedule();
        this.ability += req.ability();
        this.reCooperation += req.reCooperation();
        this.communication += req.communication();

        totalEvaluation++;
        calculateTotal();
    }

    public double getOverall() {
        if (totalEvaluation == 0) {
            return 0;
        }

        return (double) total / (5 * totalEvaluation);
    }

    public record CrewEvaluationWrapperDTO(
            double overall,
            double completeness,
            double ability,
            double communication,
            double schedule,
            double reCooperation
    ) {
    }

    public CrewEvaluationWrapperDTO getWrapperDTO(){}
}
