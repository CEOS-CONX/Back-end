package com.conx.server.notification.service;

import com.conx.server.global.security.userDetails.CustomUserDetails;
import com.conx.server.notification.domain.Notification;
import com.conx.server.notification.domain.NotificationType;
import com.conx.server.notification.dto.NotificationFilter;
import com.conx.server.notification.dto.NotificationWrapperDTO;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.user.domain.User;
import com.conx.server.user.service.common.UserFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final long USER_ID = 1L;
    private static final String USER_EMAIL = "user@example.com";

    @Mock
    private UserFinder userFinder;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private User user;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService =
                new NotificationService(
                        userFinder,
                        notificationRepository
                );

        given(customUserDetails.getUserEmail())
                .willReturn(USER_EMAIL);

        given(userFinder.findByEmail(USER_EMAIL))
                .willReturn(user);

        given(user.getId())
                .willReturn(USER_ID);
    }

    @Test
    void 전체_알림_조회_시_대상_ID를_반환한다() {
        Notification notification =
                Notification.create(
                        NotificationType.QUESTION_ANSWER_REGISTERED,
                        USER_ID,
                        "문의에 답변이 등록되었습니다.",
                        "기업명",
                        10L,
                        20L,
                        null,
                        null,
                        null
                );

        given(
                notificationRepository.findAllByReceiverId(
                        USER_ID
                )
        ).willReturn(
                List.of(notification)
        );

        List<NotificationWrapperDTO> result =
                notificationService.getAllNotifications(
                        customUserDetails,
                        NotificationFilter.ALL
                );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).type())
                .isEqualTo(
                        NotificationType.QUESTION_ANSWER_REGISTERED
                );
        assertThat(result.get(0).projectId())
                .isEqualTo(10L);
        assertThat(result.get(0).questionId())
                .isEqualTo(20L);
        assertThat(result.get(0).applicationId())
                .isNull();
        assertThat(result.get(0).submissionId())
                .isNull();
        assertThat(result.get(0).settlementId())
                .isNull();
    }

    @Test
    void 프로젝트_알림_조회_시_지원서와_결과물_ID를_반환한다() {
        Notification selectedNotification =
                Notification.create(
                        NotificationType.PROJECT_SELECTED,
                        USER_ID,
                        "프로젝트에 선정되었습니다.",
                        "기업명",
                        10L,
                        null,
                        30L,
                        null,
                        null
                );

        Notification resultNotification =
                Notification.create(
                        NotificationType.RESULT_UPLOADED,
                        USER_ID,
                        "결과물이 등록되었습니다.",
                        "크루명",
                        10L,
                        null,
                        null,
                        40L,
                        null
                );

        given(
                notificationRepository
                        .findAllByReceiverIdAndTypeIn(
                                USER_ID,
                                List.of(
                                        NotificationType.ADJUSTMENT_DONE,
                                        NotificationType.LATE_FOR_SUBMIT_DEADLINE,
                                        NotificationType.CLOSE_TO_END_OF_RECRUITING,
                                        NotificationType.PROJECT_CLOSE_TO_END,
                                        NotificationType.PROJECT_REJECTED,
                                        NotificationType.PROJECT_SELECTED,
                                        NotificationType.RESULT_UPLOAD_CLOSE_TO_END,
                                        NotificationType.RESULT_UPLOADED
                                )
                        )
        ).willReturn(
                List.of(
                        selectedNotification,
                        resultNotification
                )
        );

        List<NotificationWrapperDTO> result =
                notificationService.getAllNotifications(
                        customUserDetails,
                        NotificationFilter.PROJECT
                );

        assertThat(result).hasSize(2);

        assertThat(result.get(0).projectId())
                .isEqualTo(10L);
        assertThat(result.get(0).applicationId())
                .isEqualTo(30L);

        assertThat(result.get(1).projectId())
                .isEqualTo(10L);
        assertThat(result.get(1).submissionId())
                .isEqualTo(40L);
    }

    @Test
    void 기존_알림도_대상_ID가_null인_상태로_조회된다() {
        Notification notification =
                Notification.create(
                        NotificationType.PROJECT_CLOSE_TO_END,
                        USER_ID,
                        "프로젝트 마감이 임박했습니다.",
                        "기업명"
                );

        given(
                notificationRepository.findAllByReceiverId(
                        USER_ID
                )
        ).willReturn(
                List.of(notification)
        );

        List<NotificationWrapperDTO> result =
                notificationService.getAllNotifications(
                        customUserDetails,
                        NotificationFilter.ALL
                );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).projectId())
                .isNull();
        assertThat(result.get(0).questionId())
                .isNull();
        assertThat(result.get(0).applicationId())
                .isNull();
        assertThat(result.get(0).submissionId())
                .isNull();
        assertThat(result.get(0).settlementId())
                .isNull();
    }
}
