package com.conx.server.project.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultForm {

    //결과물
    //플랫폼
    private String platform;

    //콘텐츠유형
    private String contentType;

    //개수
    private int numberOfResult;

    //최종 제출물
    private String finalResult;

    public static ResultForm from(ResultFormRequestDTO req){
        return new ResultForm(
                req.platform(), req.contentType(), req.numberOfResult(), req.finalResult()
        );
    }

    public static ResultForm from(String platform, String contentType, int numberOfResult, String finalResult){
        return new ResultForm(
                platform, contentType, numberOfResult, finalResult
        );
    }
}
