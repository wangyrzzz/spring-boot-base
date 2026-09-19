package com.example.demo.common;

public record RbacPermissionView(Long id, Long parentId, Integer menuType, String name, String code,
                                 String permPath, Integer sort, String remark) {
}
