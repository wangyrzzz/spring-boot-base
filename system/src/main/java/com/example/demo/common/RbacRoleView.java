package com.example.demo.common;

import java.util.List;

public record RbacRoleView(Long id, String roleCode, String roleName, String remark, List<Long> permissionIds) {
}
