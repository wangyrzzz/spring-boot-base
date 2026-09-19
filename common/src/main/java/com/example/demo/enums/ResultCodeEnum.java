package com.example.demo.enums;

import lombok.Getter;

/**
 * @Author: WangYuanrong
 * @Date: 2021/4/7 11:10
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(0,"处理成功"),

    FAIL(400,"请求无效"),

    UNAUTHORIZED(401,"请求未授权"),

    ERROR(500,"系统繁忙")
    ;

    private final Integer code;

    private final String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
