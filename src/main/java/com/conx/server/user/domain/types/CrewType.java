package com.conx.server.user.domain.types;

import lombok.Getter;

@Getter
public enum CrewType {
    ACADEMY("학회"),
    SMALLMEETING("소모임"),
    CLUB("동아리"),
    COUNCIL("학생회"),
    ETC("기타");

    private final String crewType;

    CrewType(String crewType){
        this.crewType = crewType;
    }
}