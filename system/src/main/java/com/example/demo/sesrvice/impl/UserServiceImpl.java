package com.example.demo.sesrvice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Page<User> page(UserQuery userQuery) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(StringUtils.isNotBlank(userQuery.getUsername()), User::getUsername, userQuery.getUsername())
                .like(StringUtils.isNotBlank(userQuery.getRealName()), User::getRealName, userQuery.getRealName())
                .eq(userQuery.getGender() != null, User::getGender, userQuery.getGender())
                .like(StringUtils.isNotBlank(userQuery.getMobile()), User::getMobile, userQuery.getMobile());
        return this.page(new Page<>(userQuery.getPage(), userQuery.getLimit()), wrapper);
    }

    @Cacheable(value = "user", key = "#id")
    @Override
    public User getCache(Long id) {
        return this.getById(id);
    }
}
