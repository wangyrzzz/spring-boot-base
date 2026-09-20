package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.common.ApiException;
import com.example.demo.entity.SysDept;
import com.example.demo.mapper.SysDeptMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DeptService extends ServiceImpl<SysDeptMapper, SysDept> {
    public Page<SysDept> page(long current, long size, String name, String code, Long parentId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<SysDept>()
                .like(StringUtils.hasText(name), SysDept::getName, name)
                .like(StringUtils.hasText(code), SysDept::getCode, code)
                .eq(parentId != null, SysDept::getParentId, parentId)
                .orderByAsc(SysDept::getId);
        return page(new Page<>(current, size), wrapper);
    }

    public List<SysDept> select(Long parentId) {
        return lambdaQuery().eq(parentId != null, SysDept::getParentId, parentId)
                .orderByAsc(SysDept::getId).list();
    }

    @Transactional
    public Long saveOrUpdateDept(SysDept dept) {
        if (dept == null || !StringUtils.hasText(dept.getName())) {
            throw new ApiException(400, "部门名称不能为空");
        }
        if (dept.getId() != null && dept.getId().equals(dept.getParentId())) {
            throw new ApiException(400, "部门不能以自身作为父级");
        }
        if (dept.getParentId() == null) {
            dept.setAncestors("");
        } else {
            SysDept parent = getById(dept.getParentId());
            if (parent == null) {
                throw new ApiException(400, "父部门不存在");
            }
            dept.setAncestors(StringUtils.hasText(parent.getAncestors())
                    ? parent.getAncestors() + "," + parent.getId() : String.valueOf(parent.getId()));
        }
        saveOrUpdate(dept);
        return dept.getId();
    }

    @Transactional
    public void removeDept(Long id) {
        if (id == null) {
            return;
        }
        if (lambdaQuery().eq(SysDept::getParentId, id).count() > 0) {
            throw new ApiException(400, "请先删除子部门");
        }
        removeById(id);
    }
}
