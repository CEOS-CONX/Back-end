package com.conx.server.notification.service.notificationFactory;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ProjectApplication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationFacadeService {

    private final NotificationRepository notificationRepository;

    public NotificationFacadeService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    //프로젝트 마감 전(1일 / 3일 / 7일 전) 프로젝트 알림
    public void saveNotificationAboutCloseToProjectDeadline(List<Project> projects){
        List<Notification> notifications = projects.stream().map(NotificationFactory::closeToProjectDeadline).toList();
        notificationRepository.saveAll(notifications);
    }

    //모집마감 1/3/7일 전
    public void saveNotificationAboutCloseToEndOfRecruitingProject(List<Project> projects){
        List<Notification> notifications = projects.stream().map(NotificationFactory::closeToEndOfRecruiting).toList();
        notificationRepository.saveAll(notifications);
    }

    //최종결과물이 업로드되지 않은 채 기한만료
    public void saveNotificationAboutLateForSubmitDeadlineProjects(List<Project> projects){
        List<Notification> notifications = projects.stream().map(NotificationFactory::resultNotSubmittedAfterSubmitDeadline).toList();
        notificationRepository.saveAll(notifications);
    }

    //최종결과물이 업로드되지 않은 프로젝트 마감 1/3/7일 전
    public void saveNotificationAboutCloseToEndAndResultNotUploadedProject(List<Project> projects){
        List<Notification> notifications = projects.stream().map(NotificationFactory::closeToEndAboutResultNotUploadedProject).toList();
        notificationRepository.saveAll(notifications);
    }



    //크루가 최종결과물을 업로드함
    public void saveNotificationAboutResultUploaded(Project project){
        Notification notification = NotificationFactory.resultUploaded(project);
        notificationRepository.save(notification);
    }

    //프로젝트에 선정됨(크루용)
    public void saveNotificationAboutSelectedProject(Project project){
        Notification notification = NotificationFactory.selected(project);
        notificationRepository.save(notification);
    }

    //프로젝트에 선정되지 않음(크루용)
    public void saveNotificationAboutRejectedProject(ProjectApplication projectApplication){
        Notification notification = NotificationFactory.rejected(projectApplication);
        notificationRepository.save(notification);
    }

    //북마크한 프로젝트 모집마감 1/3/7일 전(크루용)
    public void saveNotificationAboutBookmarkedProject(List<ProjectBookmark> bookmarkedProjects){
        List<Notification> notifications = bookmarkedProjects.stream().map(NotificationFactory::bookmarkedProjectCloseToEnd).toList();
        notificationRepository.saveAll(notifications);
    }

    //정산이 완료됨(크류용)
    public void saveNotificationAboutAdjustmentDone(Project project){
        Notification notification = NotificationFactory.adjustmentDone(project);
        notificationRepository.save(notification);
    }
}