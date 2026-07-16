package com.conx.server.user.dto.company.response;

import java.util.List;

public record SubsidyStatusResponse(
    SubsidyStatusWrapperDTO subsidyStatus,
    List<AdjustmentWrapperDTO> adjustmentList
) {
}
