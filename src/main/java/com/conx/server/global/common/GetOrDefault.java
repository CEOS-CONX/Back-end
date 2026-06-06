package com.conx.server.global.common;

public class GetOrDefault {
    public static <T> T getOrDefault(T newValue, T currentValue) {
        if (newValue == null) {
            return currentValue;
        }

        return newValue;
    }
}