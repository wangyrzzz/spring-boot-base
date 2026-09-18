package com.example.demo.common;

public enum DataScopeEnum {
    ALL(1), OWN(2), OWN_DEPT(3), OWN_DEPT_CHILD(4), CUSTOM(5);

    private final int code;

    DataScopeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static DataScopeEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (DataScopeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
