package com.example.demo.sesrvice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.example.demo.entity.User;
import com.example.demo.query.UserQuery;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author WangYuanrong
 * @since 2021-06-18
 */
public interface IUserService extends IService<User> {

    Page<User> page(UserQuery userQuery);

    User getCache(Long id);

}
