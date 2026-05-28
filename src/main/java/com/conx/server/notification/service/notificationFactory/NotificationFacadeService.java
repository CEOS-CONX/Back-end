package com.conx.server.notification.service.notificationFactory;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.Project;
import com.conx.server.user.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationFacadeService {

    private final NotificationRepository notificationRepository;

    public NotificationFacadeService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    //메일 전송 후
    public void saveNotificationAboutMail(User user, String mailSubject){
        Notification notification = NotificationFactory.mail(user, mailSubject);
        notificationRepository.save(notification);
    }

    //모집마감 1/3/7일 전
    public void saveNotificationAboutCloseToEndProject(List<Project> projects){
        List<Notification> notifications = projects.stream().map(NotificationFactory::closeToEnd).toList();
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

    //북마크한 프로젝트가 곧 모집마감됨(크루용)
    public void saveNotificationAboutBookmarkedProject(ProjectBookmark projectBookmark){
        Notification notification = NotificationFactory.bookmarkedProjectCloseToEnd(projectBookmark);
        notificationRepository.save(notification);
    }

    //정산이 완료됨(크류용)
    public void saveNotificationAboutAdjustmentDone(Project project){
        Notification notification = NotificationFactory.adjustmentDone(project);
        notificationRepository.save(notification);
    }
}