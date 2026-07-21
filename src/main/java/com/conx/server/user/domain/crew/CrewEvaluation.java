package com.conx.server.user.domain.crew;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewEvaluation {
    private CrewEvaluation(Crew crew){
        this.crew = crew;
        this.completeness = 0;
        this.schedule = 0;
        this.ability = 0;
        this.reCooperation = 0;
        this.communication = 0;
        this.totalPoint = 0;
        this.totalEvaluationCount = 0;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "crew_id",
            nullable = false
    )
    private Crew crew;

    private int completeness;

    private int schedule;

    private int ability;

    private int reCooperation;

    private int communication;

    private int totalPoint;

    private int totalEvaluationCount;

    public static CrewEvaluation create(Crew crew){
        return new CrewEvaluation(crew);
    }

    public void addEvaluation(Evaluation e){
        completeness += e.getCompleteness();
        schedule += e.getSchedule();
        ability += e.getAbility();
        reCooperation += e.getReCooperation();
        communication += e.getCommunication();
        totalEvaluationCount++;

        setTotalPoint();
    }

    private void setTotalPoint(){
        this.totalPoint = completeness + schedule + ability + reCooperation + communication;
    }

    public double getMeanPoint(){
        return (double) totalPoint / totalEvaluationCount;
    }
}