package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "RBAC 角色视图")
public record RbacRoleView(
        @Schema(description = "角色 ID") Long id,
        @Schema(description = "角色编码") String roleCode,
        @Schema(description = "角色名称") String roleName,
        @Schema(description = "备注") String remark,
        @Schema(description = "权限 ID 列表") List<Long> permissionIds) {
}
