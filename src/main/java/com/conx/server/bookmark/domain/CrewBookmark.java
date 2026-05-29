package com.conx.server.bookmark.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CrewBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;
}
