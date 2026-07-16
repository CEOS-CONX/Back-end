package com.conx.server.notification.service.notificationFactory;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.domain.NotificationType;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectApplication;
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
                project.getCompanyName()
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
                project.getCompanyName()
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
                project.getCompanyName()
        );
    }

    static Notification resultUploaded(Project project){
        return Notification.create(
                NotificationType.RESULT_UPLOADED,
                project.getCompany().getId(),
                NotificationType.RESULT_UPLOADED.format(project.getProjectName()),
                project.getCrewName()
        );
    }

    static Notification selected(Project project){
        return Notification.create(
                NotificationType.PROJECT_SELECTED,
                project.getSelectedCrew().getId(),
                NotificationType.PROJECT_SELECTED.format(project.getProjectName()),
                project.getCompanyName()
        );
    }

    static Notification rejected(ProjectApplication projectApplication){
        return Notification.create(
                NotificationType.PROJECT_REJECTED,
                projectApplication.getCrew().getId(),
                NotificationType.PROJECT_REJECTED.format(projectApplication.getProject().getProjectName()),
                projectApplication.getCompanyName()
        );
    }

    static Notification bookmarkedProjectCloseToEnd(ProjectBookmark projectBookmark) {
        long lastDay = ChronoUnit.DAYS.between(
                LocalDate.now(),
                projectBookmark.getProject().getRecruitDeadLine()
        );

        return Notification.create(
                NotificationType.CLOSE_TO_END_OF_MARKED_PROJECT,
                projectBookmark.getCrew().getId(),
                NotificationType.CLOSE_TO_END_OF_MARKED_PROJECT.format(projectBookmark.getProject().getProjectName(), lastDay),
                projectBookmark.getProject().getCompanyName()
        );
    }

    static Notification adjustmentDone(Project project) {
        return Notification.create(
                NotificationType.ADJUSTMENT_DONE,
                project.getSelectedCrew().getId(),
                NotificationType.ADJUSTMENT_DONE.format(project.getProjectName()),
                "CONX"
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
                project.getCompany().getCompanyName()
        );
    }
}