package com.conx.server.global.apiResponse;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class Meta {

    private final int page;
    private final int size;
    private final boolean hasNext;

    public Meta(int page, int size, boolean hasNext) {
        this.page = page;
        this.size = size;
        this.hasNext = hasNext;
    }

    public static Meta from(Page<?> page) {
        return new Meta(
                page.getNumber(),
                page.getSize(),
                page.hasNext()
        );
    }
}