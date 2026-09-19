package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RbacUserRoleRequest {
    @NotNull
    private Long userId;

    private List<Long> roleIds;
}
