package com.conx.server.user.service.mypage;

import com.conx.server.bookmark.repository.ProjectBookmarkRepository;
import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.dto.crew.request.CrewProfileUpdateRequest;
import com.conx.server.user.dto.crew.response.CrewBookmarkedProjectResponse;
import com.conx.server.user.dto.crew.response.CrewProfileResponse;
import com.conx.server.user.service.common.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.conx.server.global.common.GetOrDefault.getOrDefault;

@Service
@RequiredArgsConstructor
public class CrewMyPageService {

    private final ProjectBookmarkRepository projectBookmarkRepository;
    private final UserFinder userFinder;

    @Transactional(readOnly = true)
    public CrewProfileResponse getProfile(Long crewId) {
        Crew crew = userFinder.findActiveCrew(crewId);
        return CrewProfileResponse.from(crew);
    }

    @Transactional
    public CrewProfileResponse updateProfile(Long crewId, CrewProfileUpdateRequest request) {
        Crew crew = userFinder.findActiveCrew(crewId);

        crew.modifyMyPageProfile(
                getOrDefault(request.profileImage(), crew.getProfileImage()),
                getOrDefault(request.crewName(), crew.getCrewName()),
                getOrDefault(request.crewType(), crew.getCrewType()),
                getOrDefault(request.customCrewType(), crew.getCustomCrewType()),
                getOrDefault(request.managerName(), crew.getManagerName()),
                getOrDefault(request.job(), crew.getJob()),
                getOrDefault(request.crewSchool(), crew.getCrewSchool()),
                getOrDefault(request.memberAmount(), crew.getMemberAmount()),
                getOrDefault(request.crewIntroduction(), crew.getCrewIntroduction()),
                getOrDefault(request.additionalIntroduction(), crew.getAdditionalIntroduction()),
                getOrDefault(request.advantages(), crew.getAdvantages()),
                getOrDefault(request.interestingIndustry(), crew.getInterestingIndustry()),
                getOrDefault(request.snsLink(), crew.getSnsLink()),
                getOrDefault(request.etcLink(), crew.getEtcLink()),
                getOrDefault(request.kakaotalkLink(), crew.getKakaotalkLink())
        );

        return CrewProfileResponse.from(crew);
    }

    @Transactional(readOnly = true)
    public Page<CrewBookmarkedProjectResponse> getBookmarkedProjects(Long crewId, Pageable pageable) {
        Crew crew = userFinder.findActiveCrew(crewId);

        return projectBookmarkRepository.findAllByCrewId(crew.getId(), pageable)
                .map(CrewBookmarkedProjectResponse::from);
    }
}