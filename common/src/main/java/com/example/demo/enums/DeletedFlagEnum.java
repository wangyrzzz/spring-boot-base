package com.example.demo.enums;

public enum DeletedFlagEnum {
    NORMAL(0),
    DELETED(1);

    private final int code;

    DeletedFlagEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
