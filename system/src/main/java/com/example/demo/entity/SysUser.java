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

    @Schema(description = "用户名")
    private String username;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "性别：1男，2女")
    private Integer gender;
    @Schema(description = "手机号")
    private String mobile;
    @Schema(description = "邮箱")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "密码", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "头像地址")
    private String avatar;
    @Schema(description = "状态：0冻结，1正常")
    private Integer status;
    @Schema(description = "备注")
    private String remark;

    @TableLogic(value = "0", delval = "1")
    @Schema(description = "软删除：0正常，1已删除")
    private Integer deleted;
}
