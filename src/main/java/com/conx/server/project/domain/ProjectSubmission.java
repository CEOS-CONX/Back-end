package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import jakarta.persistence.*;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String subject;

    private String writer;

    private String content;

    @ElementCollection
    @CollectionTable(
            name = "project_submission_file_link",
            joinColumns = @JoinColumn(name = "project_submission_id")
    )
    @Column(name = "file_link")
    private List<String> fileLinks;

    @ElementCollection
    @CollectionTable(
            name = "project_links",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<AdditionalLinksWrapper> additionalLinks;

    @Enumerated(EnumType.STRING)
    private ProjectSubmissionStatus status;

    private ProjectSubmission(
            Project project,
            String subject,
            String content,
            List<String> fileLinks,
            List<AdditionalLinksWrapper> additionalLinks
    ) {
        this.project = project;
        this.subject = subject;
        this.content = content;
        this.writer = project.getCrewName();
        this.fileLinks = fileLinks;
        this.additionalLinks = additionalLinks;
        this.status = ProjectSubmissionStatus.SUBMITTED;
    }

    public static ProjectSubmission create(
            Project project,
            String subject,
            String content,
            List<String> fileLinks,
            List<AdditionalLinksWrapper> additionalLinks
    ) {
        ProjectSubmission submission = new ProjectSubmission(project, subject, content, fileLinks, additionalLinks);
        return submission;
    }

    public Crew getCrew(){
        return project.getSelectedCrew();
    }

    public void setFeedback(){
        this.status = ProjectSubmissionStatus.FEEDBACKED;
    }

    public boolean isEditable(){
        return this.status == ProjectSubmissionStatus.SUBMITTED;
    }

}