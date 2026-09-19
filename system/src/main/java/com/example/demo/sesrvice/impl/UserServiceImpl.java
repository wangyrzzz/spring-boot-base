package com.example.demo.sesrvice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysUser;
import com.example.demo.mapper.SysUserMapper;
import com.example.demo.query.UserQuery;
import com.example.demo.sesrvice.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author WangYuanrong
 * @since 2021-06-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements IUserService {

    @Override
    public Page<SysUser> page(UserQuery userQuery) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.isNotBlank(userQuery.getUsername()), SysUser::getUsername, userQuery.getUsername())
                .like(StringUtils.isNotBlank(userQuery.getRealName()), SysUser::getRealName, userQuery.getRealName())
                .eq(userQuery.getGender() != null, SysUser::getGender, userQuery.getGender())
                .like(StringUtils.isNotBlank(userQuery.getMobile()), SysUser::getMobile, userQuery.getMobile());
        return this.page(new Page<>(userQuery.getPage(), userQuery.getLimit()), wrapper);
    }

    @Cacheable(value = "user", key = "#id")
    @Override
    public SysUser getCache(Long id) {
        return this.getById(id);
    }
}
