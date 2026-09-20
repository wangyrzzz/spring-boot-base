package com.example.demo.enums;

public enum SuccessFlagEnum {
    FAILURE(0),
    SUCCESS(1);

    private final int code;

    SuccessFlagEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
