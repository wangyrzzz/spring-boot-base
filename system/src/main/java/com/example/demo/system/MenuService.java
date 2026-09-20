package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysMenu;
import com.example.demo.enums.MenuTypeEnum;
import com.example.demo.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MenuService extends ServiceImpl<SysMenuMapper, SysMenu> {
    public Page<SysMenu> page(long current, long size, String code, String name, Integer menuType) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>()
                .like(StringUtils.hasText(code), SysMenu::getCode, code)
                .like(StringUtils.hasText(name), SysMenu::getName, name)
                .eq(menuType != null, SysMenu::getMenuType, menuType)
                .orderByAsc(SysMenu::getSort).orderByAsc(SysMenu::getId);
        return page(new Page<>(current, size), wrapper);
    }

    public List<SysMenu> select(Integer menuType) {
        return lambdaQuery().eq(menuType != null, SysMenu::getMenuType, menuType)
                .orderByAsc(SysMenu::getSort).orderByAsc(SysMenu::getId).list();
    }

    @Transactional
    public Long saveOrUpdateMenu(SysMenu menu) {
        if (menu == null || !StringUtils.hasText(menu.getName())) {
            throw new ApiException(400, "菜单名称不能为空");
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getMenuType() == null) {
            menu.setMenuType(MenuTypeEnum.MENU.getCode());
        }
        saveOrUpdate(menu);
        return menu.getId();
    }
}
