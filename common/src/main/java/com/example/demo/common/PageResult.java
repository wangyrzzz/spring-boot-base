package com.example.demo.common;

import com.example.demo.enums.ResultCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: yanhongwei
 * @Date: 2023-03-06  18:40
 */
@Getter
@Setter
@Schema(description = "分页响应")
public class PageResult<T> extends Result<T> implements Serializable {

    @Schema(description = "总记录数")
    private Long count;


    public static <T> PageResult<T> ok(T data, Long count) {
        PageResult<T> res = new PageResult<>();
        res.setSuccess(Boolean.TRUE);
        res.setCode(ResultCodeEnum.SUCCESS.getCode());
        res.setMsg("");
        res.setData(data);
        res.setCount(count);
        return res;
    }
}
