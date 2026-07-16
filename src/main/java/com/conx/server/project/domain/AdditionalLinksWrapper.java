package com.conx.server.project.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdditionalLinksWrapper {
    String linkName;
    String link;
    String explanation;

    public static AdditionalLinksWrapper from(AdditionalLinksRequestDTO req){
        return new AdditionalLinksWrapper(req.linkName(), req.link(), req.explanation());
    }
}
