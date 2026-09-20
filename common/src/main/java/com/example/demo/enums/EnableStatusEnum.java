package com.example.demo.enums;

public enum EnableStatusEnum {
    DISABLED(0),
    ENABLED(1);

    private final int code;

    EnableStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
