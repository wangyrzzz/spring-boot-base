package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_scope")
@Schema(description = "角色数据权限关联")
public class SysRoleScope extends BaseEntity {
    @Schema(description = "角色 ID")
    private Long roleId;
    @Schema(description = "数据权限规则 ID")
    private Long scopeId;
    @Schema(description = "规则优先级，越小越优先")
    private Integer priority;
}
