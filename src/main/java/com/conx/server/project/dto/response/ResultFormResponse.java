package com.conx.server.project.dto.response;

import com.conx.server.project.domain.ResultForm;

public record ResultFormResponse(
        String platform,
        String contentType,
        int numberOfResult,
        String finalResult
) {
    public static ResultFormResponse from(ResultForm resultForm) {
        return new ResultFormResponse(
                resultForm.getPlatform(),
                resultForm.getContentType(),
                resultForm.getNumberOfResult(),
                resultForm.getFinalResult()
        );
    }
}
