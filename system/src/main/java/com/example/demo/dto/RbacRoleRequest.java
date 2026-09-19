package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "RBAC 角色请求")
public class RbacRoleRequest {
    @Schema(description = "角色 ID，新增时为空")
    private Long id;

    @NotBlank
    @Size(max = 32)
    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleCode;

    @NotBlank
    @Size(max = 32)
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @Size(max = 32)
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "权限 ID 列表")
    private List<Long> permissionIds;
}
