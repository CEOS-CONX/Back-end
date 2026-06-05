package com.conx.server.user.service.common;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.project.domain.Project;
import com.conx.server.project.repository.ProjectRepository;
import com.conx.server.user.domain.company.Company;
import com.conx.server.user.domain.crew.Crew;
import com.conx.server.user.domain.types.UserStatus;
import com.conx.server.user.domain.email.EmailViewLog;
import com.conx.server.user.dto.UserRole;
import com.conx.server.user.dto.email.EmailTargetType;
import com.conx.server.user.dto.email.EmailViewRequest;
import com.conx.server.user.dto.email.EmailViewResponse;
import com.conx.server.user.repository.CompanyRepository;
import com.conx.server.user.repository.CrewRepository;
import com.conx.server.user.repository.EmailViewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailViewService {

    private static final String CONSENT_TEXT =
            "이메일 정보를 조회하면 상대방에게 연락하기 위한 목적으로 개인정보를 확인하는 것에 동의한 것으로 간주됩니다.";

    private final ProjectRepository projectRepository;
    private final EmailViewLogRepository emailViewLogRepository;
    private final UserFinder userFinder;
    private final CompanyRepository companyRepository;
    private final CrewRepository crewRepository;

    @Transactional
    public EmailViewResponse viewEmail(
            CustomUserDetails customUserDetails,
            EmailViewRequest request
    ) {
        if (!request.consentAgreed()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        ViewerInfo viewerInfo = findViewer(customUserDetails.getUserEmail());

        if (request.targetType() == EmailTargetType.CREW) {
            return viewCrewEmail(viewerInfo, request.targetId());
        }

        if (request.targetType() == EmailTargetType.PROJECT) {
            return viewProjectCompanyEmail(viewerInfo, request.targetId());
        }

        throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
    }

    private EmailViewResponse viewCrewEmail(
            ViewerInfo viewerInfo,
            Long crewId
    ) {
        Crew crew = userFinder.findActiveCrew(crewId);

        saveEmailViewLog(
                viewerInfo,
                EmailTargetType.CREW,
                crew.getId(),
                crew.getCrewName(),
                crew.getEmail()
        );

        return EmailViewResponse.of(
                EmailTargetType.CREW,
                crew.getId(),
                crew.getCrewName(),
                crew.getEmail()
        );
    }

    private EmailViewResponse viewProjectCompanyEmail(
            ViewerInfo viewerInfo,
            Long projectId
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        Company company = project.getCompany();

        saveEmailViewLog(
                viewerInfo,
                EmailTargetType.PROJECT,
                project.getId(),
                company.getCompanyName(),
                company.getEmail()
        );

        return EmailViewResponse.of(
                EmailTargetType.PROJECT,
                project.getId(),
                company.getCompanyName(),
                company.getEmail()
        );
    }

    private void saveEmailViewLog(
            ViewerInfo viewerInfo,
            EmailTargetType targetType,
            Long targetId,
            String targetName,
            String viewedEmail
    ) {
        EmailViewLog emailViewLog = EmailViewLog.create(
                viewerInfo.viewerId(),
                viewerInfo.viewerRole(),
                viewerInfo.viewerEmail(),
                targetType,
                targetId,
                targetName,
                viewedEmail,
                true,
                CONSENT_TEXT
        );

        emailViewLogRepository.save(emailViewLog);
    }

    private ViewerInfo findViewer(String email) {
        return companyRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .map(company -> new ViewerInfo(
                        company.getId(),
                        UserRole.COMPANY,
                        company.getEmail()
                ))
                .orElseGet(() -> {
                    Crew crew = crewRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                            .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

                    return new ViewerInfo(
                            crew.getId(),
                            UserRole.CREW,
                            crew.getEmail()
                    );
                });
    }

    private record ViewerInfo(
            Long viewerId,
            UserRole viewerRole,
            String viewerEmail
    ) {
    }
}