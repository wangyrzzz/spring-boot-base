package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "RBAC 授权请求")
public class RbacGrantRequest {
    @NotEmpty
    @Schema(description = "角色 ID 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;

    @Schema(description = "权限 ID 列表")
    private List<Long> permissionIds;
}
