package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.enums.ProjectSubmissionStatus;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.dto.crew.request.SubmitProjectResultRequestDTO;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isRevised;

    /**
     * 한 프로젝트에 여러 제출물이 존재할 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    /**
     * 제출 당시 작성 크루
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_crew_id")
    private Crew authorCrew;

    private String title;

    @Lob
    private String content;

    /**
     * 제출 파일 URL 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "project_submission_file_link",
            joinColumns = @JoinColumn(
                    name = "project_submission_id"
            )
    )
    @Column(name = "file_link")
    private List<String> fileLinks =
            new ArrayList<>();

    /**
     * 외부 참고 링크 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "project_submission_reference_link",
            joinColumns = @JoinColumn(
                    name = "project_submission_id"
            )
    )
    @Column(name = "reference_link")
    private List<String> referenceLinks =
            new ArrayList<>();

    private String revisionReason;

    /**
     * DRAFT만 수정 가능하다.
     * 제출 완료된 결과물은 수정할 수 없다.
     */
    private boolean editable;

    @Enumerated(EnumType.STRING)
    private ProjectSubmissionStatus status;

    private ProjectSubmission(
            Project project,
            Crew authorCrew,
            String title,
            String content,
            List<String> fileLinks,
            List<String> referenceLinks
    ) {
        this.project = project;
        this.authorCrew = authorCrew;
        this.title = title;
        this.content = content;
        this.fileLinks =
                copyList(fileLinks);
        this.referenceLinks =
                copyList(referenceLinks);
        this.isRevised = false;
    }

    /**
     * 실제 결과물 제출 시각
     *
     * 임시 저장 생성 시각과 결과물 제출 시각을
     * 구분하기 위해 별도로 저장한다.
     */
    private LocalDateTime submittedAt;

    /**
     * 제출 완료 결과물 생성
     */
    public static ProjectSubmission create(
            Project project,
            Crew authorCrew,
            String title,
            String content,
            List<String> fileLinks,
            List<String> referenceLinks
    ) {
        ProjectSubmission submission =
                new ProjectSubmission(
                        project,
                        authorCrew,
                        title,
                        content,
                        fileLinks,
                        referenceLinks
                );

        submission.activateSubmission();

        return submission;
    }

    /**
     * 임시 저장 결과물 생성
     */
    public static ProjectSubmission createDraft(
            Project project,
            Crew authorCrew,
            String title,
            String content,
            List<String> fileLinks,
            List<String> referenceLinks
    ) {
        ProjectSubmission submission =
                new ProjectSubmission(
                        project,
                        authorCrew,
                        title,
                        content,
                        fileLinks,
                        referenceLinks
                );

        submission.draftSubmission();

        return submission;
    }

    /**
     * 기존 코드 호환용
     */
    public static ProjectSubmission create(
            Project project,
            String content,
            List<String> fileLinks
    ) {
        return create(
                project,
                project.getSelectedCrew(),
                "결과물 제출",
                content,
                fileLinks,
                List.of()
        );
    }

    /**
     * 기존 코드 호환용
     */
    public static ProjectSubmission createDraft(
            Project project,
            String content,
            List<String> fileLinks
    ) {
        return createDraft(
                project,
                project.getSelectedCrew(),
                "결과물 임시 저장",
                content,
                fileLinks,
                List.of()
        );
    }

    /**
     * 임시 저장 상태의 제출물만 수정 가능
     */
    public void update(
            SubmitProjectResultRequestDTO request
    ) {
        if (!isDraft()) {
            throw new CustomException(
                    ErrorCode.INVALID_SUBMISSION_STATUS
            );
        }

        this.title = request.title();
        this.content = request.content();
        this.fileLinks =
                copyList(request.fileLinks());
        this.referenceLinks =
                copyList(request.referenceLinks());
    }

    public Crew getCrew() {
        if (authorCrew != null) {
            return authorCrew;
        }

        return project.getSelectedCrew();
    }

    /**
     * 임시 저장 또는 신규 제출물을 제출 완료 상태로 변경한다.
     */
    public void activateSubmission() {
        this.status =
                ProjectSubmissionStatus.SUBMITTED;

        this.revisionReason = null;
        this.editable = false;

        this.submittedAt =
                LocalDateTime.now(
                        ZoneId.of("Asia/Seoul")
                );
    }

    /**
     * 기존 데이터에는 submittedAt이 없을 수 있으므로
     * 생성 시각을 대신 반환한다.
     */
    public LocalDateTime getResolvedSubmittedAt() {
        return submittedAt == null
                ? getCreatedAt()
                : submittedAt;
    }

    public void draftSubmission() {
        this.status =
                ProjectSubmissionStatus.DRAFT;

        this.editable = true;
    }

    public void requestRevision(
            String revisionReason
    ) {
        if (!isSubmitted()) {
            throw new CustomException(
                    ErrorCode.INVALID_SUBMISSION_STATUS
            );
        }

        this.revisionReason =
                revisionReason;

        this.status =
                ProjectSubmissionStatus.REVISION_REQUESTED;

        this.isRevised = true;
        this.editable = false;
    }

    public void approve() {
        if (!isSubmitted()) {
            throw new CustomException(
                    ErrorCode.INVALID_SUBMISSION_STATUS
            );
        }

        this.status =
                ProjectSubmissionStatus.APPROVED;

        this.editable = false;
    }

    public boolean isSubmitted() {
        return this.status
                == ProjectSubmissionStatus.SUBMITTED;
    }

    public boolean isDraft() {
        return this.status
                == ProjectSubmissionStatus.DRAFT;
    }

    private static List<String> copyList(
            List<String> values
    ) {
        if (values == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(values);
    }
}