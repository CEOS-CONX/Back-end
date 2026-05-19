package com.conx.server.User.Domain.Enum;

import lombok.Getter;

@Getter
public enum Industry {
    BEAUTY("뷰티"),
    FASHION("패션"),
    IT("IT 플랫폼"),
    CAREER("커리어"),
    FANDB("F%D"),
    LIFESTYLE("라이프스타일"),
    ENTERTAIN("엔터테인먼트"),
    ETC("기타");

    private final String industry;

    Industry(String industry){
        this.industry = industry;
    }
}