package com.example.demo.query;

import com.example.demo.common.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yanhongwei
 * @Date: 2023-03-06  16:50
 */
@Data
@Schema(description = "用户分页查询参数")
public class UserQuery extends PageParam {


    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "性别：1男，2女")
    private Integer gender;

    @Schema(description = "手机号")
    private String mobile;
}
