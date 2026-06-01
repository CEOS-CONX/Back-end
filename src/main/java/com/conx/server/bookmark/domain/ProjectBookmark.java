package com.conx.server.bookmark.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.crew.Crew;
import jakarta.persistence.Entity;
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
public class ProjectBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private ProjectBookmark(Crew crew, Project project) {
        this.crew = crew;
        this.project = project;
    }

    public static ProjectBookmark create(Crew crew, Project project) {
        return new ProjectBookmark(crew, project);
    }
}