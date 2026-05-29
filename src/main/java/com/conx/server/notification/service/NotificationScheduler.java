package com.conx.server.notification.service;

import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.notification.service.notificationFactory.NotificationFacadeService;
import com.conx.server.project.domain.Project;
import com.conx.server.project.repository.ProjectRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class NotificationScheduler {

    private final ProjectRepository projectRepository;
    private final NotificationFacadeService notificationFacadeService;

    public NotificationScheduler(ProjectRepository projectRepository,
                                 NotificationRepository notificationRepository,
                                 NotificationFacadeService notificationFacadeService) {
        this.projectRepository = projectRepository;
        this.notificationFacadeService = notificationFacadeService;
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

        notificationFacadeService.saveNotificationAboutCloseToEndProject(projects);
    }

    //북마크한 프로젝트 신청마감 3일 전
    @Scheduled(cron = "0 0 0 * * *")
    public void sendNotificationOfDeadlineBookmarkProject(){
        //TODO: 북마크 작업 완료 후 수정...
    }
}
