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
            String content,
            List<String> fileLinks
    ) {
        this.project = project;
        this.content = content;
        this.fileLinks = fileLinks;
    }

    public static ProjectSubmission create(
            Project project,
            String content,
            List<String> fileLinks
    ) {
        ProjectSubmission submission = new ProjectSubmission(project, content, fileLinks);
        submission.activateSubmission();
        return submission;
    }

    public static ProjectSubmission createDraft(
            Project project,
            String content,
            List<String> fileLinks
    ) {
        ProjectSubmission submission = new ProjectSubmission(project, content, fileLinks);
        submission.draftSubmission();
        return submission;
    }

    public void update(SubmitProjectResultRequestDTO req){
        this.content = req.content();
        this.fileLinks = req.fileLinks();
    }

    public Crew getCrew(){
        return project.getSelectedCrew();
    }

    public void activateSubmission() {
        this.status = ProjectSubmissionStatus.SUBMITTED;
        this.revisionReason = null;
    }

    public void draftSubmission() {
        this.status = ProjectSubmissionStatus.DRAFT;
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

    public boolean isEditable() {
        return status == ProjectSubmissionStatus.DRAFT ||
                status == ProjectSubmissionStatus.REVISION_REQUESTED;
    }
}