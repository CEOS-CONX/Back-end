package com.conx.server.user.domain.crew;

import com.conx.server.global.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Evaluation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private float mean;

    private float completeness;

    private float schedule;

    private float ability;

    private float recooperation;

    private float communication;
}
