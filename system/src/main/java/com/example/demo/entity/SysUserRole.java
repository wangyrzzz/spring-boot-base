package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
@Schema(description = "用户角色关联")
public class SysUserRole extends BaseEntity {
    @Schema(description = "用户 ID")
    private Long userId;
    @Schema(description = "角色 ID")
    private Long roleId;
}
