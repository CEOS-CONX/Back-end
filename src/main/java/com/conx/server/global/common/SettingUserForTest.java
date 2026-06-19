package com.conx.server.global.common;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequest;
import com.conx.server.user.repository.CompanyRepository;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.repository.EvaluationRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("test")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SettingUserForTest {

    private final PasswordEncoder passwordEncoder;
    private final CrewRepository crewRepository;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;
    private final EvaluationRepository evaluationRepository;

    @PostConstruct
    @Transactional
    public void normalSetting(){
        settingUser();
        settingProject();
    }

    @Transactional
    protected void settingUser(){
        String password = passwordEncoder.encode("1q2w3e4r!!");

        Crew crew1 = Crew.create("kimdoes2143@naver.com", password);
        crew1.activateCrew("홍익대학교 서예동아리", CrewType.CLUB, null, "지수민", "동아리 학회장");

        Crew crew2 = Crew.create("testing1234@gmail.com", password);
        crew2.activateCrew("연세대학교 IT창업동아리", CrewType.ETC, "연합동아리", "윤지영", "대외협력국장");

        Crew crew3 = Crew.create("conxTestEmail@gmail.com", password);
        crew3.activateCrew("이화여자대학교 화학공학과 총학생회", CrewType.COUNCIL, null, "이다현", "총학회장");


        Company company1 = Company.create("kdhyun422@gmail.com", password);
        company1.activateCompany("코카콜라", Industry.ETC, "음료","정수진", "코카콜라 코리아 마케팅팀 대리");

        Company company2 = Company.create("companyTest1234@gmail.com", password);
        company2.activateCompany("올리브영", Industry.BEAUTY, null,"채원호", "올리브영 마케팅팀 팀장");

        Company company3 = Company.create("navernaver@gmail.com", password);
        company3.activateCompany("네이버", Industry.IT, null,"최수연", "네이버 대표");

        crewRepository.saveAll(List.of(crew1, crew2, crew3));
        companyRepository.saveAll(List.of(company1, company2, company3));


        Evaluation evaluation1 = Evaluation.create(crew1);
        Evaluation evaluation2 = Evaluation.create(crew2);
        Evaluation evaluation3 = Evaluation.create(crew3);
        evaluationRepository.saveAll(List.of(evaluation1, evaluation2, evaluation3));
    }

    @Transactional
    protected void settingProject(){
        CompanyProjectRequest req1 = new CompanyProjectRequest(
                null, "네이버", "이수진", "navernaver@gmail.com", null,
                "치지직 오픈베타기능 테스트 및 홍보 프로젝트", "숏폼 업로드 및 프로젝트 보고서 작성",
                ProjectType.APPTEST, "숏폼 2편 업로드, 프로젝트 보고서, 5만 회 이상의 조회수",
                "치지직의 신기능을 테스트하고 홍보합니다. 신기능은 크루 선정 후 공개될 예정입니다.",
                "pdf파일", "숏폼, 보고서, 숏폼 인사이트 정보",
                LocalDate.of(2026, 6, 30), LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 15),
                CrewType.CLUB, null, "열정적으로 참여할 수 있는 분",
                500000L, true, "숏폼 조회수 총합 100만회 달성", null, null
        );
        Project project1 = Project.createRecruitingProject(companyRepository.findByEmail("navernaver@gmail.com").get(), req1);


        CompanyProjectRequest req2 = new CompanyProjectRequest(
                null, "코카콜라", "정민영", "kdhyun422@gmail.com", null,
                "코카콜라 신제품 홍보 및 협찬 프로젝트", "동아리 행사 등에 코카콜라 신제품을 협찬 및 홍보 목적의 프로젝트입니다.",
                ProjectType.APPTEST, "동아리 행사에 자사 음료 제공 및 동아리 인스타그램에 업로드",
                "코카콜라 신제품 자몽소다맛 콜라를 협찬 및 홍보하는 프로젝트입니다.",
                "pdf파일", "보고서, 동아리 회원들의 리뷰",
                LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 20),
                LocalDate.of(2026, 7, 14), LocalDate.of(2026, 7, 20),
                CrewType.COUNCIL, null, "학생회 간식행사 우대",
                250000L, true, "회원 리뷰 15개 이상", null, null
        );
        Project project2 = Project.createRecruitingProject(companyRepository.findByEmail("kdhyun422@gmail.com").get(), req2);

        projectRepository.saveAll(List.of(project1, project2));
    }

}