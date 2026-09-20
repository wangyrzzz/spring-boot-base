package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysRoleScope;
import com.example.demo.mapper.SysRoleScopeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleScopeService extends ServiceImpl<SysRoleScopeMapper, SysRoleScope> {
    @Transactional
    public void replace(Long roleId, List<Long> scopeIds) {
        remove(new LambdaQueryWrapper<SysRoleScope>().eq(SysRoleScope::getRoleId, roleId));
        if (scopeIds == null || scopeIds.isEmpty()) {
            return;
        }
        List<SysRoleScope> rows = new ArrayList<>();
        for (Long scopeId : scopeIds.stream().filter(java.util.Objects::nonNull).distinct().toList()) {
            SysRoleScope row = new SysRoleScope();
            row.setRoleId(roleId);
            row.setScopeId(scopeId);
            row.setPriority(100);
            rows.add(row);
        }
        saveBatch(rows);
    }
}
