package com.conx.server.notification.service.notificationFactory;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.domain.NotificationType;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectApplication;
import com.conx.server.project.domain.ProjectQuestion;
import com.conx.server.project.domain.ProjectSubmission;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
class NotificationFactory {

    static Notification closeToEndAboutResultNotUploadedProject(Project project) {
        long lastDay = ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getSubmitDeadline()
        );

        return Notification.create(
                NotificationType.RESULT_UPLOAD_CLOSE_TO_END,
                project.getSelectedCrew().getId(),
                NotificationType.RESULT_UPLOAD_CLOSE_TO_END.format(project.getProjectName(), lastDay),
                project.getCompanyName(),
                project.getId(),
                null,
                null,
                null,
                null
        );
    }

    static Notification resultNotSubmittedAfterSubmitDeadline(Project project){
        long afterDay = ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getSubmitDeadline()
        );

        return Notification.create(
                NotificationType.LATE_FOR_SUBMIT_DEADLINE,
                project.getSelectedCrew().getId(),
                NotificationType.LATE_FOR_SUBMIT_DEADLINE.format(project.getProjectName(), afterDay),
                project.getCompanyName(),
                project.getId(),
                null,
                null,
                null,
                null
        );
    }

    static Notification closeToEndOfRecruiting(Project project){
        long lastDay = ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getRecruitDeadLine()
        );

        return Notification.create(
                NotificationType.CLOSE_TO_END_OF_RECRUITING,
                project.getCompany().getId(),
                NotificationType.CLOSE_TO_END_OF_RECRUITING.format(project.getProjectName(), lastDay),
                project.getCompanyName(),
                project.getId(),
                null,
                null,
                null,
                null
        );
    }

    static Notification resultUploaded(Project project){
        return Notification.create(
                NotificationType.RESULT_UPLOADED,
                project.getCompany().getId(),
                NotificationType.RESULT_UPLOADED.format(project.getProjectName()),
                project.getCrewName(),
                project.getId(),
                null,
                null,
                null,
                null
        );
    }

    static Notification resultUploaded(ProjectSubmission submission){
        Project project = submission.getProject();

        return Notification.create(
                NotificationType.RESULT_UPLOADED,
                project.getCompany().getId(),
                NotificationType.RESULT_UPLOADED.format(project.getProjectName()),
                project.getCrewName(),
                project.getId(),
                null,
                null,
                submission.getId(),
                null
        );
    }

    static Notification projectQuestionRegistered(ProjectQuestion q){
        Project project = q.getProject();

        return Notification.create(
                NotificationType.QUESTION_REGISTERED,
                project.getCompany().getId(),
                NotificationType.QUESTION_REGISTERED.format(project.getProjectName()),
                q.getWriterName(),
                project.getId(),
                q.getId(),
                null,
                null,
                null
        );
    }

    static Notification projectAnswerRegistered(ProjectQuestion q){
        Project project = q.getProject();

        return Notification.create(
                NotificationType.QUESTION_ANSWER_REGISTERED,
                q.getWriterId(),
                NotificationType.QUESTION_ANSWER_REGISTERED.format(project.getProjectName()),
                project.getCompanyName(),
                project.getId(),
                q.getId(),
                null,
                null,
                null
        );
    }

    static Notification selected(Project project){
        return Notification.create(
                NotificationType.PROJECT_SELECTED,
                project.getSelectedCrew().getId(),
                NotificationType.PROJECT_SELECTED.format(project.getProjectName()),
                project.getCompanyName(),
                project.getId(),
                null,
                null,
                null,
                null
        );
    }

    static Notification selected(ProjectApplication projectApplication){
        Project project = projectApplication.getProject();

        return Notification.create(
                NotificationType.PROJECT_SELECTED,
                projectApplication.getCrew().getId(),
                NotificationType.PROJECT_SELECTED.format(project.getProjectName()),
                project.getCompanyName(),
                project.getId(),
                null,
                projectApplication.getId(),
                null,
                null
        );
    }

    static Notification rejected(ProjectApplication projectApplication){
        Project project = projectApplication.getProject();

        return Notification.create(
                NotificationType.PROJECT_REJECTED,
                projectApplication.getCrew().getId(),
                NotificationType.PROJECT_REJECTED.format(project.getProjectName()),
                projectApplication.getCompanyName(),
                project.getId(),
                null,
                projectApplication.getId(),
                null,
                null
        );
    }

    static Notification bookmarkedProjectCloseToEnd(ProjectBookmark projectBookmark) {
        Project project = projectBookmark.getProject();

        long lastDay = ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getRecruitDeadLine()
        );

        return Notification.create(
                NotificationType.CLOSE_TO_END_OF_MARKED_PROJECT,
                projectBookmark.getCrew().getId(),
                NotificationType.CLOSE_TO_END_OF_MARKED_PROJECT.format(project.getProjectName(), lastDay),
                project.getCompanyName(),
                project.getId(),
                null,
                null,
                null,
                null
        );
    }

    static Notification adjustmentDone(Project project) {
        return Notification.create(
                NotificationType.ADJUSTMENT_DONE,
                project.getSelectedCrew().getId(),
                NotificationType.ADJUSTMENT_DONE.format(project.getProjectName()),
                "CONX",
                project.getId(),
                null,
                null,
                null,
                null
        );
    }

    //
    static Notification closeToProjectDeadline(Project project){
        long lastDay = ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getProjectDeadline()
        );

        return Notification.create(
                NotificationType.PROJECT_CLOSE_TO_END,
                project.getSelectedCrew().getId(),
                NotificationType.PROJECT_CLOSE_TO_END.format(project.getProjectName(), lastDay),
                project.getCompany().getCompanyName(),
                project.getId(),
                null,
                null,
                null,
                null
        );
    }
}
