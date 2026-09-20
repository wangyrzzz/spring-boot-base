package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysRoleMenu;
import com.example.demo.mapper.SysRoleMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleMenuService extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> {
    public List<Long> menuIds(Long roleId) {
        return lambdaQuery().eq(SysRoleMenu::getRoleId, roleId)
                .orderByAsc(SysRoleMenu::getId).list().stream().map(SysRoleMenu::getMenuId).distinct().toList();
    }

    @Transactional
    public void replace(Long roleId, List<Long> menuIds) {
        remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        List<SysRoleMenu> rows = new ArrayList<>();
        for (Long menuId : menuIds.stream().filter(java.util.Objects::nonNull).distinct().toList()) {
            SysRoleMenu row = new SysRoleMenu();
            row.setRoleId(roleId);
            row.setMenuId(menuId);
            rows.add(row);
        }
        saveBatch(rows);
    }
}
