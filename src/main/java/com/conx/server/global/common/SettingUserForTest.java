package com.conx.server.global.common;

import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.ResultFormRequestDTO;
import com.conx.server.project.domain.enums.ProjectType;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.admin.Admin;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.CrewEvaluation;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.company.request.CompanyProjectRequestDTO;
import com.conx.server.user.repository.AdminRepository;
import com.conx.server.user.repository.CompanyRepository;
import com.conx.server.user.repository.CrewEvaluationRepository;
import com.conx.server.user.repository.CrewRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile({"test", "local"})
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SettingUserForTest {

    private final PasswordEncoder passwordEncoder;
    private final CrewRepository crewRepository;
    private final CompanyRepository companyRepository;
    private final ProjectRepository projectRepository;
    private final AdminRepository adminRepository;
    private final CrewEvaluationRepository crewEvaluationRepository;

    @PostConstruct
    @Transactional
    public void normalSetting() {
        settingProject();
    }

    @Transactional
    protected void settingProject() {
        if (projectRepository.count() > 0) {
            return;
        }

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

        Crew crew9 = Crew.create("cccccc@gmail.com", password);
        crew9.activateCrew("중앙대학교 밴드동아리 사운드웨이브", CrewType.CLUB, null, "정다은", "동아리 회장");


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
                List.of(crew1, crew2, crew3, crew4, crew5, crew6, crew7, crew8, crew9)
        );

        List<CrewEvaluation> evaluations = new ArrayList<>();
        for (Crew crew : List.of(crew1, crew2, crew3, crew4, crew5, crew6, crew7, crew8, crew9)){
            evaluations.add(CrewEvaluation.create(crew));
        }
        crewEvaluationRepository.saveAll(evaluations);

        companyRepository.saveAll(
                List.of(company1, company2, company3, company4, company5, company6, company7, company8)
        );

        adminRepository.save(admin);

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
                "배달의민족", "김범준", "companyFood@gmail.com",
                List.of(),
                "배민 캠퍼스 맛집 큐레이션 프로젝트",
                "대학교 주변 맛집을 소개하는 콘텐츠를 제작하는 프로젝트입니다.",
                Industry.IT, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 5, "활동 결과 보고서")),
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30),
                350000L, true, "게시물 도달 30만",
                CrewType.ETC, 10,
                "맛집 방문 및 콘텐츠 제작",
                "SNS 운영 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request6 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "companyFashion@gmail.com",
                List.of(),
                "무신사 겨울 컬렉션 스타일링 프로젝트",
                "겨울 시즌 신상품을 활용한 스타일링 콘텐츠를 제작합니다.",
                Industry.BEAUTY, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 2, "콘텐츠 링크")),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 10, 20), LocalDate.of(2026, 11, 20),
                500000L, true, "좋아요 2,000개",
                CrewType.CLUB, 6,
                "스타일링 콘텐츠 제작",
                "패션 콘텐츠 제작 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request7 = new CompanyProjectRequestDTO(
                "네이버", "최수연", "navernaver@gmail.com",
                List.of(),
                "네이버 클립 대학생 크리에이터 모집",
                "네이버 클립을 활용한 숏폼 콘텐츠를 제작하고 홍보하는 프로젝트입니다.",
                Industry.IT, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("네이버 클립", "숏폼", 3, "활동 결과 보고서")),
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 15),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                450000L, true, "누적 조회수 50만",
                CrewType.CLUB, 8,
                "클립 콘텐츠 제작 및 업로드",
                "영상 제작 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request8 = new CompanyProjectRequestDTO(
                "코카콜라", "정수진", "kdhyun422@gmail.com",
                List.of(),
                "코카콜라 대학축제 샘플링 프로젝트",
                "전국 대학축제에서 코카콜라 신제품을 홍보합니다.",
                Industry.LIFESTYLE, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 2, "활동보고서")),
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 10),
                450000L, true, "SNS 도달 50만",
                CrewType.CLUB, 10,
                "축제 부스 운영 및 SNS 홍보",
                "축제 운영 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request9 = new CompanyProjectRequestDTO(
                "코카콜라", "정수진", "kdhyun422@gmail.com",
                List.of(),
                "제로콜라 릴스 챌린지",
                "제로콜라를 활용한 숏폼 콘텐츠를 제작합니다.",
                Industry.LIFESTYLE, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 3, "영상 원본")),
                LocalDate.of(2026, 7, 3), LocalDate.of(2026, 7, 8),
                LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 20),
                350000L, true, "조회수 100만",
                CrewType.CLUB, 5,
                "릴스 제작",
                "영상 편집 가능자",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request10 = new CompanyProjectRequestDTO(
                "코카콜라", "정수진", "kdhyun422@gmail.com",
                List.of(),
                "캠퍼스 브랜드 홍보단",
                "캠퍼스 내 브랜드 홍보 활동입니다.",
                Industry.LIFESTYLE, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("블로그", "후기", 2, "활동보고서")),
                LocalDate.of(2026, 6, 15), LocalDate.of(2026, 6, 20),
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 8, 10),
                300000L, false, null,
                CrewType.COUNCIL, 8,
                "홍보물 배포",
                "학생회 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request11 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "companyFashion@gmail.com",
                List.of(),
                "가을 룩북 촬영",
                "무신사 FW 신상품 콘텐츠 제작 프로젝트입니다.",
                Industry.BEAUTY, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 2, "촬영 원본")),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 20),
                500000L, true, "좋아요 2000개",
                CrewType.CLUB, 6,
                "룩북 촬영",
                "사진동아리 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request12 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "companyFashion@gmail.com",
                List.of(),
                "캠퍼스 패션 홍보",
                "대학생 대상 패션 콘텐츠를 제작합니다.",
                Industry.BEAUTY, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 2, "콘텐츠 링크")),
                LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 30),
                400000L, false, null,
                CrewType.CLUB, 8,
                "패션 콘텐츠 제작",
                "SNS 운영 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request13 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "companyFashion@gmail.com",
                List.of(),
                "무신사 스탠다드 캠퍼스 화보",
                "무신사 스탠다드 의류를 활용한 대학생 화보 프로젝트입니다.",
                Industry.BEAUTY, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 3, "촬영 원본")),
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 25),
                550000L, true, "좋아요 3000개",
                CrewType.CLUB, 6,
                "화보 촬영 및 SNS 업로드",
                "모델 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request14 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "companyFood@gmail.com",
                List.of(),
                "배민 신기능 체험단",
                "배달의민족 앱의 신규 기능을 체험하고 피드백을 제공합니다.",
                Industry.IT, ProjectType.APPTEST,
                List.of(new ResultFormRequestDTO("설문지", "피드백", 1, "피드백 보고서")),
                LocalDate.of(2026, 7, 20), LocalDate.of(2026, 7, 25),
                LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 30),
                300000L, false, null,
                CrewType.ETC, 10,
                "앱 사용 후 피드백 제출",
                "앱 테스트 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request15 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "companyFood@gmail.com",
                List.of(),
                "캠퍼스 배민 홍보 프로젝트",
                "대학교 주변 배민 서비스 홍보 프로젝트입니다.",
                Industry.IT, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "스토리", 5, "활동보고서")),
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 20),
                250000L, true, "노출수 50만",
                CrewType.COUNCIL, 15,
                "홍보 콘텐츠 제작",
                "학생회 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request16 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "companyFood@gmail.com",
                List.of(),
                "배민 리뷰 콘텐츠 제작",
                "배달의민족 서비스를 소개하는 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("유튜브", "영상", 1, "영상 링크")),
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 30),
                450000L, true, "조회수 20만",
                CrewType.CLUB, 5,
                "영상 제작",
                "영상 편집 가능자",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request17 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "companyFood@gmail.com",
                List.of(),
                "배민 라이더 인터뷰 프로젝트",
                "라이더 인터뷰 콘텐츠를 제작하는 프로젝트입니다.",
                Industry.IT, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("유튜브", "인터뷰", 1, "편집본")),
                LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 9, 15), LocalDate.of(2026, 10, 5),
                500000L, true, "조회수 30만",
                CrewType.ETC, 4,
                "인터뷰 촬영 및 편집",
                "촬영 장비 보유자 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request18 = new CompanyProjectRequestDTO(
                "네이버", "최수연", "navernaver@gmail.com",
                List.of(),
                "네이버 지도 캠퍼스 리뷰 프로젝트",
                "대학교 주변 맛집과 명소를 네이버 지도에 리뷰하는 프로젝트입니다.",
                Industry.IT, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("네이버지도", "리뷰", 10, "활동 보고서")),
                LocalDate.of(2026, 8, 20), LocalDate.of(2026, 8, 25),
                LocalDate.of(2026, 9, 20), LocalDate.of(2026, 10, 10),
                300000L, true, "리뷰 100건 이상",
                CrewType.CLUB, 12,
                "맛집 방문 및 리뷰 작성",
                "성실하게 활동 가능한 분",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request19 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "companyFashion@gmail.com",
                List.of(),
                "무신사 대학생 브랜드 서포터즈",
                "무신사 브랜드를 홍보할 대학생 서포터즈를 모집합니다.",
                Industry.BEAUTY, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 4, "활동 보고서")),
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1),
                500000L, true, "콘텐츠 20건 이상",
                CrewType.CLUB, 15,
                "SNS 콘텐츠 제작 및 브랜드 홍보",
                "패션에 관심 있는 대학생",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request20 = new CompanyProjectRequestDTO(
                "코카콜라", "정수진", "kdhyun422@gmail.com",
                List.of(),
                "코카콜라 연말 캠페인 프로젝트",
                "연말 시즌을 맞아 코카콜라 브랜드 캠페인을 진행합니다.",
                Industry.LIFESTYLE, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 2, "최종 결과 보고서")),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 25),
                600000L, true, "누적 조회수 100만",
                CrewType.ETC, 10,
                "캠페인 콘텐츠 기획 및 제작",
                "영상 제작 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request21 = new CompanyProjectRequestDTO(
                "네이버", "최수연", "naver@gmail.com",
                List.of(),
                "네이버 플레이스 리뷰 크루 모집",
                "플레이스 서비스를 홍보할 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 3, "게시물 링크")),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 5),
                LocalDate.of(2026, 10, 10), LocalDate.of(2026, 11, 10),
                450000L, true, "조회수 20만",
                CrewType.CLUB, 8,
                "콘텐츠 제작",
                "영상 편집 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request22 = new CompanyProjectRequestDTO(
                "코카콜라", "James Park", "cocacola@gmail.com",
                List.of(),
                "코카콜라 제로 캠페인",
                "제로 음료 홍보 콘텐츠를 제작합니다.",
                Industry.LIFESTYLE, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 4, "게시물 링크")),
                LocalDate.of(2026, 10, 3), LocalDate.of(2026, 10, 8),
                LocalDate.of(2026, 10, 15), LocalDate.of(2026, 11, 15),
                400000L, true, "좋아요 3000개",
                CrewType.CLUB, 10,
                "SNS 콘텐츠 제작",
                "SNS 운영 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request23 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "baemin@gmail.com",
                List.of(),
                "배민 맛집 숏폼 프로젝트",
                "대학가 맛집을 소개하는 숏폼 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("유튜브", "쇼츠", 5, "영상 링크")),
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 10, 20), LocalDate.of(2026, 11, 20),
                500000L, false, "",
                CrewType.ETC, 6,
                "영상 제작",
                "숏폼 제작 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request24 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "musinsa@gmail.com",
                List.of(),
                "무신사 FW 스타일링 프로젝트",
                "가을 스타일링 콘텐츠를 제작합니다.",
                Industry.BEAUTY, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 3, "게시물 링크")),
                LocalDate.of(2026, 10, 6), LocalDate.of(2026, 10, 12),
                LocalDate.of(2026, 10, 22), LocalDate.of(2026, 11, 22),
                550000L, true, "조회수 15만",
                CrewType.CLUB, 7,
                "패션 콘텐츠 제작",
                "패션 계정 운영",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request25 = new CompanyProjectRequestDTO(
                "네이버", "최수연", "naver@gmail.com",
                List.of(),
                "네이버 지도 캠퍼스 프로젝트",
                "캠퍼스 주변 장소를 소개합니다.",
                Industry.IT, ProjectType.APPTEST,
                List.of(new ResultFormRequestDTO("블로그", "포스팅", 3, "URL")),
                LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 14),
                LocalDate.of(2026, 10, 25), LocalDate.of(2026, 11, 25),
                300000L, false, "",
                CrewType.ETC, 5,
                "리뷰 작성",
                "블로그 운영",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request26 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "baemin@gmail.com",
                List.of(),
                "배민 배달팁 캠페인",
                "배달팁 관련 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 4, "URL")),
                LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 16),
                LocalDate.of(2026, 10, 28), LocalDate.of(2026, 11, 28),
                450000L, true, "좋아요 5000개",
                CrewType.CLUB, 8,
                "콘텐츠 기획",
                "SNS 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request27 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "musinsa@gmail.com",
                List.of(),
                "무신사 스냅 챌린지",
                "OOTD 콘텐츠를 제작합니다.",
                Industry.BEAUTY, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 5, "URL")),
                LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 18),
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30),
                600000L, true, "조회수 25만",
                CrewType.CLUB, 12,
                "패션 콘텐츠",
                "패션 관심",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request28 = new CompanyProjectRequestDTO(
                "코카콜라", "James Park", "cocacola@gmail.com",
                List.of(),
                "연말 코카콜라 캠페인",
                "겨울 시즌 콘텐츠를 제작합니다.",
                Industry.LIFESTYLE, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("유튜브", "영상", 2, "영상 링크")),
                LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 20),
                LocalDate.of(2026, 11, 5), LocalDate.of(2026, 12, 5),
                650000L, true, "조회수 30만",
                CrewType.CLUB, 6,
                "영상 제작",
                "프리미어 사용 가능",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request29 = new CompanyProjectRequestDTO(
                "네이버", "최수연", "naver@gmail.com",
                List.of(),
                "네이버 블로그 챌린지",
                "블로그 체험 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("블로그", "포스팅", 5, "URL")),
                LocalDate.of(2026, 10, 18), LocalDate.of(2026, 10, 22),
                LocalDate.of(2026, 11, 8), LocalDate.of(2026, 12, 8),
                280000L, false, "",
                CrewType.ETC, 15,
                "블로그 작성",
                "성실한 활동",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request30 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "baemin@gmail.com",
                List.of(),
                "배민 신메뉴 리뷰단",
                "프랜차이즈 신메뉴를 리뷰합니다.",
                Industry.IT, ProjectType.UGC,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 4, "URL")),
                LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 25),
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 12, 10),
                350000L, true, "좋아요 2000개",
                CrewType.ETC, 10,
                "리뷰 작성",
                "맛집 리뷰 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request31 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "musinsa@gmail.com",
                List.of(),
                "겨울 아우터 스타일링 프로젝트",
                "무신사 FW 아우터를 활용한 스타일링 콘텐츠를 제작합니다.",
                Industry.BEAUTY, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 3, "게시물 링크")),
                LocalDate.of(2026, 10, 22), LocalDate.of(2026, 10, 28),
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 12, 10),
                500000L, true, "조회수 20만",
                CrewType.CLUB, 8,
                "패션 콘텐츠 제작",
                "스타일링 경험 우대",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request32 = new CompanyProjectRequestDTO(
                "코카콜라", "James Park", "cocacola@gmail.com",
                List.of(),
                "코카콜라 크리스마스 캠페인",
                "연말 시즌 SNS 홍보 콘텐츠를 제작합니다.",
                Industry.LIFESTYLE, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 4, "게시물 링크")),
                LocalDate.of(2026, 10, 25), LocalDate.of(2026, 10, 30),
                LocalDate.of(2026, 11, 15), LocalDate.of(2026, 12, 20),
                600000L, true, "좋아요 5000개",
                CrewType.CLUB, 10,
                "SNS 콘텐츠 제작",
                "영상 편집 가능",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request33 = new CompanyProjectRequestDTO(
                "네이버", "최수연", "naver@gmail.com",
                List.of(),
                "네이버 지도 숏폼 챌린지",
                "네이버 지도를 활용한 숏폼 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("유튜브", "쇼츠", 4, "영상 링크")),
                LocalDate.of(2026, 10, 27), LocalDate.of(2026, 11, 2),
                LocalDate.of(2026, 11, 15), LocalDate.of(2026, 12, 15),
                420000L, false, "",
                CrewType.CLUB, 7,
                "영상 제작",
                "숏폼 제작 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request34 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "baemin@gmail.com",
                List.of(),
                "배민 겨울 배달 캠페인",
                "겨울철 배달 문화를 홍보하는 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 5, "게시물 링크")),
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 6),
                LocalDate.of(2026, 11, 20), LocalDate.of(2026, 12, 20),
                450000L, true, "조회수 15만",
                CrewType.ETC, 12,
                "콘텐츠 기획",
                "SNS 운영 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request35 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "musinsa@gmail.com",
                List.of(),
                "무신사 스냅 콘테스트",
                "패션 스냅 콘텐츠를 제작합니다.",
                Industry.BEAUTY, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 5, "URL")),
                LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 8),
                LocalDate.of(2026, 11, 22), LocalDate.of(2026, 12, 22),
                380000L, false, "",
                CrewType.CLUB, 9,
                "사진 촬영",
                "패션 관심",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request36 = new CompanyProjectRequestDTO(
                "네이버", "최수연", "naver@gmail.com",
                List.of(),
                "네이버 블로그 체험단",
                "블로그 리뷰 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.UGC,
                List.of(new ResultFormRequestDTO("블로그", "포스팅", 4, "URL")),
                LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 25), LocalDate.of(2026, 12, 25),
                300000L, false, "",
                CrewType.ETC, 6,
                "리뷰 작성",
                "블로그 운영 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request37 = new CompanyProjectRequestDTO(
                "코카콜라", "James Park", "cocacola@gmail.com",
                List.of(),
                "코카콜라 대학생 이벤트",
                "캠퍼스 프로모션 콘텐츠를 제작합니다.",
                Industry.LIFESTYLE, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 3, "게시물 링크")),
                LocalDate.of(2026, 11, 8), LocalDate.of(2026, 11, 13),
                LocalDate.of(2026, 11, 28), LocalDate.of(2026, 12, 28),
                550000L, true, "조회수 25만",
                CrewType.CLUB, 8,
                "콘텐츠 제작",
                "SNS 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request38 = new CompanyProjectRequestDTO(
                "배달의민족", "김범준", "baemin@gmail.com",
                List.of(),
                "배민 리뷰 크루",
                "배달 음식 리뷰 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.UGC,
                List.of(new ResultFormRequestDTO("인스타그램", "피드", 5, "URL")),
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15),
                LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 31),
                350000L, true, "좋아요 3000개",
                CrewType.ETC, 10,
                "리뷰 작성",
                "맛집 콘텐츠 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request39 = new CompanyProjectRequestDTO(
                "네이버", "최수연", "naver@gmail.com",
                List.of(),
                "네이버 AI 서비스 체험단",
                "AI 서비스를 활용한 콘텐츠를 제작합니다.",
                Industry.IT, ProjectType.CAMPAIGN,
                List.of(new ResultFormRequestDTO("유튜브", "영상", 2, "영상 링크")),
                LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 18),
                LocalDate.of(2026, 12, 3), LocalDate.of(2027, 1, 3),
                650000L, true, "조회수 30만",
                CrewType.CLUB, 5,
                "영상 제작",
                "AI 서비스 경험",
                List.of(), List.of()
        );

        CompanyProjectRequestDTO request40 = new CompanyProjectRequestDTO(
                "무신사", "한문일", "musinsa@gmail.com",
                List.of(),
                "2026 연말 패션 캠페인",
                "연말 패션 콘텐츠를 제작하는 프로젝트입니다.",
                Industry.BEAUTY, ProjectType.MARKETING,
                List.of(new ResultFormRequestDTO("인스타그램", "릴스", 4, "게시물 링크")),
                LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 12, 5), LocalDate.of(2027, 1, 5),
                700000L, true, "조회수 40만",
                CrewType.CLUB, 10,
                "패션 콘텐츠 제작",
                "영상 편집 경험 우대",
                List.of(), List.of()
        );



        Company naver = companyRepository.findByEmail("navernaver@gmail.com").orElseThrow();
        Company cocaCola = companyRepository.findByEmail("kdhyun422@gmail.com").orElseThrow();
        Company baemin = companyRepository.findByEmail("companyFood@gmail.com").orElseThrow();
        Company musinsa = companyRepository.findByEmail("companyFashion@gmail.com").orElseThrow();

        Project project1 = Project.createRecruitingProject(naver, request1);
        Project project2 = Project.createRecruitingProject(cocaCola, request2);
        Project project3 = Project.createRecruitingProject(baemin, request3);
        Project project4 = Project.createRecruitingProject(musinsa, request4);
        Project project5 = Project.createRecruitingProject(baemin, request5);
        Project project6 = Project.createRecruitingProject(musinsa, request6);
        Project project7 = Project.createRecruitingProject(naver, request7);
        Project project8 = Project.createRecruitingProject(cocaCola, request8);
        Project project9 = Project.createRecruitingProject(cocaCola, request9);
        Project project10 = Project.createRecruitingProject(cocaCola, request10);
        Project project11 = Project.createRecruitingProject(musinsa, request11);
        Project project12 = Project.createRecruitingProject(musinsa, request12);
        Project project13 = Project.createRecruitingProject(musinsa, request13);
        Project project14 = Project.createRecruitingProject(baemin, request14);
        Project project15 = Project.createRecruitingProject(baemin, request15);
        Project project16 = Project.createRecruitingProject(baemin, request16);
        Project project17 = Project.createRecruitingProject(baemin, request17);
        Project project18 = Project.createRecruitingProject(naver, request18);
        Project project19 = Project.createRecruitingProject(musinsa, request19);
        Project project20 = Project.createRecruitingProject(cocaCola, request20);
        Project project21 = Project.createRecruitingProject(naver, request21);
        Project project22 = Project.createRecruitingProject(cocaCola, request22);
        Project project23 = Project.createRecruitingProject(baemin, request23);
        Project project24 = Project.createRecruitingProject(musinsa, request24);
        Project project25 = Project.createRecruitingProject(naver, request25);
        Project project26 = Project.createRecruitingProject(baemin, request26);
        Project project27 = Project.createRecruitingProject(musinsa, request27);
        Project project28 = Project.createRecruitingProject(cocaCola, request28);
        Project project29 = Project.createRecruitingProject(naver, request29);
        Project project30 = Project.createRecruitingProject(baemin, request30);
        Project project31 = Project.createRecruitingProject(musinsa, request31);
        Project project32 = Project.createRecruitingProject(cocaCola, request32);
        Project project33 = Project.createRecruitingProject(naver, request33);
        Project project34 = Project.createRecruitingProject(baemin, request34);
        Project project35 = Project.createRecruitingProject(musinsa, request35);
        Project project36 = Project.createRecruitingProject(naver, request36);
        Project project37 = Project.createRecruitingProject(cocaCola, request37);
        Project project38 = Project.createRecruitingProject(baemin, request38);
        Project project39 = Project.createRecruitingProject(naver, request39);
        Project project40 = Project.createRecruitingProject(musinsa, request40);

        // CONTRACT_PENDING
        project2.selectCrew(crew1);

        project11.selectCrew(crew2);

        project18.selectCrew(crew3);

        project27.selectCrew(crew4);

        project35.selectCrew(crew5);

// PROGRESS
        project5.selectCrew(crew6);
        project5.completeContract();

        project13.selectCrew(crew7);
        project13.completeContract();

        project22.selectCrew(crew1);
        project22.completeContract();

        project31.selectCrew(crew2);
        project31.completeContract();

        project39.selectCrew(crew3);
        project39.completeContract();

// WAITING_RESULT
        project8.selectCrew(crew4);
        project8.completeContract();
        project8.afterProjectDeadline();

        project16.selectCrew(crew5);
        project16.completeContract();
        project16.afterProjectDeadline();

        project24.selectCrew(crew6);
        project24.completeContract();
        project24.afterProjectDeadline();

        project33.selectCrew(crew7);
        project33.completeContract();
        project33.afterProjectDeadline();

// INSPECTION
        project4.selectCrew(crew1);
        project4.completeContract();
        project4.afterProjectDeadline();
        project4.submitProjectResult();

        project20.selectCrew(crew2);
        project20.completeContract();
        project20.afterProjectDeadline();
        project20.submitProjectResult();

        project28.selectCrew(crew3);
        project28.completeContract();
        project28.afterProjectDeadline();
        project28.submitProjectResult();

        project37.selectCrew(crew4);
        project37.completeContract();
        project37.afterProjectDeadline();
        project37.submitProjectResult();

// ADJUSTING
        project15.selectCrew(crew5);
        project15.completeContract();
        project15.afterProjectDeadline();
        project15.submitProjectResult();
        project15.completeInspection();

        project25.selectCrew(crew6);
        project25.completeContract();
        project25.afterProjectDeadline();
        project25.submitProjectResult();
        project25.completeInspection();

        project38.selectCrew(crew7);
        project38.completeContract();
        project38.afterProjectDeadline();
        project38.submitProjectResult();
        project38.completeInspection();

// DONE
        project10.selectCrew(crew1);
        project10.completeContract();
        project10.afterProjectDeadline();
        project10.submitProjectResult();
        project10.completeInspection();
        project10.end();

        project17.selectCrew(crew2);
        project17.completeContract();
        project17.afterProjectDeadline();
        project17.submitProjectResult();
        project17.completeInspection();
        project17.end();

        project23.selectCrew(crew3);
        project23.completeContract();
        project23.afterProjectDeadline();
        project23.submitProjectResult();
        project23.completeInspection();
        project23.end();

        project29.selectCrew(crew4);
        project29.completeContract();
        project29.afterProjectDeadline();
        project29.submitProjectResult();
        project29.completeInspection();
        project29.end();

        project40.selectCrew(crew5);
        project40.completeContract();
        project40.afterProjectDeadline();
        project40.submitProjectResult();
        project40.completeInspection();
        project40.end();

        projectRepository.saveAll(List.of(
                project1, project2, project3, project4, project5,
                project6, project7, project8, project9, project10,
                project11, project12, project13, project14, project15,
                project16, project17, project18, project19, project20,
                project21, project22, project23, project24, project25,
                project26, project27, project28, project29, project30,
                project31, project32, project33, project34, project35,
                project36, project37, project38, project39, project40
        ));
    }
}