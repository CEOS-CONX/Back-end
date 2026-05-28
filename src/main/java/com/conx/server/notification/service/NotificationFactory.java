package com.conx.server.notification.service;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.domain.NotificationType;
import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class NotificationFactory {

    public Notification mail(User user, String mailSubject){
        return Notification.create(
                NotificationType.MAIL, user.getId(), NotificationType.MAIL.format(mailSubject)
        );
    }

    public Notification closeToEnd(Project project){
        long lastDay = ChronoUnit.DAYS.between(
                LocalDate.now(),
                project.getRecruitDeadLine()
        );

        return Notification.create(
                NotificationType.CLOSE_TO_END,
                project.getCompany().getId(),
                NotificationType.CLOSE_TO_END.format(project.getName(), lastDay)
        );
    }

    public Notification resultUploaded(Project project){
        return Notification.create(
                NotificationType.RESULT_UPLOADED,
                project.getCompany().getId(),
                NotificationType.RESULT_UPLOADED.format(project.getName()));
    }

    public Notification selected(Project project){
        return Notification.create(
                NotificationType.PROJECT_SELECTED,
                project.getSelectedCrew().getId(),
                NotificationType.PROJECT_SELECTED.format(project.getName())
        );
    }

    public Notification bookmarkedProjectCloseToEnd(ProjectBookmark projectBookmark) {
        long lastDay = ChronoUnit.DAYS.between(
                LocalDate.now(),
                projectBookmark.getProject().getRecruitDeadLine()
        );

        return Notification.create(
                NotificationType.CLOSE_TO_END_OF_MARKED_PROJECT,
                projectBookmark.getCrew().getId(),
                NotificationType.CLOSE_TO_END_OF_MARKED_PROJECT.format(projectBookmark.getProject().getName(), lastDay)
        );
    }

    public Notification adjustmentDone(Project project) {
        return Notification.create(
                NotificationType.ADJUSTMENT_DONE,
                project.getSelectedCrew().getId(),
                NotificationType.ADJUSTMENT_DONE.format(project.getName())
        );
    }
}