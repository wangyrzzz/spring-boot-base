package com.example.demo.common;

import com.example.demo.enums.ResultCodeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 统一异常定义
 * @Author: WangYuanrong
 * @Date: 2021/4/7 11:04
 */
@Getter
@Setter
public class ApiException extends RuntimeException {

    private Integer code;
    private String message;

    public ApiException(String message) {
        this.code = ResultCodeEnum.ERROR.getCode();
        this.message = message;
    }

    public ApiException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiException(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMsg();
    }
}
