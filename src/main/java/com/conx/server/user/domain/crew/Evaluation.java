package com.conx.server.user.domain.crew;

import com.conx.server.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation extends BaseEntity {
    private Evaluation(Crew crew){
        this.crew = crew;
        this.mean = 0;
        this.completeness = 0;
        this.schedule = 0;
        this.ability = 0;
        this.recooperation = 0;
        this.communication = 0;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private double mean;

    private double completeness;

    private double schedule;

    private double ability;

    private double recooperation;

    private double communication;

    private double calculateMean(){
        return (completeness + schedule + ability + recooperation + communication) / 5;
    }

    public static Evaluation create(Crew crew){
        return new Evaluation(crew);
    }

    public void addPoint(int completeness, int schedule, int ability, int recooperation, int communication){
        this.completeness += completeness;
        this.schedule += schedule;
        this.ability += ability;
        this.recooperation += recooperation;
        this.communication += communication;
        this.mean = calculateMean();
    }
}
