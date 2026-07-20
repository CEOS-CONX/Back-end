package com.conx.server.project.domain;

import com.conx.server.domain.file.dto.FileRequestDTO;
import com.conx.server.global.BaseEntity;
import com.conx.server.user.dto.company.request.CompanyFeedbackRequestDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectInspectionFeedback extends BaseEntity {
    private ProjectInspectionFeedback(ProjectSubmission submission, String content,
                                      List<String> fileLinks, List<AdditionalLinksWrapper> links){
        this.submission = submission;
        this.content = content;
        this.fileLinks = fileLinks;
        this.additionalLinks = links;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "submission")
    private ProjectSubmission submission;

    private String content;

    @ElementCollection
    @CollectionTable(
            name = "project_feedback_file_links",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<String> fileLinks;

    @ElementCollection
    @CollectionTable(
            name = "project_feedback_additional_links",
            joinColumns = @JoinColumn(name = "project_id")
    )
    private List<AdditionalLinksWrapper> additionalLinks;

    public static ProjectInspectionFeedback create(ProjectSubmission submission, CompanyFeedbackRequestDTO req){
        return new ProjectInspectionFeedback(
                submission,
                req.content(),
                req.files().stream().map(FileRequestDTO::fileLinks).toList(),
                req.links().stream().map(AdditionalLinksWrapper::from).toList()
        );
    }
}
