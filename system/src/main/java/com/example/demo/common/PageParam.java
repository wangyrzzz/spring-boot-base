package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询入参
 * @Author: WangYuanrong
 * @Date: 2022/1/27 14:34
 */
@Schema(description = "分页查询入参")
@Data
public class PageParam {

    /**
     * 当前页，从1开始
     */
    @Schema(description = "当前页，从1开始")
    protected int page = 1;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数")
    protected int limit = 10;

}
