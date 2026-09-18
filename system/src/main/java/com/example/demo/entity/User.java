package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户
 * @author WangYuanrong
 * @since 2021-06-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user")
@Schema(description="用户")
public class User extends BaseEntity {

    private String username;

    private String realName;

    private Integer gender;

    private String mobile;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String avatar;

    private Long deptId;

    private Integer status;

    private String remark;

    @TableLogic
    private Boolean deleted;


}
