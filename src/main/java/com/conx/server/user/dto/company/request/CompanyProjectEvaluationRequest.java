package com.conx.server.user.dto.company.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CompanyProjectEvaluationRequest(

        @NotNull(message = "완성도 점수를 입력해주세요.")
        @Min(value = 1, message = "평가 점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평가 점수는 5점 이하여야 합니다.")
        Integer completeness,

        @NotNull(message = "일정 준수 점수를 입력해주세요.")
        @Min(value = 1, message = "평가 점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평가 점수는 5점 이하여야 합니다.")
        Integer schedule,

        @NotNull(message = "업무 역량 점수를 입력해주세요.")
        @Min(value = 1, message = "평가 점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평가 점수는 5점 이하여야 합니다.")
        Integer ability,

        @NotNull(message = "재협업 의사 점수를 입력해주세요.")
        @Min(value = 1, message = "평가 점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평가 점수는 5점 이하여야 합니다.")
        Integer reCooperation,

        @NotNull(message = "의사소통 점수를 입력해주세요.")
        @Min(value = 1, message = "평가 점수는 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평가 점수는 5점 이하여야 합니다.")
        Integer communication
) {
}