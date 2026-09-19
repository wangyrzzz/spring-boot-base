package com.example.demo.common;

public record RbacPermissionView(Long id, Long parentId, Integer menuType, String name, String code,
                                 String path, Integer sort, String remark) {
}
