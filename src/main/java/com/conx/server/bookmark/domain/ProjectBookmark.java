package com.conx.server.bookmark.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProjectBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}