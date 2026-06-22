package com.conx.server.landingPage.dto;

import com.conx.server.global.exception.CustomException;
import com.conx.server.global.exception.ErrorCode;
import com.conx.server.user.domain.types.Industry;
import lombok.Getter;

@Getter
public enum IndustryForLandingPage {
    ALL("전체"),
    BEAUTY("뷰티"),
    FASHION("패션"),
    IT("IT 플랫폼"),
    CAREER("커리어"),
    FANDB("F%D"),
    LIFESTYLE("라이프스타일"),
    ENTERTAIN("엔터테인먼트"),
    ETC("기타");

    private final String industry;

    IndustryForLandingPage(String industry){
        this.industry = industry;
    }

    public Industry toIndustry(){
        if (this == ALL){
            throw new CustomException(ErrorCode.INVALID_CATEGORY);
        }

        return Industry.valueOf(this.name());
    }
}