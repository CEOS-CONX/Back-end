package com.conx.server.user.dto.email;

public record EmailViewRequest(
        EmailTargetType targetType,
        Long targetId,
        boolean consentAgreed
) {
}