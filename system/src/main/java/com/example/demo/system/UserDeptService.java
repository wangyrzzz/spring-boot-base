package com.example.demo.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.example.demo.entity.SysUserDept;
import com.example.demo.mapper.SysUserDeptMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDeptService extends ServiceImpl<SysUserDeptMapper, SysUserDept> {
    public Long firstDeptId(Long userId) {
        SysUserDept row = lambdaQuery().eq(SysUserDept::getUserId, userId)
                .orderByAsc(SysUserDept::getId).last("limit 1").one();
        return row == null ? null : row.getDeptId();
    }

    @Transactional
    public void replace(Long userId, List<Long> deptIds) {
        remove(new LambdaQueryWrapper<SysUserDept>().eq(SysUserDept::getUserId, userId));
        if (deptIds == null || deptIds.isEmpty()) {
            return;
        }
        List<SysUserDept> rows = new ArrayList<>();
        for (Long deptId : deptIds.stream().filter(java.util.Objects::nonNull).distinct().toList()) {
            SysUserDept row = new SysUserDept();
            row.setUserId(userId);
            row.setDeptId(deptId);
            rows.add(row);
        }
        saveBatch(rows);
    }
}
