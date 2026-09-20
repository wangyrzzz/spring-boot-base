package com.example.demo.enums;

public enum ExportTaskStatusEnum {
    PENDING(0),
    RUNNING(1),
    COMPLETED(2),
    FAILED(3);

    private final int code;

    ExportTaskStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
