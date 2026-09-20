package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysUserRole;
import com.example.demo.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRoleService extends ServiceImpl<SysUserRoleMapper, SysUserRole> {
    public List<Long> roleIds(Long userId) {
        return lambdaQuery().eq(SysUserRole::getUserId, userId)
                .orderByAsc(SysUserRole::getId).list().stream().map(SysUserRole::getRoleId).toList();
    }

    @Transactional
    public void replace(Long userId, List<Long> roleIds) {
        remove(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        List<SysUserRole> rows = new ArrayList<>();
        for (Long roleId : roleIds.stream().filter(java.util.Objects::nonNull).distinct().toList()) {
            SysUserRole row = new SysUserRole();
            row.setUserId(userId);
            row.setRoleId(roleId);
            rows.add(row);
        }
        saveBatch(rows);
    }
}
