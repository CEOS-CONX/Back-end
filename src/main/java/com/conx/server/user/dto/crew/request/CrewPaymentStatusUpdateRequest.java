package com.conx.server.user.dto.crew.request;

import com.conx.server.project.domain.enums.CrewPaymentStatus;
import jakarta.validation.constraints.NotNull;

public record CrewPaymentStatusUpdateRequest(

        @NotNull
        CrewPaymentStatus paymentStatus

) {
}