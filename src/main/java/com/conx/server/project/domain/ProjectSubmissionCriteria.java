package com.conx.server.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
public class ProjectSubmissionCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private String finalResult;

    private int numberOfResult;

    private boolean done;

    protected ProjectSubmissionCriteria() {}

    private ProjectSubmissionCriteria(
            Project project,
            String finalResult,
            int numberOfResult
    ) {
        this.project = project;
        this.finalResult = finalResult;
        this.numberOfResult = numberOfResult;
        this.done = false;
    }

    public static ProjectSubmissionCriteria from(Project project, ResultForm resultForm) {
        return new ProjectSubmissionCriteria(
                project,
                resultForm.getFinalResult(),
                resultForm.getNumberOfResult()
        );
    }

    public void mark(){
        this.done = !done;
    }
}
