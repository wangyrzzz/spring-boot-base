package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RbacRoleRequest {
    private Long id;

    @NotBlank
    @Size(max = 32)
    private String roleCode;

    @NotBlank
    @Size(max = 32)
    private String roleName;

    @Size(max = 32)
    private String remark;

    private List<Long> permissionIds;
}
