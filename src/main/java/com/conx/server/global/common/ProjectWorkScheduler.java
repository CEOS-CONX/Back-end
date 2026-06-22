package com.conx.server.global.common;


import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class ProjectWorkScheduler {

    private final ProjectRepository projectRepository;

    public ProjectWorkScheduler(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void expireProject(){
        List<Project> expiredProjects = projectRepository.findExpireProject(LocalDate.now());
        expiredProjects.forEach(Project::expire);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void endOfSubmitDate(){
        List<Project> endOfSubmitDateProjects = projectRepository.findAllByProjectDeadlineAndStatus(LocalDate.now(), ProjectStatus.PROGRESS);
        endOfSubmitDateProjects.forEach(Project::afterProjectDeadline);
    }
}
