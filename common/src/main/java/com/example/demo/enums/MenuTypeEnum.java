package com.example.demo.enums;

public enum MenuTypeEnum {
    MENU(1),
    BUTTON(2);

    private final int code;

    MenuTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
