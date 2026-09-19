package com.example.demo.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "用户权限视图")
public record RbacUserPermissionView(
        @Schema(description = "角色编码列表") List<String> roleCodes,
        @Schema(description = "权限编码列表") List<String> permissionCodes) {
}
