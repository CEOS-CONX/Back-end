package com.conx.server.global.common;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ResultFormRequestDTO;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.admin.Admin;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.repository.AdminRepository;
import com.conx.server.user.repository.CompanyRepository;
import com.conx.server.user.repository.CrewRepository;
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
    private final AdminRepository adminRepository;

    @PostConstruct
    @Transactional
    public void normalSetting() {
        settingUser();
        settingProject();
    }

    @Transactional
    protected void settingUser() {
        String password = passwordEncoder.encode("1q2w3e4r!!");

        Admin admin = new Admin("jclee@gmail.com", password);

        Crew crew1 = Crew.create("kimdoes2143@naver.com", password);
        crew1.activateCrew("홍익대학교 서예동아리", CrewType.CLUB, null, "지수민", "동아리 학회장");

        Crew crew2 = Crew.create("testing1234@gmail.com", password);
        crew2.activateCrew("연세대학교 IT창업동아리", CrewType.ETC, "연합동아리", "윤지영", "대외협력국장");

        Crew crew3 = Crew.create("conxTestEmail@gmail.com", password);
        crew3.activateCrew("이화여자대학교 화학공학과 총학생회", CrewType.COUNCIL, null, "이다현", "총학회장");

        Crew crew4 = Crew.create("crewDance@gmail.com", password);
        crew4.activateCrew("고려대학교 댄스동아리 스텝", CrewType.CLUB, null, "박서준", "동아리 부회장");

        Crew crew5 = Crew.create("crewPhoto@gmail.com", password);
        crew5.activateCrew("성균관대학교 사진동아리 포커스", CrewType.CLUB, "촬영전문", "김하늘", "동아리 총무");

        Crew crew6 = Crew.create("crewStartup@gmail.com", password);
        crew6.activateCrew("한양대학교 창업연합동아리", CrewType.ETC, "창업연합", "이준영", "대표");

        Crew crew7 = Crew.create("crewCouncil2@gmail.com", password);
        crew7.activateCrew("서강대학교 경영학과 학생회", CrewType.COUNCIL, null, "최유진", "학생회장");

        Crew crew8 = Crew.create("crewMusic@gmail.com", password);
        crew8.activateCrew("중앙대학교 밴드동아리 사운드웨이브", CrewType.CLUB, null, "정다은", "동아리 회장");

        Company company1 = Company.create("kdhyun422@gmail.com", password);
        company1.activateCompany("코카콜라", Industry.ETC, "음료", "정수진", "코카콜라 코리아 마케팅팀 대리");

        Company company2 = Company.create("companyTest1234@gmail.com", password);
        company2.activateCompany("올리브영", Industry.BEAUTY, null, "채원호", "올리브영 마케팅팀 팀장");

        Company company3 = Company.create("navernaver@gmail.com", password);
        company3.activateCompany("네이버", Industry.IT, null, "최수연", "네이버 대표");

        Company company4 = Company.create("companyFood@gmail.com", password);
        company4.activateCompany("배달의민족", Industry.IT, "푸드테크", "김범준", "우아한형제들 마케팅팀장");

        Company company5 = Company.create("companyFashion@gmail.com", password);
        company5.activateCompany("무신사", Industry.BEAUTY, "패션", "한문일", "무신사 브랜드마케팅팀 팀장");

        Company company6 = Company.create("companySports@gmail.com", password);
        company6.activateCompany("나이키코리아", Industry.LIFESTYLE, "스포츠용품", "홍정욱", "나이키 코리아 마케팅 이사");

        Company company7 = Company.create("companyEdu@gmail.com", password);
        company7.activateCompany("메가스터디", Industry.ETC, "교육", "손주은", "메가스터디교육 대표");

        Company company8 = Company.create("companyCafe@gmail.com", password);
        company8.activateCompany("스타벅스코리아", Industry.LIFESTYLE, "카페", "송호섭", "스타벅스코리아 대표이사");

        crewRepository.saveAll(
                List.of(crew1, crew2, crew3, crew4, crew5, crew6, crew7, crew8)
        );

        companyRepository.saveAll(
                List.of(company1, company2, company3, company4, company5, company6, company7, company8)
        );

        adminRepository.save(admin);
    }

    @Transactional
    protected void settingProject() {
        CompanyProjectRequestDTO request1 = new CompanyProjectRequestDTO(
                "네이버", "이수진", "navernaver@gmail.com",
                List.of(),
                "치지직 오픈베타기능 테스트 및 홍보 프로젝트",
                "치지직의 신기능을 테스트하고 홍보합니다. 신기능은 크루 선정 후 공개될 예정입니다.",
                Industry.IT, ProjectType.APPTEST,
                List.of(new ResultFormRequestDTO("치지직", "숏폼", 2, "프로젝트 보고서(PDF)")),
                LocalDate.of(2026, 6, 30), LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 15),
                500000L, true, "숏폼 조회수 총합 100만회 달성",
                CrewType.CLUB, 5, "숏폼 제작 및 프로젝트 보고서 작성", "열정적으로 참여할 수 있는 분",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request2 = new CompanyProjectRequestDTO(
                "코카콜라", "정민영", "kdhyun422@gmail.com",
                List.of(),
                "코카콜라 신제품 홍보 및 협찬 프로젝트",
                "코카콜라 신제품 자몽소다맛 콜라를 협찬 및 홍보하는 프로젝트입니다.",
                Industry.LIFESTYLE, ProjectType.APPTEST,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 1, "활동 결과 보고서")),
                LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 20),
                LocalDate.of(2026, 7, 14), LocalDate.of(2026, 7, 20),
                250000L, true, "회원 리뷰 15개 이상",
                CrewType.COUNCIL, 10, "동아리 행사에 자사 음료 제공 및 인스타그램 업로드", "학생회 간식행사 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request3 = new CompanyProjectRequestDTO(
                "배달의민족", "이지훈", "companyFood@gmail.com",
                List.of(),
                "배민 신규 라이더 앱 사용성 테스트",
                "배달의민족 신규 라이더 전용 앱의 UX를 테스트하고 피드백을 받는 프로젝트입니다.",
                Industry.IT, ProjectType.APPTEST,
                List.of(new ResultFormRequestDTO("유튜브", "리뷰영상", 1, "사용성 테스트 보고서")),
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 7, 20), LocalDate.of(2026, 8, 5),
                300000L, false, null,
                CrewType.ETC, 8, "앱 설치 후 일주일간 사용하며 피드백 작성", "IT 및 스타트업에 관심있는 분",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request4 = new CompanyProjectRequestDTO(
                "무신사", "김도연", "companyFashion@gmail.com",
                List.of(),
                "무신사 스탠다드 여름 신상 룩북 촬영 프로젝트",
                "무신사 스탠다드 여름 신상 의류를 활용한 룩북 콘텐츠를 제작합니다.",
                Industry.BEAUTY, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 2, "촬영본 원본 파일")),
                LocalDate.of(2026, 6, 20), LocalDate.of(2026, 6, 25),
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 25),
                400000L, true, "게시물당 좋아요 500개 이상",
                CrewType.CLUB, 6, "룩북 촬영 및 SNS 업로드", "패션/사진에 관심있는 분 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request5 = new CompanyProjectRequestDTO(
                "나이키코리아", "박현우", "companySports@gmail.com",
                List.of(),
                "나이키 러닝 클럽 대학 홍보 프로젝트",
                "대학생 러닝 크루를 대상으로 나이키 러닝 클럽 앱 체험 및 홍보를 진행합니다.",
                Industry.LIFESTYLE, ProjectType.APPTEST,
                List.of(new ResultFormRequestDTO("인스타그램", "스토리", 3, "체험 후기 보고서")),
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 15),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 20),
                350000L, true, "누적 조회수 30만회 달성",
                CrewType.ETC, 12, "러닝 모임 진행 및 앱 체험 콘텐츠 제작", "러닝 동아리 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request6 = new CompanyProjectRequestDTO(
                "메가스터디", "윤서아", "companyEdu@gmail.com",
                List.of(),
                "메가스터디 대학생 서포터즈 모집",
                "고등학생 대상 학습 콘텐츠 홍보를 위한 대학생 서포터즈를 모집합니다.",
                Industry.ETC, ProjectType.APPTEST,
                List.of(new ResultFormRequestDTO("블로그", "포스팅", 4, "월간 활동 보고서")),
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 20), LocalDate.of(2026, 9, 20),
                200000L, false, null,
                CrewType.COUNCIL, 15, "월 2회 학습 콘텐츠 제작 및 배포", "교육 콘텐츠 제작 경험자 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request7 = new CompanyProjectRequestDTO(
                "스타벅스코리아", "임채원", "companyCafe@gmail.com",
                List.of(),
                "스타벅스 여름 시즌 메뉴 체험단",
                "신메뉴 출시에 맞춰 대학가 인근 매장에서 체험 후기를 남기는 프로젝트입니다.",
                Industry.LIFESTYLE, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 1, "체험 후기 사진 및 글")),
                LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 8),
                LocalDate.of(2026, 7, 15), LocalDate.of(2026, 7, 25),
                150000L, false, null,
                CrewType.CLUB, 20, "매장 방문 후 메뉴 체험 및 SNS 후기 작성", "누구나 지원 가능",
                List.of(), List.of()
        );

        Company naver = companyRepository.findByEmail("navernaver@gmail.com").orElseThrow();
        Company cocaCola = companyRepository.findByEmail("kdhyun422@gmail.com").orElseThrow();
        Company baemin = companyRepository.findByEmail("companyFood@gmail.com").orElseThrow();
        Company musinsa = companyRepository.findByEmail("companyFashion@gmail.com").orElseThrow();
        Company nike = companyRepository.findByEmail("companySports@gmail.com").orElseThrow();
        Company megastudy = companyRepository.findByEmail("companyEdu@gmail.com").orElseThrow();
        Company starbucks = companyRepository.findByEmail("companyCafe@gmail.com").orElseThrow();

        Project project1 = Project.createRecruitingProject(naver, request1);
        Project project2 = Project.createRecruitingProject(cocaCola, request2);
        Project project3 = Project.createRecruitingProject(baemin, request3);
        Project project4 = Project.createRecruitingProject(musinsa, request4);
        Project project5 = Project.createRecruitingProject(nike, request5);
        Project project6 = Project.createRecruitingProject(megastudy, request6);
        Project project7 = Project.createRecruitingProject(starbucks, request7);

        projectRepository.saveAll(
                List.of(project1, project2, project3, project4, project5, project6, project7)
        );
    }
}