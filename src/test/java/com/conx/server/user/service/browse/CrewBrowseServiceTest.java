package com.conx.server.user.service.browse;

import com.conx.server.bookmark.repository.CrewBookmarkRepository;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.domain.enums.ProjectStatus;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.crew.Evaluation;
import com.conx.server.user.domain.types.CrewType;
import com.conx.server.user.domain.types.Industry;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.crew.CrewProjectHistorySort;
import com.conx.server.user.dto.crew.response.CrewBrowseDetailResponse;
import com.conx.server.user.dto.crew.response.CrewBrowseResponse;
import com.conx.server.user.dto.crew.response.CrewProjectHistoryResponse;
import com.conx.server.user.repository.CrewFileRepository;
import com.conx.server.user.repository.CrewLinkRepository;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.repository.EvaluationRepository;
import com.conx.server.user.repository.PortfolioRepository;
import com.conx.server.user.service.common.UserFinder;
import com.conx.server.user.domain.crew.CrewRepresentativeProject;
import com.conx.server.user.repository.CrewRepresentativeProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CrewBrowseServiceTest {

    private static final List<ProjectStatus> HISTORY_STATUSES =
            List.of(
                    ProjectStatus.PROGRESS,
                    ProjectStatus.WAITING_RESULT,
                    ProjectStatus.INSPECTION,
                    ProjectStatus.ADJUSTING,
                    ProjectStatus.DONE
            );

    @Mock
    private UserFinder userFinder;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private CrewRepository crewRepository;

    @Mock
    private CrewBookmarkRepository crewBookmarkRepository;

    @Mock
    private CrewLinkRepository crewLinkRepository;

    @Mock
    private CrewFileRepository crewFileRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CustomUserDetails companyUserDetails;

    @Mock
    private Crew crew;

    @Mock
    private Project project;

    @Mock
    private Evaluation evaluation;

    @InjectMocks
    private CrewBrowseService crewBrowseService;

    @Mock
    private CrewRepresentativeProjectRepository
            crewRepresentativeProjectRepository;

    @Mock
    private CrewRepresentativeProject representativeProject;

    @Test
    @DisplayName("기업이 크루 목록을 조회하면 북마크 여부를 반영한다")
    void getCrewsWithCompanyBookmark() {
        // given
        CrewBrowseResponse firstCrew =
                new CrewBrowseResponse(
                        1L,
                        "crew1.png",
                        "첫 번째 크루",
                        "첫 번째 소개",
                        Industry.IT,
                        CrewType.ACADEMY,
                        4.5,
                        5,
                        false
                );

        CrewBrowseResponse secondCrew =
                new CrewBrowseResponse(
                        2L,
                        "crew2.png",
                        "두 번째 크루",
                        "두 번째 소개",
                        Industry.IT,
                        CrewType.ACADEMY,
                        4.0,
                        3,
                        false
                );

        PageRequest pageable =
                PageRequest.of(0, 12);

        Page<CrewBrowseResponse> crewPage =
                new PageImpl<>(
                        List.of(firstCrew, secondCrew),
                        pageable,
                        2
                );

        given(companyUserDetails.getId())
                .willReturn(10L);

        doReturn(
                List.of(
                        new SimpleGrantedAuthority(
                                UserRole.COMPANY.getRole()
                        )
                )
        ).when(companyUserDetails)
                .getAuthorities();

        given(
                crewRepository.findBrowseCrewsOrderByRecent(
                        null,
                        null,
                        null,
                        pageable
                )
        ).willReturn(crewPage);

        given(
                crewBookmarkRepository
                        .findBookmarkedCrewIdsByCompanyIdAndCrewIds(
                                10L,
                                List.of(1L, 2L)
                        )
        ).willReturn(List.of(2L));

        // when
        Page<CrewBrowseResponse> result =
                crewBrowseService.getCrews(
                        null,
                        null,
                        null,
                        null,
                        0,
                        12,
                        companyUserDetails
                );

        // then
        assertThat(result.getContent())
                .hasSize(2);

        assertThat(
                result.getContent()
                        .get(0)
                        .bookmarked()
        ).isFalse();

        assertThat(
                result.getContent()
                        .get(1)
                        .bookmarked()
        ).isTrue();

        verify(
                crewBookmarkRepository
        ).findBookmarkedCrewIdsByCompanyIdAndCrewIds(
                10L,
                List.of(1L, 2L)
        );
    }

    @Test
    @DisplayName("직접 선택한 대표 프로젝트와 프로젝트별 평점을 반환한다")
    void getCrewDetailWithProjectEvaluation() {
        // given
        given(companyUserDetails.getId())
                .willReturn(10L);

        doReturn(
                List.of(
                        new SimpleGrantedAuthority(
                                UserRole.COMPANY.getRole()
                        )
                )
        ).when(companyUserDetails)
                .getAuthorities();

        given(userFinder.findActiveCrew(1L))
                .willReturn(crew);

        given(crew.getId())
                .willReturn(1L);

        given(crew.getProfileImage())
                .willReturn("crew.png");

        given(crew.getCrewName())
                .willReturn("테스트 크루");

        given(crew.getCrewType())
                .willReturn(CrewType.ACADEMY);

        given(crew.getActivityField())
                .willReturn("콘텐츠 마케팅");

        given(crew.getPublicSchools())
                .willReturn(
                        List.of("서강대학교")
                );

        given(crew.getMemberAmount())
                .willReturn(10);

        given(crew.getInterestingIndustry())
                .willReturn(Industry.IT);

        given(crew.getCatchphrase())
                .willReturn(
                        "브랜드의 이야기를 만드는 크루"
                );

        given(crew.getCrewIntroduction())
                .willReturn("크루 소개");

        given(crew.getPublicAdvantages())
                .willReturn(
                        List.of("기획")
                );

        given(crew.getPublicSpecialties())
                .willReturn(
                        List.of("SNS 운영")
                );

        given(crew.getTotalSubsidy())
                .willReturn(1_000_000);

        given(
                evaluationRepository.getMeanByCrew(crew)
        ).willReturn(
                Optional.of(4.2)
        );

        given(
                crewBookmarkRepository
                        .existsByCompanyIdAndCrewId(
                                10L,
                                1L
                        )
        ).willReturn(true);

        given(
                crewLinkRepository
                        .findAllByCrewIdOrderByIdAsc(1L)
        ).willReturn(List.of());

        given(
                crewFileRepository
                        .findAllByCrewIdOrderByIdAsc(1L)
        ).willReturn(List.of());

        given(
                portfolioRepository
                        .findAllByCrewIdOrderByIdDesc(1L)
        ).willReturn(List.of());

        /*
         * 최신 프로젝트 자동 조회가 아니라
         * 저장된 대표 프로젝트 연결을 조회합니다.
         */
        given(
                crewRepresentativeProjectRepository
                        .findAllByCrewIdOrderByDisplayOrderAsc(
                                1L
                        )
        ).willReturn(
                List.of(representativeProject)
        );

        given(representativeProject.getProject())
                .willReturn(project);

        given(project.getId())
                .willReturn(100L);

        given(project.getStatus())
                .willReturn(ProjectStatus.DONE);

        given(project.getName())
                .willReturn("SNS 콘텐츠 제작");

        given(project.getBrandName())
                .willReturn("CONX");

        given(project.getPlatformName())
                .willReturn("인스타그램");

        given(project.getContentType())
                .willReturn("릴스 영상");

        given(project.getProjectStartDate())
                .willReturn(
                        LocalDate.of(
                                2026,
                                1,
                                1
                        )
                );

        given(project.getProjectDeadline())
                .willReturn(
                        LocalDate.of(
                                2026,
                                2,
                                1
                        )
                );

        given(
                evaluationRepository.findAllByProjectIdIn(
                        List.of(100L)
                )
        ).willReturn(
                List.of(evaluation)
        );

        given(evaluation.getProject())
                .willReturn(project);

        given(evaluation.getMean())
                .willReturn(4.8);

        // when
        CrewBrowseDetailResponse result =
                crewBrowseService.getCrewDetail(
                        1L,
                        companyUserDetails
                );

        // then
        assertThat(result.point())
                .isEqualTo(4.2);

        assertThat(result.bookmarked())
                .isTrue();

        assertThat(result.activityField())
                .isEqualTo("콘텐츠 마케팅");

        assertThat(result.catchphrase())
                .isEqualTo(
                        "브랜드의 이야기를 만드는 크루"
                );

        assertThat(result.representativeProjects())
                .hasSize(1);

        CrewProjectHistoryResponse projectResponse =
                result.representativeProjects()
                        .get(0);

        assertThat(projectResponse.projectId())
                .isEqualTo(100L);

        assertThat(projectResponse.projectName())
                .isEqualTo("SNS 콘텐츠 제작");

        assertThat(projectResponse.point())
                .isEqualTo(4.8);

        assertThat(projectResponse.platformName())
                .isEqualTo("인스타그램");

        assertThat(projectResponse.contentType())
                .isEqualTo("릴스 영상");

        verify(
                crewRepresentativeProjectRepository
        ).findAllByCrewIdOrderByDisplayOrderAsc(
                1L
        );
    }

    @Test
    @DisplayName("평가가 없는 프로젝트의 point는 null이다")
    void getCrewProjectsWithoutProjectEvaluation() {
        // given
        given(userFinder.findActiveCrew(1L))
                .willReturn(crew);

        Pageable pageable =
                PageRequest.of(
                        0,
                        8,
                        Sort.by(
                                Sort.Order.desc("createdAt"),
                                Sort.Order.desc("id")
                        )
                );

        given(
                projectRepository.findCrewProjectHistory(
                        1L,
                        HISTORY_STATUSES,
                        pageable
                )
        ).willReturn(
                new PageImpl<>(
                        List.of(project),
                        pageable,
                        1
                )
        );

        given(project.getId())
                .willReturn(100L);

        given(project.getStatus())
                .willReturn(ProjectStatus.ADJUSTING);

        given(project.getName())
                .willReturn("평가 대기 프로젝트");

        given(project.getBrandName())
                .willReturn("CONX");

        given(
                evaluationRepository.findAllByProjectIdIn(
                        List.of(100L)
                )
        ).willReturn(List.of());

        // when
        Page<CrewProjectHistoryResponse> result =
                crewBrowseService.getCrewProjects(
                        1L,
                        0,
                        8,
                        CrewProjectHistorySort.RECENT
                );

        // then
        assertThat(result.getContent())
                .hasSize(1);

        assertThat(
                result.getContent()
                        .get(0)
                        .point()
        ).isNull();
    }

    @Test
    @DisplayName("프로젝트 전체보기의 size는 최대 8로 제한한다")
    void getCrewProjectsLimitsSizeToEight() {
        // given
        given(userFinder.findActiveCrew(1L))
                .willReturn(crew);

        Pageable expectedPageable =
                PageRequest.of(
                        0,
                        8,
                        Sort.by(
                                Sort.Order.desc("createdAt"),
                                Sort.Order.desc("id")
                        )
                );

        given(
                projectRepository.findCrewProjectHistory(
                        1L,
                        HISTORY_STATUSES,
                        expectedPageable
                )
        ).willReturn(
                Page.empty(expectedPageable)
        );

        // when
        Page<CrewProjectHistoryResponse> result =
                crewBrowseService.getCrewProjects(
                        1L,
                        0,
                        20,
                        CrewProjectHistorySort.RECENT
                );

        // then
        assertThat(result.getSize())
                .isEqualTo(8);

        verify(
                projectRepository
        ).findCrewProjectHistory(
                1L,
                HISTORY_STATUSES,
                expectedPageable
        );
    }
}