package com.conx.server.user.dto.company.request;

import jakarta.validation.constraints.NotBlank;

public record CompanyProjectRevisionRequest(
        @NotBlank(message = "프로젝트 수정요청 사유를 입력해주세요")
        String revisionReason
) {
}