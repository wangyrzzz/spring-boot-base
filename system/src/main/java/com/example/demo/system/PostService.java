package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysPost;
import com.example.demo.mapper.SysPostMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PostService extends ServiceImpl<SysPostMapper, SysPost> {
    public Page<SysPost> page(long current, long size, String postCode, String postName, Integer status) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<SysPost>()
                .like(StringUtils.hasText(postCode), SysPost::getPostCode, postCode)
                .like(StringUtils.hasText(postName), SysPost::getPostName, postName)
                .eq(status != null, SysPost::getStatus, status)
                .orderByAsc(SysPost::getSort)
                .orderByDesc(SysPost::getId);
        return page(new Page<>(current, size), wrapper);
    }

    public List<SysPost> select() {
        return lambdaQuery().eq(SysPost::getStatus, 1).orderByAsc(SysPost::getSort).orderByDesc(SysPost::getId).list();
    }
}
