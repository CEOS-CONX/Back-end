package com.conx.server.user.dto.email;

public record EmailViewResponse(
        EmailTargetType targetType,
        Long targetId,
        String name,
        String email
) {
    public static EmailViewResponse of(
            EmailTargetType targetType,
            Long targetId,
            String name,
            String email
    ) {
        return new EmailViewResponse(
                targetType,
                targetId,
                name,
                email
        );
    }
}