package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysRole;
import com.example.demo.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class RoleService extends ServiceImpl<SysRoleMapper, SysRole> {
    public Page<SysRole> page(long current, long size, String roleCode, String roleName) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleCode), SysRole::getRoleCode, roleCode)
                .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .orderByAsc(SysRole::getId);
        return page(new Page<>(current, size), wrapper);
    }

    @Transactional
    public Long saveOrUpdateRole(SysRole role) {
        if (role == null || !StringUtils.hasText(role.getRoleCode()) || !StringUtils.hasText(role.getRoleName())) {
            throw new ApiException(400, "角色编码和名称不能为空");
        }
        role.setRoleCode(role.getRoleCode().trim());
        role.setRoleName(role.getRoleName().trim());
        long duplicate = lambdaQuery().eq(SysRole::getRoleCode, role.getRoleCode())
                .ne(role.getId() != null, SysRole::getId, role.getId()).count();
        if (duplicate > 0) {
            throw new ApiException(400, "角色编码已存在");
        }
        saveOrUpdate(role);
        return role.getId();
    }
}
