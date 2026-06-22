package com.conx.server.notification.service;

import com.conx.server.bookmark.domain.ProjectBookmark;
import com.conx.server.bookmark.repository.ProjectBookmarkRepository;
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
    private final ProjectBookmarkRepository projectBookmarkRepository;

    public NotificationScheduler(ProjectRepository projectRepository,
                                 NotificationRepository notificationRepository,
                                 NotificationFacadeService notificationFacadeService, ProjectBookmarkRepository projectBookmarkRepository) {
        this.projectRepository = projectRepository;
        this.notificationFacadeService = notificationFacadeService;
        this.projectBookmarkRepository = projectBookmarkRepository;
    }

    //프로젝트 진행 기간 전(1일 / 3일 / 7일 전) 프로젝트 알림
    @Scheduled(cron = "0 0 0 * * *")
    public void saveNotificationOfProjectDeadlineProject(){
        List<Project> projects = projectRepository.findAllAboutProjectDeadlineProject(
                List.of(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(7)
                )
        );

        notificationFacadeService.saveNotificationAboutCloseToProjectDeadline(projects);
    }

    //모집마감 (1일 / 3일 / 7일 전) 프로젝트 알림
    @Scheduled(cron = "0 0 0 * * *")
    public void sendNotificationOfRecruitingDeadlineProject(){
        List<Project> projects = projectRepository.findAllAboutRecruitingDeadline(
                List.of(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(7)
                )
        );

        notificationFacadeService.saveNotificationAboutCloseToEndOfRecruitingProject(projects);
    }

    //최종결과물이 업로드되지 않은 프로젝트 마감 1/3/7일 전
    @Scheduled(cron = "0 0 0 * * *")
    public void sendNotificationOfSubmitDeadlineProject(){
        List<Project> projects = projectRepository.findAllAboutSubmitDeadlineProject(
                List.of(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(7)
                )
        );

        notificationFacadeService.saveNotificationAboutCloseToEndAndResultNotUploadedProject(projects);
    }

    //프로젝트 제출기한마감 후 결과물이 업로드되지 않음
    @Scheduled(cron = "0 0 0 * * *")
    public void sendNotificationOfNotSubmittedResultAfterDeadlineProject(){
        List<Project> projects = projectRepository.findAllAboutLateProject(LocalDate.now());
        notificationFacadeService.saveNotificationAboutLateForSubmitDeadlineProjects(projects);
    }

    //북마크한 프로젝트 신청마감 1/3/7일 전
    @Scheduled(cron = "0 0 0 * * *")
    public void sendNotificationOfDeadlineBookmarkProject(){
        //TODO: 북마크 작업 완료 후 수정...
        List<ProjectBookmark> bookmarkedProjects = projectBookmarkRepository.findAllAboutDeadline(
                List.of(
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(7)
                )
        );

        notificationFacadeService.saveNotificationAboutBookmarkedProject(bookmarkedProjects);
    }
}
