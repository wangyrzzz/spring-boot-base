package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户角色授权请求")
public class RbacUserRoleRequest {
    @NotNull
    @Schema(description = "用户 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "角色 ID 列表")
    private List<Long> roleIds;
}
