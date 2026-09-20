package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_menu")
@Schema(description = "角色菜单关联")
public class SysRoleMenu extends BaseEntity {
    @Schema(description = "角色 ID")
    private Long roleId;
    @Schema(description = "菜单权限 ID")
    private Long menuId;
}
