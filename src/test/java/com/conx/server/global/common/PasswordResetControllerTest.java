package com.conx.server.global.common;

import com.conx.server.user.controller.common.PasswordResetController;
import com.conx.server.user.dto.passwordReset.request.PasswordResetRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationConfirmRequest;
import com.conx.server.user.dto.passwordReset.request.PasswordResetVerificationSendRequest;
import com.conx.server.user.dto.passwordReset.response.PasswordResetVerificationConfirmResponse;
import com.conx.server.user.service.common.PasswordResetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("local")
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(PasswordResetController.class)
class PasswordResetControllerTest {

    private static final String BASE_URL = "/api/v1/auth/password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private ApiResponseFactory apiResponseFactory;

    @Nested
    @DisplayName("POST /verifications - 인증번호 발송")
    class SendVerificationCode {

        @Test
        @DisplayName("유효한 이름/이메일이면 200과 공통 성공 메시지를 반환한다")
        void success() throws Exception {
            PasswordResetVerificationSendRequest request =
                    new PasswordResetVerificationSendRequest("홍길동", "test@example.com");

            when(apiResponseFactory.success(eq("입력한 정보와 일치하는 계정이 있으면 인증번호가 발송됩니다."), isNull()))
                    .thenReturn(new ApiResponse<>("success",
                            "입력한 정보와 일치하는 계정이 있으면 인증번호가 발송됩니다.", null, null));

            mockMvc.perform(post(BASE_URL + "/verifications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.message")
                            .value("입력한 정보와 일치하는 계정이 있으면 인증번호가 발송됩니다."))
                    .andExpect(jsonPath("$.payload").doesNotExist());

            verify(passwordResetService, times(1)).sendVerificationCode(request);
        }

        @Test
        @DisplayName("이름이 비어있으면 400을 반환하고 서비스는 호출되지 않는다")
        void blankName() throws Exception {
            PasswordResetVerificationSendRequest request =
                    new PasswordResetVerificationSendRequest("", "test@example.com");

            mockMvc.perform(post(BASE_URL + "/verifications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(passwordResetService, times(0)).sendVerificationCode(any());
        }

        @Test
        @DisplayName("이메일 형식이 올바르지 않으면 400을 반환한다")
        void invalidEmailFormat() throws Exception {
            PasswordResetVerificationSendRequest request =
                    new PasswordResetVerificationSendRequest("홍길동", "not-an-email");

            mockMvc.perform(post(BASE_URL + "/verifications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(passwordResetService, times(0)).sendVerificationCode(any());
        }
    }

    @Nested
    @DisplayName("POST /verifications/confirm - 인증번호 확인")
    class ConfirmVerificationCode {

        @Test
        @DisplayName("이메일/인증번호가 일치하면 resetToken을 포함한 200을 반환한다")
        void success() throws Exception {
            PasswordResetVerificationConfirmRequest request =
                    new PasswordResetVerificationConfirmRequest("test@example.com", 123456);
            PasswordResetVerificationConfirmResponse serviceResponse =
                    new PasswordResetVerificationConfirmResponse("reset-token-value");

            when(passwordResetService.confirmVerificationCode(request)).thenReturn(serviceResponse);
            when(apiResponseFactory.success(
                    eq("비밀번호 재설정 이메일 인증에 성공했습니다."), eq(serviceResponse), isNull()))
                    .thenReturn(new ApiResponse<>("success",
                            "비밀번호 재설정 이메일 인증에 성공했습니다.", serviceResponse, null));

            mockMvc.perform(post(BASE_URL + "/verifications/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.payload.resetToken").value("reset-token-value"));

            verify(passwordResetService, times(1)).confirmVerificationCode(request);
        }

        @Test
        @DisplayName("인증번호가 null이면 400을 반환한다")
        void missingCode() throws Exception {
            String body = """
                    {"email":"test@example.com","code":null}
                    """;

            mockMvc.perform(post(BASE_URL + "/verifications/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());

            verify(passwordResetService, times(0)).confirmVerificationCode(any());
        }

        @Test
        @DisplayName("이메일 형식이 올바르지 않으면 400을 반환한다")
        void invalidEmailFormat() throws Exception {
            PasswordResetVerificationConfirmRequest request =
                    new PasswordResetVerificationConfirmRequest("not-an-email", 123456);

            mockMvc.perform(post(BASE_URL + "/verifications/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(passwordResetService, times(0)).confirmVerificationCode(any());
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/auth/password - 비밀번호 재설정")
    class ResetPassword {

        @Test
        @DisplayName("유효한 요청이면 200과 성공 메시지를 반환한다")
        void success() throws Exception {
            PasswordResetRequest request =
                    new PasswordResetRequest("reset-token-value", "newPassword1!", "newPassword1!");

            when(apiResponseFactory.success(eq("비밀번호가 재설정되었습니다."), isNull()))
                    .thenReturn(new ApiResponse<>("success", "비밀번호가 재설정되었습니다.", null, null));

            mockMvc.perform(patch(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.message").value("비밀번호가 재설정되었습니다."));

            verify(passwordResetService, times(1)).resetPassword(request);
        }

        @Test
        @DisplayName("resetToken이 비어있으면 400을 반환한다")
        void blankResetToken() throws Exception {
            PasswordResetRequest request =
                    new PasswordResetRequest("", "newPassword1!", "newPassword1!");

            mockMvc.perform(patch(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(passwordResetService, times(0)).resetPassword(any());
        }

        @Test
        @DisplayName("새 비밀번호 확인값이 비어있으면 400을 반환한다")
        void blankNewPasswordConfirmation() throws Exception {
            PasswordResetRequest request =
                    new PasswordResetRequest("reset-token-value", "newPassword1!", "");

            mockMvc.perform(patch(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(passwordResetService, times(0)).resetPassword(any());
        }
    }
}