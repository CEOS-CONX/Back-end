package com.conx.server.global.common;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public final class EmailNormalizer {

    private EmailNormalizer() {}

    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}