package com.conx.server.user.controller.common;

import com.conx.server.global.common.ApiResponseFactory;
import com.conx.server.notification.repository.NotificationRepository;
import com.conx.server.user.dto.passwordReset.request.PasswordResetRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationConfirmRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationSendRequest;
import com.conx.server.user.dto.passwordReset.response.PasswordResetVerificationConfirmResponse;
import com.conx.server.user.service.common.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PasswordResetControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        ApiResponseFactory apiResponseFactory =
                new ApiResponseFactory(
                        notificationRepository
                );

        PasswordResetController controller =
                new PasswordResetController(
                        passwordResetService,
                        apiResponseFactory
                );

        objectMapper =
                new ObjectMapper();

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(controller)
                        .setMessageConverters(
                                new MappingJackson2HttpMessageConverter(
                                        objectMapper
                                )
                        )
                        .build();
    }

    @Test
    @DisplayName("비밀번호 재설정 인증번호 발송을 요청한다")
    void sendVerificationCode() throws Exception {
        PasswordResetVerificationSendRequest request =
                new PasswordResetVerificationSendRequest(
                        "담당자",
                        "company@test.com"
                );

        mockMvc.perform(
                        post(
                                "/api/v1/auth/password/verifications"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "입력한 정보와 일치하는 계정이 있으면 인증번호가 발송됩니다."
                                )
                );

        verify(passwordResetService)
                .sendVerificationCode(
                        eq(request)
                );
    }

    @Test
    @DisplayName("비밀번호 재설정 인증번호를 확인한다")
    void confirmVerificationCode() throws Exception {
        PasswordResetVerificationConfirmRequest request =
                new PasswordResetVerificationConfirmRequest(
                        "company@test.com",
                        123456
                );

        PasswordResetVerificationConfirmResponse response =
                new PasswordResetVerificationConfirmResponse(
                        "reset-token"
                );

        given(
                passwordResetService
                        .confirmVerificationCode(
                                eq(request)
                        )
        ).willReturn(response);

        mockMvc.perform(
                        post(
                                "/api/v1/auth/password/verifications/confirm"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "비밀번호 재설정 이메일 인증에 성공했습니다."
                                )
                )
                .andExpect(
                        jsonPath("$.payload.resetToken")
                                .value("reset-token")
                );

        verify(passwordResetService)
                .confirmVerificationCode(
                        eq(request)
                );
    }

    @Test
    @DisplayName("인증 완료 후 비밀번호를 재설정한다")
    void resetPassword() throws Exception {
        PasswordResetRequest request =
                new PasswordResetRequest(
                        "reset-token",
                        "new-password",
                        "new-password"
                );

        mockMvc.perform(
                        patch(
                                "/api/v1/auth/password"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("success")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "비밀번호가 재설정되었습니다."
                                )
                );

        verify(passwordResetService)
                .resetPassword(
                        eq(request)
                );
    }
}