package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户数据对象
 * @Author: WangYuanrong
 * @Date: 2022/4/8 14:54
 */
@Data
public class LoginUserDTO implements Serializable {

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 姓名
     */
    private String realName;

}
