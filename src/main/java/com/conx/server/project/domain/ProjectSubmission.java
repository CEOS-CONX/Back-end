package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.user.domain.crew.Crew;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    private String content;

    @ElementCollection
    @CollectionTable(
            name = "project_submission_file_link",
            joinColumns = @JoinColumn(name = "project_submission_id")
    )
    @Column(name = "file_link")
    private List<String> fileLinks;

    private String revisionReason;

    @Enumerated(EnumType.STRING)
    private ProjectSubmissionStatus status;

    private ProjectSubmission(
            Project project,
            Crew crew,
            String content,
            List<String> fileLinks
    ) {
        this.project = project;
        this.crew = crew;
        this.content = content;
        this.fileLinks = fileLinks;
        this.status = ProjectSubmissionStatus.SUBMITTED;
    }

    public static ProjectSubmission create(
            Project project,
            Crew crew,
            String content,
            List<String> fileLinks
    ) {
        return new ProjectSubmission(project, crew, content, fileLinks);
    }

    public void requestRevision(String revisionReason) {
        this.revisionReason = revisionReason;
        this.status = ProjectSubmissionStatus.REVISION_REQUESTED;
    }

    public void approve() {
        this.status = ProjectSubmissionStatus.APPROVED;
    }

    public boolean isSubmitted() {
        return this.status == ProjectSubmissionStatus.SUBMITTED;
    }
}