package com.example.demo.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 登录用户数据对象
 * @Author: WangYuanrong
 * @Date: 2022/4/8 14:54
 */
@Data
@Schema(description = "登录用户信息")
public class LoginUserDTO implements Serializable {

    /**
     * 用户id
     */
    @Schema(description = "用户 ID")
    private Long userId;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String username;

    /**
     * 姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

}
