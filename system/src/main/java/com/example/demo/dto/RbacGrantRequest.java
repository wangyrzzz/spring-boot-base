package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RbacGrantRequest {
    @NotEmpty
    private List<Long> roleIds;

    private List<Long> permissionIds;
}
