package com.conx.server.notification.service;

import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.project.domain.Project;
import com.conx.server.project.repository.ProjectRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationScheduler {

    private final NotificationFactory notificationFactory;
    private final ProjectRepository projectRepository;
    private final NotificationRepository notificationRepository;

    public NotificationScheduler(NotificationFactory notificationFactory, ProjectRepository projectRepository, NotificationRepository notificationRepository) {
        this.notificationFactory = notificationFactory;
        this.projectRepository = projectRepository;
        this.notificationRepository = notificationRepository;
    }

    //마감임박 (1일 / 3일 / 7일 전) 프로젝트 알림
    @Scheduled(cron = "0 0 0 * * *")
    public void sendNotificationOfDeadlineProject(){
        List<Project> projects = projectRepository.findAllAboutDeadline(
                List.of(
                        LocalDate.now().minusDays(1),
                        LocalDate.now().minusDays(3),
                        LocalDate.now().minusDays(7)
                )
        );


        List<Notification> notifications = new ArrayList<>();

        for (Project p : projects){
            notifications.add(notificationFactory.closeToEnd(p));
        }

        notificationRepository.saveAll(notifications);
    }

    //북마크한 프로젝트 신청마감 3일 전
    @Scheduled(cron = "0 0 0 * * *")
    public void sendNotificationOfDeadlineBookmarkProject(){
        //TODO: 북마크 작업 완료 후 수정...
    }
}
