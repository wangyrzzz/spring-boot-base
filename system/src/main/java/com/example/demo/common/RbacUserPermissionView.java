package com.example.demo.common;

import java.util.List;

public record RbacUserPermissionView(List<String> roleCodes, List<String> permissionCodes) {
}
