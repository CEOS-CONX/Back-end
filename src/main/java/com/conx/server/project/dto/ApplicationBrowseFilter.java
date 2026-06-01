package com.conx.server.project.dto;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.project.domain.enums.ProjectApplicationStatus;

public enum ApplicationBrowseFilter {
    PENDING,
    SELECTED,
    REJECTED,
    ALL;

    public ProjectApplicationStatus toApplicationStatus(){
        if (this == ALL){
            throw new CustomException(ErrorCode.INVALID_CATEGORY);
        } else {
            return ProjectApplicationStatus.valueOf(this.name());
        }
    }
}
