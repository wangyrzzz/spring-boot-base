package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user")
@Schema(description = "用户")
public class SysUser extends BaseEntity {

    private String username;
    private String realName;
    private Integer gender;
    private String mobile;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String avatar;
    private Integer status;
    private String remark;

    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
