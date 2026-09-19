package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RBAC 权限视图")
public record RbacPermissionView(
        @Schema(description = "权限 ID") Long id,
        @Schema(description = "父级权限 ID") Long parentId,
        @Schema(description = "菜单类型") Integer menuType,
        @Schema(description = "权限名称") String name,
        @Schema(description = "权限编码") String code,
        @Schema(description = "路由路径") String path,
        @Schema(description = "排序") Integer sort,
        @Schema(description = "备注") String remark) {
}
