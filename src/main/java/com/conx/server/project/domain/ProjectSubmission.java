package com.conx.server.project.domain;

import com.conx.server.global.BaseEntity;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
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

    /**
     * 하나의 프로젝트에 여러 제출 이력이 존재할 수 있다.
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

    /**
     * 결과물 제목
     */
    private String subject;

    /**
     * 제출 당시 작성 크루명 스냅샷
     */
    private String writer;

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
     * 결과물 추가 링크 목록
     */
    @ElementCollection
    @CollectionTable(
            name = "project_submission_additional_link",
            joinColumns = @JoinColumn(
                    name = "project_submission_id"
            )
    )
    private List<AdditionalLinksWrapper> additionalLinks =
            new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProjectSubmissionStatus status;

    /**
     * 임시 저장 생성 시각과 실제 제출 시각을 구분한다.
     */
    private LocalDateTime submittedAt;

    private ProjectSubmission(
            Project project,
            Crew authorCrew,
            String subject,
            String content,
            List<String> fileLinks,
            List<AdditionalLinksWrapper> additionalLinks
    ) {
        this.project = project;
        this.authorCrew = authorCrew;
        this.subject = subject;
        this.writer = resolveWriter(
                project,
                authorCrew
        );
        this.content = content;
        this.fileLinks = copyList(fileLinks);
        this.additionalLinks =
                copyList(additionalLinks);
    }

    /**
     * 제출 완료 결과물 생성
     */
    public static ProjectSubmission create(
            Project project,
            Crew authorCrew,
            String subject,
            String content,
            List<String> fileLinks,
            List<AdditionalLinksWrapper> additionalLinks
    ) {
        ProjectSubmission submission =
                new ProjectSubmission(
                        project,
                        authorCrew,
                        subject,
                        content,
                        fileLinks,
                        additionalLinks
                );

        submission.activateSubmission();

        return submission;
    }

    public static ProjectSubmission create(
            Project project,
            String subject,
            String content,
            List<String> fileLinks,
            List<AdditionalLinksWrapper> additionalLinks
    ) {
        return create(
                project,
                project.getSelectedCrew(),
                subject,
                content,
                fileLinks,
                additionalLinks
        );
    }

    /**
     * DRAFT 또는 새 결과물을 제출 완료 상태로 변경한다.
     */
    public void activateSubmission() {
        if (status != null && !isDraft()) {
            throw new CustomException(
                    ErrorCode.INVALID_SUBMISSION_STATUS
            );
        }

        this.status =
                ProjectSubmissionStatus.SUBMITTED;

        this.submittedAt =
                LocalDateTime.now(
                        ZoneId.of("Asia/Seoul")
                );
    }

    public void draftSubmission() {
        this.status =
                ProjectSubmissionStatus.DRAFT;

        this.submittedAt = null;
    }

    /**
     * 기업이 결과물에 피드백을 등록한다.
     */
    public void setFeedback() {
        if (!isSubmitted()) {
            throw new CustomException(
                    ErrorCode.INVALID_SUBMISSION_STATUS
            );
        }

        this.status =
                ProjectSubmissionStatus.FEEDBACKED;
    }

    public Crew getCrew() {
        if (authorCrew != null) {
            return authorCrew;
        }

        return project.getSelectedCrew();
    }

    public boolean isSubmitted() {
        return status == ProjectSubmissionStatus.SUBMITTED;
    }

    public boolean isDraft() {
        return status
                == ProjectSubmissionStatus.DRAFT;
    }

    /**
     * 크루가 수정할 수 있는지 확인한다.
     */
    public boolean isEditable() {
        return isDraft();
    }

    /**
     * 기업이 피드백을 등록할 수 있는지 확인한다.
     */
    public boolean canRegisterFeedback() {
        return isSubmitted();
    }

    /**
     * 기존 데이터에 submittedAt이 없으면 생성 시각을 반환한다.
     */
    public LocalDateTime getResolvedSubmittedAt() {
        return submittedAt == null
                ? getCreatedAt()
                : submittedAt;
    }

    private static String resolveWriter(
            Project project,
            Crew authorCrew
    ) {
        if (authorCrew != null) {
            return authorCrew.getCrewName();
        }

        Crew selectedCrew = project.getSelectedCrew();

        return selectedCrew == null
                ? null
                : selectedCrew.getCrewName();
    }

    private static <T> List<T> copyList(
            List<T> values
    ) {
        if (values == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(values);
    }
}